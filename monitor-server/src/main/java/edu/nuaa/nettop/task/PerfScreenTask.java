package edu.nuaa.nettop.task;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.common.obj.DevStatusObj;
import edu.nuaa.nettop.common.obj.LinkStatusObj;
import edu.nuaa.nettop.common.obj.PortStatusObj;
import edu.nuaa.nettop.common.response.BoDevStatus;
import edu.nuaa.nettop.common.response.BoLinkLrbStatus;
import edu.nuaa.nettop.common.response.BoLinkStatus;
import edu.nuaa.nettop.common.response.BoNetServStatus;
import edu.nuaa.nettop.common.response.BoPerfOptScreenStatus;
import edu.nuaa.nettop.common.response.BoRestResObj;
import edu.nuaa.nettop.common.utils.CommonUtils;
import edu.nuaa.nettop.common.utils.ProxyUtil;
import edu.nuaa.nettop.config.StaticConfig;
import edu.nuaa.nettop.model.LxdStatus;
import edu.nuaa.nettop.model.ServMem;
import edu.nuaa.nettop.vo.lxd.LxdRequest;
import edu.nuaa.nettop.vo.lxd.LxdResponse;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 57387
 * @title: PerfScreenTask
 * @projectName jlxd-monitor-new
 * @date 2020/9/2017:51
 */
@Slf4j
public class PerfScreenTask implements Job {

    private static final String URL = "http://" + StaticConfig.WEB_IP + ":" + StaticConfig.PORT + "/v2/netMonitorData/screen/perfopt";

    private Map<String, String> linkInfoMap;
    private Map<String, Integer> linkBandwidthMap;
    private String routerName;
    private String serverIp;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //设置参数
        BoPerfOptScreenStatus screenStatus = new BoPerfOptScreenStatus();
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String wlid = jobDataMap.getString("wlid");
        List<String> serverIps = (List<String>) jobDataMap.get("servers");
        linkInfoMap = (Map<String, String>) jobDataMap.get("links");
        linkBandwidthMap = (Map<String, Integer>) jobDataMap.get("bandwidth");
        routerName = jobDataMap.getString("router");
        serverIp = jobDataMap.getString("ip");
        serverIps.remove("192.168.31.12");
        log.info("Run Perf screen task-> {}", wlid);
        screenStatus.setWlid(wlid);
        //服务器总体资源利用率
        setServerData(screenStatus, serverIps);
        //路由器资源监控
        try {
            setRouterData(wlid, screenStatus);
        } catch (MonitorException e) {
            throw new JobExecutionException(e.getMessage());
        }
        //链路流量吞吐量
        setLinkData(wlid, screenStatus);
        //setTM
        setTm(wlid, screenStatus);
        log.info("perf screen data->{}", JSON.toJSONString(screenStatus));
        //提交数据
        sendToWeb(screenStatus);
    }

    private void setRouterData(String wlid, BoPerfOptScreenStatus screenStatus) throws MonitorException {
        //创建请求
        LxdRequest request = new LxdRequest();
        request.getDeviceList().add(routerName);
        //获取状态数据
        LxdResponse response = ProxyUtil.getLxdStatus(serverIp, request);
        //构造返回数据
        Set<LxdStatus> lxdStatusSet = response.getLxdStatuses();
        for (LxdStatus lxdStatus : lxdStatusSet) {
            DevStatusObj devStatusObj = new DevStatusObj();
            if (lxdStatus.getStatus() == 0) {
                devStatusObj.setMem(0);
                devStatusObj.setMemPk(0);
                devStatusObj.setCpu(0);
                devStatusObj.setDk(0);
                devStatusObj.setPorts(null);
                devStatusObj.setSt((short) 0);
            } else {
                devStatusObj.setSt((short) 1);
                devStatusObj.setMemPk(lxdStatus.getMemUsagePeak());
                devStatusObj.setMem(lxdStatus.getMemUsage());
                devStatusObj.setDk(lxdStatus.getDiskUsage());
                //读取redis数据
                String cache = CommonUtils.getFromRedis(wlid + routerName);
                if (cache == null || cache.equals("")) {
                    devStatusObj.setCpu(0);
                } else {
                    long oldCpuTime = Long.parseLong(cache);
                    long newCpuTime = lxdStatus.getCpuTime();
                    if (newCpuTime < oldCpuTime) {
                        devStatusObj.setCpu((Long.MAX_VALUE - oldCpuTime + newCpuTime) / 1_000_000f / 10_200f * 100);
                    } else {
                        double cpu = (newCpuTime - oldCpuTime) / 1_000_000f / 10_200f * 100;
                        devStatusObj.setCpu(cpu);
                    }
                }
            }
            //存入redis
            CommonUtils.storeToRedis(wlid + routerName, String.valueOf(lxdStatus.getCpuTime()));
            List<PortStatusObj> ports = lxdStatus.getInterfaceList().stream().map(portModel -> {
                PortStatusObj port = new PortStatusObj();
                port.setNm(portModel.getName());
                port.setSt((short) portModel.getStatus());
                port.setBr(portModel.getByteRecv());
                port.setBs(portModel.getByteSent());
                port.setPr(portModel.getPktRecv());
                port.setPs(portModel.getPktSent());
                return port;
            }).collect(Collectors.toList());
            devStatusObj.setPorts(ports);
            screenStatus.setRouterstatus(new BoDevStatus(devStatusObj));
        }
    }

    private void setTm(String wlid, BoPerfOptScreenStatus screenStatus) {
        String tmString;
        if ((tmString = CommonUtils.getFromRedis(wlid + "perftm")) == null) {
            CommonUtils.storeToRedis(wlid + "perftm", "5");
            screenStatus.setTm(0);
        } else {
            int tm = Integer.parseInt(tmString);
            screenStatus.setTm(tm);
            CommonUtils.storeToRedis(wlid + "perftm", String.valueOf(tm + 5));
        }
    }

    private void setLinkData(String wlid, BoPerfOptScreenStatus screenStatus) {
        //从redis读取数据
        long start = System.currentTimeMillis();
        List<LinkStatusObj> linkStatusObjList = JSON.parseArray(
                CommonUtils.getFromRedis(wlid),
                LinkStatusObj.class);
        linkStatusObjList.sort(Comparator.comparingLong(LinkStatusObj::getTp));
        List<BoLinkStatus> linkStatuses = new ArrayList<>();
        int length = linkStatusObjList.size();
        for (int i = 0; i < 5; i++) {
            BoLinkStatus boLinkStatus = new BoLinkStatus();
            boLinkStatus.setId(linkStatusObjList.get(i).getId());
            boLinkStatus.setMc(linkInfoMap.get(boLinkStatus.getId()));
            boLinkStatus.setSt((byte) 1);
//            boLinkStatus.setTp(String.valueOf(linkStatusObjList.get(length - 1).getTp()));
            int ran3 = (int) (Math.random()*1000);
            boLinkStatus.setTp(String.valueOf(ran3));
            linkStatuses.add(boLinkStatus);
            length--;
        }
        screenStatus.setLinks(linkStatuses);
        //链路容量比
        for (BoLinkStatus linkStatus : linkStatuses) {
            BoLinkLrbStatus linkLrbStatus = new BoLinkLrbStatus();
            String llid = linkStatus.getId();
            linkLrbStatus.setLlid(llid);
            linkLrbStatus.setMc(linkStatus.getMc());
            linkLrbStatus.setRate(
                    Double.parseDouble(linkStatus.getTp()) /
                            linkBandwidthMap.get(llid)
            );
            screenStatus.getRlinks().add(linkLrbStatus);
        }
        log.debug("links times: {}", System.currentTimeMillis() - start);
    }

    private void setServerData(BoPerfOptScreenStatus status, List<String> serverIps) {
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
        servStatus.setMem(memTotalUsed >> 20); //GB
        servStatus.setMeml((double) memTotalUsed / memTotal);
        status.setSevstatus(servStatus);
    }

    private void sendToWeb(BoPerfOptScreenStatus screenStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoPerfOptScreenStatus> entity = new HttpEntity<>(screenStatus, headers);

        RestTemplate restTemplate = new RestTemplate();
        BoRestResObj boRestResObj = restTemplate.postForObject(URL, entity, BoRestResObj.class);
        assert boRestResObj != null;
        if (boRestResObj.getOptres() == 1) {
            log.info("POST PerfScreen Data to " + URL + " success");
        } else {
            log.error("POST failed:" + boRestResObj.getMsg());
        }
    }
}
