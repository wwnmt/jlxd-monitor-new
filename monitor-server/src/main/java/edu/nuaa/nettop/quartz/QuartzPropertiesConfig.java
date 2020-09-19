package edu.nuaa.nettop.quartz;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-11
 * Time: 16:07
 */
@Data
@Component
public class QuartzPropertiesConfig {

    @Value("${quartz.scheduler.instance-name}")
    private String quartzSchedulerInstanceName;
    @Value("${quartz.scheduler.instance-id}")
    private String quartzSchedulerInstanceId;
    @Value("${quartz.scheduler.skip-update-check}")
    private String quartzSchedulerSkipUpdateCheck;
    @Value("${quartz.scheduler.job-factory.class}")
    private String quartzSchedulerJobFactoryClass;
    @Value("${quartz.job-store.class}")
    private String quartzJobStoreClass;
    @Value("${quartz.job-store.driver-delegate-class}")
    private String quartzJobStoreDriverDelegateClass;
    @Value("${quartz.job-store.datasource}")
    private String quartzJobStoreDatasource;
    @Value("${quartz.job-store.table-prefix}")
    private String quartzJobStoreTablePrefix;
    @Value("${quartz.job-store.is-clustered}")
    private String quartzJobStoreIsClustered;
    @Value("${quartz.thread-pool.class}")
    private String quartzThreadPoolClass;
    @Value("${quartz.thread-pool.thread-count}")
    private String quartzThreadPoolThreadCount;

    @Value("${quartz.datasource.quartzDataSource.driver}")
    private String quartzDatasourceQuartzDataSourceDriver;
    @Value("${quartz.datasource.quartzDataSource.url}")
    private String quartzDatasourceQuartzDataSourceUrl;
    @Value("${quartz.datasource.quartzDataSource.user}")
    private String quartzDatasourceQuartzDataSourceUser;
    @Value("${quartz.datasource.quartzDataSource.password}")
    private String quartzDatasourceQuartzDataSourcePassword;
    @Value("${quartz.datasource.quartzDataSource.maxConnections}")
    private String quartzDatasourceQuartzDataSourceMaxConnections;
    @Value("${quartz.datasource.quartzDataSource.connection-provider}")
    private String quartzDatasourceQuartzDataSourceConnectionProviderClass;
}
