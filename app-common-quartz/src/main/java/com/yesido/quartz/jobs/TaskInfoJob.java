package com.yesido.quartz.jobs;

import java.util.Set;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.alibaba.fastjson.JSONObject;
import com.yesido.quartz.utils.SpringContextHolder;

public class TaskInfoJob {

    public void execute() throws SchedulerException {
        SchedulerFactoryBean factoryBean = SpringContextHolder.getBean(SchedulerFactoryBean.class);
        Scheduler scheduler = factoryBean.getScheduler();
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
        for (JobKey jobKey : jobKeys) {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            System.out.println(JSONObject.toJSONString(jobDataMap.get("scheduleJob")));
        }
    }
}
