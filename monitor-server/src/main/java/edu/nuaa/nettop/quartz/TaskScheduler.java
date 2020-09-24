package edu.nuaa.nettop.quartz;

import edu.nuaa.nettop.common.exception.MonitorException;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 18:31
 */
@Slf4j
@Component
public class TaskScheduler {

    private final Scheduler scheduler;

    @Autowired
    public TaskScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 停止一个Job
     */
    @Transactional(rollbackFor = MonitorException.class)
    public void cancelTask(String jobName, String jobGroup) throws MonitorException {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobName, jobGroup));
            log.info("Cancel {} Task-> {}", jobGroup, jobName);
        } catch (Exception e) {
            throw new MonitorException(e.getMessage());
        }
    }

    /**
     * 删除一个Job
     */
    @Transactional(rollbackFor = MonitorException.class)
    public void deleteTask(String jobName, String jobGroup) throws MonitorException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        try {
            if (checkExists(jobName, jobGroup)) {
                scheduler.pauseTrigger(triggerKey); //停止触发器
                scheduler.unscheduleJob(triggerKey); //移除触发器
            }
            log.info("Delete {} Task-> {}", jobGroup, jobName);
        } catch (SchedulerException e) {
            throw new MonitorException(e.getMessage());
        }
    }

    /**
     * 提交一个任务
     */
    @Transactional(rollbackFor = MonitorException.class)
    public void publishJob(String jobName, String jobGroup,
                           JobDataMap jobDataMap, int timeout,
                           Class<? extends Job> clazz) throws MonitorException {
        try {
            //设置名称与组别
            JobDetail jobDetail = JobBuilder
                    .newJob(clazz)
                    .withIdentity(JobKey.jobKey(jobName, jobGroup))
                    .usingJobData(jobDataMap)
                    .build();
            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(TriggerKey.triggerKey(jobName, jobGroup))
                    .startAt(new Date())
                    .withSchedule(SimpleScheduleBuilder
                                          .simpleSchedule()
                                          .withMisfireHandlingInstructionIgnoreMisfires()
                                          .withIntervalInSeconds(timeout)
                                          .repeatForever())
                    .build();
            //提交任务
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("Publish '{}' Task-> {}", jobGroup, jobName);
        } catch (SchedulerException e) {
            throw new MonitorException(e.getMessage());
        }
    }

    /**
     * 验证Job是否存在
     */
    public boolean checkExists(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        return scheduler.checkExists(triggerKey);
    }
}
