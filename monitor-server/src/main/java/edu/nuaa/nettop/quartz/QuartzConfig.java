package edu.nuaa.nettop.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;


/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-11
 * Time: 15:56
 */
@Configuration
public class QuartzConfig {

    private final QuartzPropertiesConfig quartzPropertiesConfig;

    @Autowired
    public QuartzConfig(QuartzPropertiesConfig quartzPropertiesConfig) {
        this.quartzPropertiesConfig = quartzPropertiesConfig;
    }

    @Bean
    public Scheduler scheduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory(quartzProperties());
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        return scheduler;
    }

    @Bean
    public Properties quartzProperties() {
        Properties prop = new Properties();
        prop.put("quartz.scheduler.instanceName", quartzPropertiesConfig.getQuartzSchedulerInstanceName());
        prop.put("org.quartz.scheduler.instanceId", quartzPropertiesConfig.getQuartzSchedulerInstanceId());
        prop.put("org.quartz.scheduler.skipUpdateCheck", quartzPropertiesConfig.getQuartzSchedulerSkipUpdateCheck());
        prop.put("org.quartz.scheduler.jobFactory.class", quartzPropertiesConfig.getQuartzSchedulerJobFactoryClass());
        prop.put("org.quartz.jobStore.class", quartzPropertiesConfig.getQuartzJobStoreClass());
        prop.put("org.quartz.jobStore.driverDelegateClass", quartzPropertiesConfig.getQuartzJobStoreDriverDelegateClass());
        prop.put("org.quartz.jobStore.dataSource", quartzPropertiesConfig.getQuartzJobStoreDatasource());
        prop.put("org.quartz.jobStore.tablePrefix", quartzPropertiesConfig.getQuartzJobStoreTablePrefix());
        prop.put("org.quartz.jobStore.isClustered", quartzPropertiesConfig.getQuartzJobStoreIsClustered());
        prop.put("org.quartz.threadPool.class", quartzPropertiesConfig.getQuartzThreadPoolClass());
        prop.put("org.quartz.threadPool.threadCount", quartzPropertiesConfig.getQuartzThreadPoolThreadCount());
        prop.put("org.quartz.dataSource.quartzDataSource.connectionProvider.class", quartzPropertiesConfig.getQuartzDatasourceQuartzDataSourceConnectionProviderClass());
        prop.put("org.quartz.dataSource.quartzDataSource.driver", quartzPropertiesConfig.getQuartzDatasourceQuartzDataSourceDriver());
        prop.put("org.quartz.dataSource.quartzDataSource.URL", quartzPropertiesConfig.getQuartzDatasourceQuartzDataSourceUrl());
        prop.put("org.quartz.dataSource.quartzDataSource.user", quartzPropertiesConfig.getQuartzDatasourceQuartzDataSourceUser());
        prop.put("org.quartz.dataSource.quartzDataSource.password", quartzPropertiesConfig.getQuartzDatasourceQuartzDataSourcePassword());
        prop.put("org.quartz.dataSource.quartzDataSource.maxConnections", quartzPropertiesConfig.getQuartzDatasourceQuartzDataSourceMaxConnections());

        return prop;
    }
}
