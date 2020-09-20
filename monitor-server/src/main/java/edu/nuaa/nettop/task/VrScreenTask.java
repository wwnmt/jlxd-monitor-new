package edu.nuaa.nettop.task;

import edu.nuaa.nettop.common.response.BoVirtRealConnScreenStatus;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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

        //服务器总体资源利用率

        //服务器端口监控数据

        //端口流量吞吐量

        //提交数据
    }
}
