package edu.nuaa.nettop.task;

import edu.nuaa.nettop.common.response.BoPerfOptScreenStatus;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author 57387
 * @title: PerfScreenTask
 * @projectName jlxd-monitor-new
 * @date 2020/9/2017:51
 */
@Slf4j
public class PerfScreenTask implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Run Perf screen task");

        BoPerfOptScreenStatus screenStatus = new BoPerfOptScreenStatus();

        //读取服务器资源信息
    }
}
