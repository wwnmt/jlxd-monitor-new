package edu.nuaa.nettop.task;

import edu.nuaa.nettop.common.constant.Constants;
import edu.nuaa.nettop.entity.NodeDO;
import edu.nuaa.nettop.model.RoutingTable;
import edu.nuaa.nettop.utils.CommonUtils;
import edu.nuaa.nettop.utils.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RtTask implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Run routing task");
        long start = System.currentTimeMillis();
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String wlid = jobDataMap.getString("wlid");
        Map<String, String> nodeMap = (Map<String, String>) jobDataMap.get("nodeMap");
        Map<String, RoutingTable> routings = CommonUtils.getOrCreate(
                wlid,
                Constants.podRoutings,
                HashMap::new
        );

        for (Map.Entry<String, String> entry : nodeMap.entrySet()) {
            routings.put(entry.getKey(), ProxyUtil.getRoutingTable(entry.getValue(), entry.getKey()));
        }
        log.info("get routing tables time->{} ms", System.currentTimeMillis() - start);
    }
}
