package edu.nuaa.nettop.task;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.common.obj.DevStatusObj;
import edu.nuaa.nettop.common.obj.PortStatusObj;
import edu.nuaa.nettop.common.response.BoDevStatus;
import edu.nuaa.nettop.common.response.BoRestResObj;
import edu.nuaa.nettop.common.utils.CommonUtils;
import edu.nuaa.nettop.common.utils.ProxyUtil;
import edu.nuaa.nettop.config.StaticConfig;
import edu.nuaa.nettop.model.LxdStatus;
import edu.nuaa.nettop.vo.lxd.LxdRequest;
import edu.nuaa.nettop.vo.lxd.LxdResponse;
import lombok.SneakyThrows;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 18:17
 */
@Slf4j
public class NodeMonitorTask implements Job {

    private static final String URL = "http://" + StaticConfig.WEB_IP + ":" + StaticConfig.PORT + "/v2/devMonitorData";

    private String sbid;

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //读取参数
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String wlid = jobDataMap.getString("wlid");
        sbid = jobDataMap.getString("sbid");
        String nodeName = jobDataMap.getString("nodeName");
        String serverIp = jobDataMap.getString("serverIp");
        //创建请求
        LxdRequest request = new LxdRequest();
        request.getDeviceList().add(nodeName);
        //获取状态数据
        LxdResponse response = ProxyUtil.getLxdStatus(serverIp, request);
        //构造返回数据
        Set<LxdStatus> lxdStatusSet = response.getLxdStatuses();
        BoDevStatus boDevStatus = null;
        if (CollectionUtils.isNotEmpty(lxdStatusSet)) {
            for (LxdStatus lxdStatus : lxdStatusSet) {
                boDevStatus = createStatusData(lxdStatus);
            }
        } else {
            DevStatusObj devStatusObj = new DevStatusObj();
            devStatusObj.setMem(0);
            devStatusObj.setMemPk(0);
            devStatusObj.setCpu(0);
            devStatusObj.setDk(0);
            devStatusObj.setPorts(null);
            devStatusObj.setSt((short) 0);
            boDevStatus = new BoDevStatus(devStatusObj);
        }

        log.info("Data-> {}", JSON.toJSONString(boDevStatus));
        //发送数据
        sendToWeb(boDevStatus);
    }

    private void sendToWeb(BoDevStatus boDevStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoDevStatus> entity = new HttpEntity<>(boDevStatus, headers);

        RestTemplate restTemplate = new RestTemplate();
        BoRestResObj boRestResObj = restTemplate.postForObject(URL, entity, BoRestResObj.class);
        assert boRestResObj != null;
        if (boRestResObj.getOptres() == 1) {
            log.info("POST devData to " + URL + " success");
        } else {
            log.error("POST failed:" + boRestResObj.getMsg());
        }
    }

    private BoDevStatus createStatusData(LxdStatus lxdStatus) {
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
            String cache = CommonUtils.getFromRedis(sbid);
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
        CommonUtils.storeToRedis(sbid, String.valueOf(lxdStatus.getCpuTime()));
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
        String tmString;
        if ((tmString = CommonUtils.getFromRedis(sbid + "tm")) == null) {
            CommonUtils.storeToRedis(sbid + "tm", "5");
            devStatusObj.setTm(0);
        } else {
            int tm = Integer.parseInt(tmString);
            devStatusObj.setTm(tm);
            CommonUtils.storeToRedis(sbid + "tm", String.valueOf(tm + 5));
        }
        return new BoDevStatus(devStatusObj);
    }
}
