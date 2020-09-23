package edu.nuaa.nettop.task;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.common.constant.Constants;
import edu.nuaa.nettop.common.obj.LinkStatusObj;
import edu.nuaa.nettop.common.obj.ServerReqObj;
import edu.nuaa.nettop.common.response.BoDdosScreenStatus;
import edu.nuaa.nettop.common.response.BoLinkStatus;
import edu.nuaa.nettop.common.response.BoNetServStatus;
import edu.nuaa.nettop.common.response.BoRestResObj;
import edu.nuaa.nettop.common.response.BoServerIntfStatus;
import edu.nuaa.nettop.common.response.BoVirtRealConnScreenStatus;
import edu.nuaa.nettop.common.utils.CommonUtils;
import edu.nuaa.nettop.common.utils.ProxyUtil;
import edu.nuaa.nettop.config.StaticConfig;
import edu.nuaa.nettop.model.ServMem;
import edu.nuaa.nettop.model.ServPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 57387
 * @title: VrScreenTask
 * @projectName jlxd-monitor-new
 * @date 2020/9/2017:44
 */
@Slf4j
public class VrScreenTask implements Job {

    private static final String URL = "http://" + StaticConfig.WEB_IP + ":" + StaticConfig.PORT + "/v2/netMonitorData/screen/virtreal";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Run Vr screen task");
        BoVirtRealConnScreenStatus screenStatus = new BoVirtRealConnScreenStatus();
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String wlid = jobDataMap.getString("wlid");
        List<String> serverIps = (List<String>) jobDataMap.get("servers");
        serverIps.remove("192.168.31.12");
        serverIps.remove("192.168.31.14");
        screenStatus.setWlid(wlid);
        //服务器总体资源利用率
        setServerData(screenStatus, serverIps);
        //服务器端口监控数据
        setServerPortData(screenStatus, serverIps, wlid);
        //set TM
        String tmString;
        if ((tmString = CommonUtils.getFromRedis(wlid+"vrtm")) == null) {
            CommonUtils.storeToRedis(wlid+"vrtm", "5");
            screenStatus.setTm(0);
        } else {
            int tm = Integer.parseInt(tmString);
            screenStatus.setTm(tm);
            CommonUtils.storeToRedis(wlid+"vrtm", String.valueOf(tm+5));
        }
        log.info("vr screen data->{}", JSON.toJSONString(screenStatus));
        //提交数据
        sendToWeb(screenStatus);
    }

    private void sendToWeb(BoVirtRealConnScreenStatus screenStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoVirtRealConnScreenStatus> entity = new HttpEntity<>(screenStatus, headers);

        RestTemplate restTemplate = new RestTemplate();
        BoRestResObj boRestResObj = restTemplate.postForObject(URL, entity, BoRestResObj.class);
        assert boRestResObj != null;
        if (boRestResObj.getOptres() == 1) {
            log.info("POST VrScreen Data to " + URL + " success");
        } else {
            log.error("POST failed:" + boRestResObj.getMsg());
        }
    }

    private void setServerPortData(BoVirtRealConnScreenStatus screenStatus, List<String> serverIps, String wlid) {
        List<BoServerIntfStatus> serverIntfStatuses = new ArrayList<>();
        for (String serverIp : serverIps) {
            ServPort servPort = ProxyUtil.getServPort(serverIp, serverIp);
            for (ServPort.Port port : servPort.getPorts()) {
                if (port.getDkmc().equals("lo"))
                    continue;
                BoServerIntfStatus intfStatus = new BoServerIntfStatus();
                intfStatus.setMc(port.getMc());
                intfStatus.setDkmc(port.getDkmc());
                intfStatus.setIp(port.getIp());
                intfStatus.setZt(port.getZt());
                String cache;
                if ((cache = CommonUtils.getFromRedis(port.getMc()+port.getDkmc())) != null) {
                    String[] caches = cache.split("-");
                    long oldRecv = Long.parseLong(caches[0]);
                    long oldSend = Long.parseLong(caches[1]);
                    long oldData = Math.max(oldRecv, oldSend);
                    long newData = Math.max(Long.parseLong(port.getRecvBytes()),
                                            Long.parseLong(port.getSendBytes()));
                    long tp = newData < oldData ?
                            (Long.MAX_VALUE - oldData + newData) * 8 / 5 :
                            (newData - oldData) * 8 / 5;
//                    tp = Math.round(tp);
//                    if (tp < 524_288)
//                        intfStatus.setTp("0");
//                    else
//                        intfStatus.setTp(String.valueOf(tp >>> 20));
                    intfStatus.setTp(tp / 1000);
                } else {
                    intfStatus.setTp(0L);
                    CommonUtils.storeToRedis(port.getMc()+port.getDkmc(),
                                             port.getRecvBytes()+"-"+port.getSendBytes());
                }
                screenStatus.getSdks().add(intfStatus);
                serverIntfStatuses.add(intfStatus);
            }
        }
        //排序
        serverIntfStatuses.sort(Comparator.comparingLong(BoServerIntfStatus::getTp));
        int length = serverIntfStatuses.size();
        List<ServerReqObj> serverReqObjs = Constants.servIntfMap.get(wlid);
        if (serverReqObjs != null) {
            int size = serverReqObjs.size();
            for (int i = 0; i < 3-size; i++) {
                screenStatus.getTopsdks().add(
                        serverIntfStatuses.get(length-1)
                );
                length--;
            }
            for (ServerReqObj serverReqObj : serverReqObjs) {
                screenStatus.getTopsdks().add(
                        findIntfStatisByName(serverIntfStatuses,
                                             serverReqObj.getSmc(),
                                             serverReqObj.getPmc())
                );
            }
        } else {
            for (int i = 0; i < 3; i++) {
                screenStatus.getTopsdks().add(
                        serverIntfStatuses.get(length-1)
                );
                length--;
            }
        }
    }

    private BoServerIntfStatus findIntfStatisByName(List<BoServerIntfStatus> list,
                                                    String serverName,
                                                    String intfName) {
        for (BoServerIntfStatus intfStatus : list) {
            if (intfStatus.getMc().equals(serverName) && intfStatus.getDkmc().equals(intfName))
                return intfStatus;
        }
        return null;
    }

    private void setServerData(BoVirtRealConnScreenStatus status, List<String> serverIps) {
        BoNetServStatus servStatus = new BoNetServStatus();
        servStatus.setServnum(serverIps.size());
        Map<String, Double> cpuMap = new HashMap<>();
        Map<String, ServMem> memMap = new HashMap<>();

        //读取服务器状态数据
        for (String serverIp : serverIps) {
            ServMem servMem = ProxyUtil.getAllMem(serverIp, serverIp);
            String cpu = ProxyUtil.getCpu(serverIp, serverIp);
            cpuMap.put(serverIp, Double.valueOf(cpu));
            memMap.put(serverIp, servMem);
        }
        //cpu?
        servStatus.setCpu(20);
        //cpu usage
        double cpuTotalUsage = cpuMap.values().stream().mapToDouble(v -> v).sum() / cpuMap.size();
        servStatus.setCpul(cpuTotalUsage);
        //mem total
        long memTotal = memMap.values().stream().mapToLong(ServMem::getTotal).sum();
        //mem usage
        long memTotalUsed = 0L;
        for (ServMem mem : memMap.values()) {
            if (mem.getShared() + mem.getBuffer() + mem.getCache() > mem.getTotal())
                memTotalUsed += mem.getTotal() - mem.getAvail() - mem.getBuffer() - mem.getCache() + mem.getShared();
            else
                memTotalUsed += mem.getTotal() - mem.getAvail() - mem.getBuffer() - mem.getCache();
        }
        servStatus.setMem(memTotalUsed >> 20);
        servStatus.setMeml((double) memTotalUsed / memTotal);
        status.setSevstatus(servStatus);
    }
}
