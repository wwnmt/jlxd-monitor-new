package edu.nuaa.nettop.task;

import edu.nuaa.nettop.common.response.BoNetServStatus;
import edu.nuaa.nettop.common.response.BoPerfOptScreenStatus;
import edu.nuaa.nettop.common.response.BoRestResObj;
import edu.nuaa.nettop.common.response.BoVirtRealConnScreenStatus;
import edu.nuaa.nettop.common.utils.ProxyUtil;
import edu.nuaa.nettop.config.StaticConfig;
import edu.nuaa.nettop.model.ServMem;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 57387
 * @title: PerfScreenTask
 * @projectName jlxd-monitor-new
 * @date 2020/9/2017:51
 */
@Slf4j
public class PerfScreenTask implements Job {

    private static final String URL = "http://" + StaticConfig.WEB_IP + ":" + StaticConfig.PORT + "/v2/netMonitorData/screen/perfopt";


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Run Perf screen task");

        BoPerfOptScreenStatus screenStatus = new BoPerfOptScreenStatus();
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String wlid = jobDataMap.getString("wlid");
        List<String> serverIps = (List<String>) jobDataMap.get("servers");

        //服务器总体资源利用率
        setServerData(screenStatus, serverIps);

        sendToWeb(screenStatus);
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
            if (mem.getShared() + mem.getBuffer() + mem.getCache() > mem.getTotal())
                memTotalUsed += mem.getTotal() - mem.getAvail() - mem.getBuffer() - mem.getCache() + mem.getShared();
            else
                memTotalUsed += mem.getTotal() - mem.getAvail() - mem.getBuffer() - mem.getCache();
        }
        servStatus.setMem(memTotalUsed >> 20);
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
