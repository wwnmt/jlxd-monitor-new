package edu.nuaa.nettop.task;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.common.constant.Constants;
import edu.nuaa.nettop.common.obj.ServerReqObj;
import edu.nuaa.nettop.common.response.BoNetServStatus;
import edu.nuaa.nettop.common.response.BoRestResObj;
import edu.nuaa.nettop.common.response.BoServerIntfStatus;
import edu.nuaa.nettop.common.response.BoVirtRealConnScreenStatus;
import edu.nuaa.nettop.common.utils.CommonUtils;
import edu.nuaa.nettop.common.utils.ProxyUtil;
import edu.nuaa.nettop.config.StaticConfig;
import edu.nuaa.nettop.model.ServMem;
import edu.nuaa.nettop.model.ServPort;
import edu.nuaa.nettop.vo.VrScreenRequest;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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
        List<VrScreenRequest.VrPort> portList = (List<VrScreenRequest.VrPort>) jobDataMap.get("vrPort");
        serverIps.remove("192.168.31.12");
        screenStatus.setWlid(wlid);
        //服务器总体资源利用率
        setServerData(screenStatus, serverIps);
        //服务器端口监控数据
        setServerPortData(screenStatus, serverIps, wlid, portList);
        //set TM
        String tmString;
        if ((tmString = CommonUtils.getFromRedis(wlid + "vrtm")) == null) {
            CommonUtils.storeToRedis(wlid + "vrtm", "5");
            screenStatus.setTm(0);
        } else {
            int tm = Integer.parseInt(tmString);
            screenStatus.setTm(tm);
            CommonUtils.storeToRedis(wlid + "vrtm", String.valueOf(tm + 5));
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

    private BoServerIntfStatus createIntfStatus(ServPort.Port port) {
        BoServerIntfStatus intfStatus = new BoServerIntfStatus();
        intfStatus.setMc(port.getMc());
        intfStatus.setDkmc(port.getDkmc());
        intfStatus.setIp(port.getIp());
        intfStatus.setZt(port.getZt());
        String cache;
        if ((cache = CommonUtils.getFromRedis(port.getMc() + port.getDkmc())) != null) {
            String[] caches = cache.split("-");
            long oldRecv = Long.parseLong(caches[0]);
            long oldSend = Long.parseLong(caches[1]);
            long newRecv = Long.parseLong(port.getRecvBytes());
            long newSend = Long.parseLong(port.getSendBytes());
            long tpSend = newSend < oldSend ?
//                    (Long.MAX_VALUE - oldSend + newSend) / 5 :
                    0L :
                    (newSend - oldSend) / 5;
            long tpRecv = newRecv < oldRecv ?
//                    (Long.MAX_VALUE - oldRecv + newRecv) / 5 :
                    0L :
                    (newRecv - oldRecv) / 5;
            long tp = Math.max(tpRecv, tpSend);
            if (tp < 0) {
                tp = 0L;
            }
            intfStatus.setTp(tp / 125);
        } else {
            intfStatus.setTp(0L);
        }
        CommonUtils.storeToRedis(port.getMc() + port.getDkmc(),
                                 port.getRecvBytes() + "-" + port.getSendBytes());
        return intfStatus;
    }

    private void setServerPortData(BoVirtRealConnScreenStatus screenStatus, List<String> serverIps, String wlid,
                                   List<VrScreenRequest.VrPort> portList) {
        List<BoServerIntfStatus> serverIntfStatuses = new ArrayList<>();
        //获取服务器上基本端口数据
        for (String serverIp : serverIps) {
            ServPort servPort = ProxyUtil.getServPort(serverIp, serverIp);
            for (ServPort.Port port : servPort.getPorts()) {
                BoServerIntfStatus intfStatus = createIntfStatus(port);
                if ((intfStatus.getDkmc().startsWith("e") || intfStatus.getDkmc().startsWith("w"))
                        && intfStatus.getZt() == 1) {
                    serverIntfStatuses.add(intfStatus);
                }
            }
        }
        List<BoServerIntfStatus> vrIntfStatus = new ArrayList<>();
        //获取虚实互联数据填充
        for (VrScreenRequest.VrPort vrPort : portList) {
            ServPort servPort = ProxyUtil.getServPort(vrPort.getServerIp(), vrPort.getDeviceIp());
            for (ServPort.Port port : servPort.getPorts()) {
                if (port.getDkmc().equals(vrPort.getVPort())) {
                    BoServerIntfStatus intfStatus = createIntfStatus(port);
                    intfStatus.setDkmc(vrPort.getPPort());
                    intfStatus.setMc(vrPort.getServerIp());
                    vrIntfStatus.add(intfStatus);
                }
            }
        }
        //使用用户指定端口数据填充sdks
        List<ServerReqObj> serverReqObjs = Constants.servIntfMap.get(wlid);
        if (serverReqObjs != null && serverReqObjs.size() > 0) {
            for (ServerReqObj serverReqObj : serverReqObjs) {
                BoServerIntfStatus intfStatus = findIntfStatisByName(vrIntfStatus,
                                                                     serverReqObj.getSmc(),
                                                                     serverReqObj.getPmc());
                if (intfStatus == null)
                    intfStatus = findIntfStatisByName(serverIntfStatuses,
                                                      serverReqObj.getSmc(),
                                                      serverReqObj.getPmc());
                if (intfStatus != null) {
                    screenStatus.getSdks().add(intfStatus);
                }
                if (screenStatus.getSdks().size() >= 3)
                    break;
            }
        } else {
            for (BoServerIntfStatus intfStatus : vrIntfStatus) {
                screenStatus.getSdks().add(intfStatus);
                if (screenStatus.getSdks().size() >= 3)
                    break;
            }
            if (screenStatus.getSdks().size() < 3) {
                for (BoServerIntfStatus intfStatus : serverIntfStatuses) {
                    screenStatus.getSdks().add(intfStatus);
                    if (screenStatus.getSdks().size() >= 3)
                        break;
                }
            }
        }
        //排序
        serverIntfStatuses.sort(Comparator.comparingLong(BoServerIntfStatus::getTp));
        int length = serverIntfStatuses.size();
        int n = Math.min(length, 5);
        for (int i = 0; i < n; i++) {
            screenStatus.getTopsdks().add(serverIntfStatuses.get(length - 1));
            length--;
        }
    }

    private BoServerIntfStatus findIntfStatisByName(List<BoServerIntfStatus> list,
                                                    String serverName,
                                                    String intfName) {
        for (BoServerIntfStatus intfStatus : list) {
            if (intfStatus.getMc().equals(serverName) && intfStatus.getDkmc().equals(intfName)) {
                return intfStatus;
            }
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
            if (mem.getShared() + mem.getBuffer() + mem.getCache() > mem.getTotal()) {
                memTotalUsed += mem.getTotal() - mem.getAvail() - mem.getBuffer() - mem.getCache() + mem.getShared();
            } else {
                memTotalUsed += mem.getTotal() - mem.getAvail() - mem.getBuffer() - mem.getCache();
            }
        }
        servStatus.setMem(memTotalUsed >> 20);
        servStatus.setMeml((double) memTotalUsed / memTotal);
        status.setSevstatus(servStatus);
    }
}
