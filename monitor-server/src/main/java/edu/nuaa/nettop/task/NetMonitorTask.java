package edu.nuaa.nettop.task;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.common.model.Link;
import edu.nuaa.nettop.common.model.Node;
import edu.nuaa.nettop.common.obj.LinkStatusObj;
import edu.nuaa.nettop.common.obj.NetStatusObj;
import edu.nuaa.nettop.common.response.BoNetStatus;
import edu.nuaa.nettop.common.response.BoRestResObj;
import edu.nuaa.nettop.utils.CommonUtils;
import edu.nuaa.nettop.utils.ProxyUtil;
import edu.nuaa.nettop.config.StaticConfig;
import edu.nuaa.nettop.model.LxdStatus;
import edu.nuaa.nettop.model.PortStatus;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 17:48
 */
@Slf4j
public class NetMonitorTask implements Job {

    private static final String URL = "http://" + StaticConfig.WEB_IP + ":" + StaticConfig.PORT + "/v2/netMonitorData";
    private List<Node> nodes;
    private List<Link> links;
    private Set<String> errDevs = new HashSet<>();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String wlid = jobDataMap.getString("wlid");
        nodes = JSON.parseArray(jobDataMap.getString("nodes"), Node.class);
        links = JSON.parseArray(jobDataMap.getString("links"), Link.class);
        Map<String, LxdRequest> serverNodeMap = new HashMap<>();
        for (Node node : nodes) {
            String serverIp = node.getServerIp();
            LxdRequest request = CommonUtils.getOrCreate(
                    serverIp,
                    serverNodeMap,
                    LxdRequest::new
            );
            request.getDeviceList().add(node.getName());
        }
        try {
            List<LxdStatus> lxdStatusList = new ArrayList<>();
            for (Map.Entry<String, LxdRequest> entry : serverNodeMap.entrySet()) {
                LxdResponse response = ProxyUtil.getLxdStatus(entry.getKey(), entry.getValue());
                lxdStatusList.addAll(response.getLxdStatuses());
            }
            Map<String, Long> oldDataMap = getData(lxdStatusList);
            lxdStatusList.clear();
            //间隔5秒
            long startTime = System.currentTimeMillis();
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            //获取第二次状态数据
            for (Map.Entry<String, LxdRequest> entry : serverNodeMap.entrySet()) {
                LxdResponse response = ProxyUtil.getLxdStatus(entry.getKey(), entry.getValue());
                errDevs = response.getErrDevs();
                lxdStatusList.addAll(response.getLxdStatuses());
            }

            Map<String, LxdStatus> lxdStatusMap = new HashMap<>();
            for (LxdStatus lxdStatus : lxdStatusList) {
                lxdStatusMap.put(lxdStatus.getName(), lxdStatus);
            }

            Map<String, Long> newDataMap = getData(lxdStatusList);

            //计算真正的间隔时间
            long endTime = System.currentTimeMillis();
            double interval = (endTime - startTime) / 1_000D;

            //计算吞吐量
            List<LinkStatusObj> linkStatusObjList = new ArrayList<>();
            newDataMap.keySet().forEach((linkId) -> {
                //计算流量吞吐量
                double throughput = calculatorThroughput(newDataMap.get(linkId),
                                                         oldDataMap.get(linkId),
                                                         interval);
                LinkStatusObj linkStatusObj = new LinkStatusObj();
                linkStatusObj.setLlid(linkId);
                Link link = findLinkByLinkId(linkId);
                if (link == null) {
                    linkStatusObj.setSt((byte) 0);
                    linkStatusObj.setTp(0);
                } else {
                    linkStatusObj.setMc(link.getName());
                    //校验容器启动状态
                    if (errDevs.contains(link.getFrom()) || errDevs.contains(link.getTo())) {
                        linkStatusObj.setSt((byte) 0);
                    } else {
                        linkStatusObj.setSt((byte) 1);
                    }
                    //校验容器接口状态
                    if (checkPortStatus(lxdStatusMap, link))
                        linkStatusObj.setSt((byte) 1);
                    else
                        linkStatusObj.setSt((byte) 0);

                    long tp = Math.round(throughput);
                    if (tp < 524_288) {
                        linkStatusObj.setTp(0);
                    } else {
                        linkStatusObj.setTp(tp >>> 20);
                    }
                }
                linkStatusObjList.add(linkStatusObj);
            });
            //存入redis
            CommonUtils.storeToRedis(wlid, JSON.toJSONString(linkStatusObjList));

            // 推送到前台
            NetStatusObj netStatusObj = new NetStatusObj();
            netStatusObj.setWlid(wlid);
            netStatusObj.setLinks(linkStatusObjList);
            List<String> errDev = errDevs.parallelStream()
                    .map(this::findSbidByName)
                    .collect(Collectors.toList());
            netStatusObj.setErrordevs(errDev);

            String tmString;
            if ((tmString = CommonUtils.getFromRedis(wlid + "tm")) == null) {
                CommonUtils.storeToRedis(wlid + "tm", "5");
                netStatusObj.setTm(0);
            } else {
                int tm = Integer.parseInt(tmString);
                netStatusObj.setTm(tm);
                CommonUtils.storeToRedis(wlid + "tm", String.valueOf(tm + 5));
            }
            BoNetStatus boNetStatus = new BoNetStatus(netStatusObj);
            log.info("{} Data-> {}", wlid, JSON.toJSONString(boNetStatus));
            sendToWeb(boNetStatus);

            errDevs.clear();
        } catch (MonitorException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPortStatus(Map<String, LxdStatus> lxdStatusMap, Link link) {
        LxdStatus from = lxdStatusMap.get(link.getFrom());
        LxdStatus to = lxdStatusMap.get(link.getTo());
        if (from == null || to == null)
            return false;
        int count = 0;
        for (PortStatus portStatus : from.getInterfaceList()) {
            if (portStatus.getName().equals(link.getFromPort()))
                if (portStatus.getStatus() == 1)
                    count++;
        }
        for (PortStatus portStatus : to.getInterfaceList()) {
            if (portStatus.getName().equals(link.getToPort()))
                if (portStatus.getStatus() == 1)
                    count++;
        }
        return count == 2;
    }

    private void sendToWeb(BoNetStatus boNetStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoNetStatus> entity = new HttpEntity<>(boNetStatus, headers);

        RestTemplate restTemplate = new RestTemplate();
        BoRestResObj boRestResObj = restTemplate.postForObject(URL, entity, BoRestResObj.class);
        assert boRestResObj != null;
        if (boRestResObj.getOptres() == 1) {
            log.info("POST netData to " + URL + " success");
        } else {
            log.error("POST failed:" + boRestResObj.getMsg());
        }
    }


    private Map<String, Long> getData(List<LxdStatus> list) {
        Map<String, Long> dataMap = new HashMap<>();
        for (Link link : links) {
            long fromData = getPortOctet(list, link.getFrom(), link.getFromPort());
            long toData = getPortOctet(list, link.getTo(), link.getToPort());
            if (fromData == 0) {
                dataMap.put(link.getLinkId(), toData);
            } else if (toData == 0) {
                dataMap.put(link.getLinkId(), fromData);
            } else if (fromData == -1 || toData == -1) {
                dataMap.put(link.getLinkId(), 0L);
            } else {
                dataMap.put(link.getLinkId(), (fromData + toData) / 2);
            }
        }
        return dataMap;
    }

    private long getPortOctet(List<LxdStatus> list, String node, String port) {
        Optional<LxdStatus> optional = list.stream()
                .filter(lxdInfoModel -> lxdInfoModel.getName().equals(node))
                .findFirst();
        if (optional.isPresent()) {
            Optional<PortStatus> optional1 = optional.get().getInterfaceList().stream()
                    .filter(interfaceModel -> interfaceModel.getName().equals(port))
                    .findFirst();
            if (optional1.isPresent()) {
                PortStatus interfaceModel = optional1.get();
                return StrictMath.max(interfaceModel.getByteRecv(), interfaceModel.getByteSent());
            }
            return 0;
        }
        return -1;
    }

    private double calculatorThroughput(double newData, double oldData, double interval) {
        return newData < oldData ?
                (Long.MAX_VALUE - oldData + newData) * 8 / interval :
                (newData - oldData) * 8 / interval;
    }

    private String findSbidByName(String name) {
        for (Node node : nodes) {
            if (node.getName().equals(name)) {
                return node.getNodeId();
            }
        }
        return null;
    }

    private Link findLinkByLinkId(String linkId) {
        for (Link link : links) {
            if (link.getLinkId().equals(linkId)) {
                return link;
            }
        }
        return null;
    }
}
