package com.yesido.quartz.config;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Quartz配置
 * 
 * @author yesido
 * @date 2019年7月30日 上午10:56:00
 */
@Configuration
public class SchedulerConfig {

    @Value("${quartz.job.package:com.yesido.quartz.jobs}")
    private String executerJobPackage;

    public String getExecuterJobPackage() {
        return executerJobPackage;
    }

    public void setExecuterJobPackage(String executerJobPackage) {
        this.executerJobPackage = executerJobPackage;
    }

    @Bean
    public Scheduler scheduler() throws Exception {
        Scheduler scheduler = schedulerFactoryBean().getScheduler();
        if (scheduler.isStarted() == false) {
            scheduler.start();
        }
        return scheduler;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setJobFactory(jobFactory());
        factory.setAutoStartup(true);
        factory.setOverwriteExistingJobs(true);
        factory.setApplicationContextSchedulerContextKey("applicationContext");
        factory.setTaskExecutor(schedulerThreadPool());
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("quartz.properties"));

        // 在quartz.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean
    public AdaptableJobFactory jobFactory() {
        AdaptableJobFactory jobFactory = new AdaptableJobFactory();
        return jobFactory;
    }

    @Bean
    public Executor schedulerThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(1000);
        return executor;
    }
}
