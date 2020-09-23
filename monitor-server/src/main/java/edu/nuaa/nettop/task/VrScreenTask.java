package edu.nuaa.nettop.task;

import edu.nuaa.nettop.common.response.BoNetServStatus;
import edu.nuaa.nettop.common.response.BoVirtRealConnScreenStatus;
import edu.nuaa.nettop.common.utils.ProxyUtil;
import edu.nuaa.nettop.model.ServMem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Run Vr screen task");
        BoVirtRealConnScreenStatus screenStatus = new BoVirtRealConnScreenStatus();
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String wlid = jobDataMap.getString("wlid");
        List<String> serverIps = (List<String>) jobDataMap.get("servers");
        screenStatus.setWlid(wlid);
        //服务器总体资源利用率
        setServerData(screenStatus, serverIps);
        //服务器端口监控数据

        //端口流量吞吐量

        //提交数据
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
        servStatus.setMem(memTotal);
        //mem usage
        long memTotalUsed = 0L;
        for (ServMem mem : memMap.values()) {

        }
        status.setSevstatus(servStatus);
    }
}
