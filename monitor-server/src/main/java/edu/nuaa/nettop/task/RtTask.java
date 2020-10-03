package edu.nuaa.nettop.task;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.model.RoutingTable;
import edu.nuaa.nettop.utils.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-10-03
 * Time: 13:30
 */
@Slf4j
public class RtTask implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();

        String wlid = jobDataMap.getString("wlid");
        String serverIp = jobDataMap.getString("serverIp");
        String nodeName = jobDataMap.getString("nodeName");

        RoutingTable routingTable = ProxyUtil.getRoutingTable(serverIp, nodeName);
        log.info("{} routing table->{}", nodeName, JSON.toJSONString(routingTable));


    }
}
