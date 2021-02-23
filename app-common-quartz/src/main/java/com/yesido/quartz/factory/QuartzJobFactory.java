package com.yesido.quartz.factory;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.yesido.proto.entity.quartz.ScheduleJob;
import com.yesido.quartz.utils.JobUtil;

/**
 * 任务factory
 * 
 * @author yesido
 * @date 2018年10月21日
 */
@DisallowConcurrentExecution
public class QuartzJobFactory implements Job {
    Logger logger = LoggerFactory.getLogger(getClass());
    public static final Integer IS_EXECUTE = 1;
    public static final Integer NOT_EXECUTE = 0;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Object scheduleJobData = context.getMergedJobDataMap().get("scheduleJob");
        ScheduleJob scheduleJob = JSONObject.parseObject(scheduleJobData.toString(), ScheduleJob.class);
        logger.info("执行任务：{}", JSONObject.toJSONString(scheduleJob));
        if (scheduleJob.getIsExecute() == 0) {
            scheduleJob.setIsExecute(IS_EXECUTE);
            JobUtil.invokMethod(scheduleJob);
            scheduleJob.setIsExecute(NOT_EXECUTE);
        }
    }

}
