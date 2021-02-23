package com.yesido.quartz.lib.manager;

import java.util.List;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.yesido.proto.entity.quartz.ScheduleJob;
import com.yesido.quartz.lib.jobs.QuartzJobFactory;
import com.yesido.quartz.lib.service.ScheduleJobService;

@Component
@EnableScheduling
public class JobManager {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${spring.application.name:test}")
    private String appName;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    @Qualifier("simpleSchedulerFactoryBean")
    private SchedulerFactoryBean simpleSchedulerFactoryBean;

    @Autowired
    @Qualifier("manySchedulerFactoryBean")
    private SchedulerFactoryBean manySchedulerFactoryBean;

    @PostConstruct
    private void init() throws SchedulerException {
        Scheduler simpleScheduler = getSimpleScheduler();
        if (simpleScheduler.isStarted() == false) {
            simpleScheduler.start();
        }

        Scheduler maryScheduler = getMaryScheduler();
        if (maryScheduler.isStarted() == false) {
            maryScheduler.start();
        }
    }

    public Scheduler getScheduler(int type) {
        if (type == ScheduleJob.run_in_simple_node) {
            return getSimpleScheduler();
        } else if (type == ScheduleJob.run_in_many_node) {
            return getMaryScheduler();
        }
        return getSimpleScheduler();
    }

    public Scheduler getScheduler(ScheduleJob job) {
        return getScheduler(job.getRunType());
    }

    public Scheduler getSimpleScheduler() {
        return simpleSchedulerFactoryBean.getScheduler();
    }

    public Scheduler getMaryScheduler() {
        return manySchedulerFactoryBean.getScheduler();
    }

    public boolean checkExists(ScheduleJob job) {
        Scheduler scheduler = getScheduler(job.getRunType());
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        boolean rs = false;
        try {
            rs = scheduler.checkExists(jobKey);
        } catch (SchedulerException e) {
            logger.error("检查调度任务是否在任务计划中异常:{}", e.toString());
        }
        return rs;
    }

    public TriggerState getTriggerState(ScheduleJob job) {
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
        try {
            Scheduler scheduler = getScheduler(job.getRunType());
            TriggerState state = scheduler.getTriggerState(triggerKey);
            return state;
        } catch (SchedulerException e) {
            logger.error("获取TriggerState失败：{}, exception：{}", job.getId(), e.toString());
        }
        return null;
    }

    @Scheduled(cron = "${com.scheduler.job.reload.rate:0/5 * * * * ? }")
    public void start() {
        List<ScheduleJob> jobs = scheduleJobService.findByAppName(appName);
        for (ScheduleJob job : jobs) {
            handlerJob(job);
        }
    }

    private void handlerJob(ScheduleJob job) {
        Integer status = job.getJobStatus();
        ckeckNodeJob(job);
        switch (status) {
            case 0:
                handlerNone(job);
                break;
            case 1:
                handlerNormal(job);
                break;
            case 2:
                handlerPaused(job);
                break;
            case 3:
                handlerComplete(job);
                break;
            case 4:
                handlerError(job);
                break;
            case 5:
                handlerBlocked(job);
                break;
            default:
                break;
        }
    }

    public void ckeckNodeJob(ScheduleJob job) {
        try {
            int runType = job.getRunType();
            JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
            Scheduler scheduler = null;
            int _runType = 0;
            if (runType == ScheduleJob.run_in_simple_node) {
                scheduler = getScheduler(ScheduleJob.run_in_many_node);
                _runType = ScheduleJob.run_in_many_node;
            } else if (runType == ScheduleJob.run_in_many_node) {
                scheduler = getScheduler(ScheduleJob.run_in_simple_node);
                _runType = ScheduleJob.run_in_simple_node;
            }
            if (scheduler != null) {
                boolean exist = scheduler.checkExists(jobKey);
                if (exist) {
                    scheduler.deleteJob(jobKey);
                    logger.info("移除节点任务：{}，当前运行方式：{}，以前运行方式：{}", job.getId(), runType, _runType);
                }
            }
        } catch (SchedulerException e) {
            logger.error("ckeckNodeJob失败：{}, exception：{}", job.getId(), e.toString());
        }
    }

    private void handlerNone(ScheduleJob job) {
        boolean exist = checkExists(job);
        Scheduler scheduler = getScheduler(job);
        if (exist) {
            JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
            try {
                scheduler.deleteJob(jobKey);
            } catch (SchedulerException e) {
                logger.error("handlerNone失败：{}, exception：{}", job.getId(), e.toString());
            }
        }
    }

    private void handlerNormal(ScheduleJob job) {
        boolean exist = checkExists(job);
        Scheduler scheduler = getScheduler(job);
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        try {
            if (!exist) {
                addJob(job);
            } else {
                TriggerState triggerState = getTriggerState(job);
                if (TriggerState.PAUSED == triggerState) {
                    scheduler.resumeJob(jobKey);
                }
            }
        } catch (SchedulerException e) {
            logger.error("handlerNormal失败：{}, exception：{}", job.getId(), e.toString());
        }
    }

    private void addJob(ScheduleJob job) throws SchedulerException {
        Scheduler scheduler = getScheduler(job);
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if (trigger == null) {
            // 不存在,创建一个
            Class<? extends Job> clazz = QuartzJobFactory.class;
            JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getJobName(), job.getJobGroup()).build();
            jobDetail.getJobDataMap().put("scheduleJob", JSONObject.toJSONString(job));

            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCron());
            trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(scheduleBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            // Trigger已存在,那么更新相应的定时设置
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCron());
            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
        }
    }

    private void handlerPaused(ScheduleJob job) {
        boolean exist = checkExists(job);
        Scheduler scheduler = getScheduler(job);
        try {
            JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
            if (exist) {
                scheduler.pauseJob(jobKey);
            }
        } catch (SchedulerException e) {
            logger.error("handlerPaused失败：{}, exception：{}", job.getId(), e.toString());
        }
    }

    private void handlerComplete(ScheduleJob job) {

    }

    private void handlerError(ScheduleJob job) {

    }

    private void handlerBlocked(ScheduleJob job) {

    }

}
