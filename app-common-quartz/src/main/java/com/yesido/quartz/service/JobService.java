package com.yesido.quartz.service;

import java.util.List;

import org.quartz.CronTrigger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.yesido.proto.entity.quartz.ScheduleJob;
import com.yesido.quartz.mapper.ScheduleJobMapper;

/**
 * JobService 实现
 * 
 * @author yesido
 * @date 2018年10月20日
 */
@Service
public class JobService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    public boolean isSchedulerStart() {
        boolean rs = false;
        try {
            rs = scheduler.isStarted();
        } catch (SchedulerException e) {
            logger.error("判断任务计划是否启动异常：{}", e.toString());
        }
        return rs;
    }

    public boolean isSchedulerShutdown() {
        boolean rs = false;
        try {
            rs = scheduler.isShutdown();
        } catch (SchedulerException e) {
            logger.error("判断任务计划是否停止异常:{}", e.toString());
        }
        return rs;
    }

    public void startScheduler() throws SchedulerException {
        if (scheduler.isStarted() == false) {
            scheduler.start();
        }
    }

    public void shutdown() throws SchedulerException {
        if (scheduler.isShutdown() == false) {
            // standby之后可以重新start
            scheduler.standby();
            // shutdown之后不能重新start
            // scheduler.shutdown();
        }
    }

    public void startup() {
        try {
            scheduler.start();
        } catch (Exception e) {
            logger.error("startup任务计划失败:{}", e.toString());
        }
    }

    public boolean checkExists(String jobName, String jobGroup) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        boolean rs = false;
        try {
            rs = scheduler.checkExists(jobKey);
        } catch (SchedulerException e) {
            logger.error("检查调度任务是否在任务计划中异常:{}", e.toString());
        }
        return rs;
    }

    public boolean checkScheduler() {
        boolean rs = isSchedulerStart();
        if (rs == false) {
            logger.info("任务计划没有启动！");
            return false;
        }
        rs = isSchedulerShutdown();
        if (rs == true) {
            logger.info("任务计划已经停止！");
            return false;
        }
        return true;
    }

    public boolean checkSchedulerJob(String jobName, String jobGroup) {
        boolean rs = checkScheduler();
        if (rs == false) {
            return false;
        }
        rs = checkExists(jobName, jobGroup);
        if (rs == false) {
            logger.info("调度任务没有在任务计划中！");
            return false;
        }
        return true;
    }

    public CronTrigger getTrigger(String jobName, String jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        CronTrigger trigger = null;
        try {
            trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        } catch (SchedulerException e) {
            logger.error("获取触发器失败, jobName：{}, jobGroup：{}, exception：{}", jobName, jobGroup, e.toString());
        }
        return trigger;
    }

    public void addJob(Long jobId) {
        if (!checkScheduler()) {
            return;
        }

        ScheduleJob job = scheduleJobMapper.getOne(jobId);
        logger.info("启动调度任务：{}", JSONObject.toJSONString(job));
        if (job != null) {
            updateJobStatus(job.getId(), ScheduleJob.STATUS_NORMAL);
            /*try {
                Scheduler scheduler = schedulerFactoryBean.getScheduler();
                CronTrigger trigger = getTrigger(job.getJobName(), job.getJobGroup());
                if (trigger == null) {
                    // 不存在,创建一个
                    Class<? extends Job> clazz = QuartzJobFactory.class;
                    JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getJobName(), job.getJobGroup()).build();
                    jobDetail.getJobDataMap().put("scheduleJob", JSONObject.toJSONString(job));
            
                    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                    trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(scheduleBuilder).build();
            
                    scheduler.scheduleJob(jobDetail, trigger);
                } else {
                    TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
                    // Trigger已存在,那么更新相应的定时设置
                    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                    // 按新的cronExpression表达式重新构建trigger
                    trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                    // 按新的trigger重新设置job执行
                    scheduler.rescheduleJob(triggerKey, trigger);
                }
                updateJobStatus(job.getId(), ScheduleJob.STATUS_NORMAL);
            } catch (Exception e) {
                logger.error("添加调度任务失败：{}, exception：{}", jobId, e.toString());
            }*/
        }
    }

    public void pauseJob(Long jobId) {
        if (!isSchedulerStart()) {
            return;
        }
        ScheduleJob job = scheduleJobMapper.getOne(jobId);
        logger.info("挂起调度任务：{}", JSONObject.toJSONString(job));
        /*Scheduler scheduler = schedulerFactoryBean.getScheduler();
        
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        try {
            scheduler.pauseJob(jobKey);
            updateJobStatus(job.getId(), ScheduleJob.STATUS_PAUSED);
        } catch (SchedulerException e) {
            logger.error("挂起调度任务失败：{}, exception：{}", jobId, e.toString());
        }*/
        updateJobStatus(job.getId(), ScheduleJob.STATUS_PAUSED);
    }

    public void resumeJob(Long jobId) {
        if (!isSchedulerStart()) {
            logger.error("任务计划没有启动,恢复调度任务失败：{}", jobId);
            return;
        }
        ScheduleJob job = scheduleJobMapper.getOne(jobId);
        logger.info("恢复调度任务：{}", JSONObject.toJSONString(job));

        /*Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        try {
            scheduler.resumeJob(jobKey);
            updateJobStatus(job.getId(), ScheduleJob.STATUS_NORMAL);
        } catch (SchedulerException e) {
            logger.error("恢复调度任务失败：{}, exception：{}", jobId, e.toString());
        }*/
        updateJobStatus(job.getId(), ScheduleJob.STATUS_NORMAL);
    }

    public void removeJob(Long jobId) {
        if (!isSchedulerStart()) {
            logger.error("任务计划没有启动,移除调度任务失败：{}", jobId);
            return;
        }
        ScheduleJob job = scheduleJobMapper.getOne(jobId);
        logger.info("移除调度任务：{}", JSONObject.toJSONString(job));

        /*Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        try {
            scheduler.deleteJob(jobKey);
            updateJobStatus(job.getId(), ScheduleJob.STATUS_NONE);
        } catch (SchedulerException e) {
            logger.error("移除调度任务失败：{}, exception：{}", jobId, e.toString());
        }*/
        updateJobStatus(job.getId(), ScheduleJob.STATUS_NONE);
    }

    public void runJobOneTime(Long jobId) {
        if (!isSchedulerStart()) {
            logger.error("任务计划没有启动,执行一次调度任务失败：{}", jobId);
            return;
        }
        ScheduleJob job = scheduleJobMapper.getOne(jobId);
        logger.info("执行一次调度任务：{}", JSONObject.toJSONString(job));

        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        try {
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            logger.error("执行一次调度任务失败, jobId：{}, exception：{}", jobId, e.toString());
        }
    }

    public List<ScheduleJob> list() {
        return scheduleJobMapper.findAll();
    }

    @Transactional
    public void updateJobStatus(Long id, Integer status) {
        scheduleJobMapper.updateJobStatus(id, status);
    }

}
