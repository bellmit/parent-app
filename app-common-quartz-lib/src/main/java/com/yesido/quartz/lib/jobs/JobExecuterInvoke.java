package com.yesido.quartz.lib.jobs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.yesido.proto.entity.quartz.ScheduleJob;
import com.yesido.proto.entity.quartz.ScheduleJobLog;
import com.yesido.quartz.lib.config.SpringContextHolder;
import com.yesido.quartz.lib.service.ScheduleJobService;

public class JobExecuterInvoke {
    private static Logger logger = LoggerFactory.getLogger(JobExecuterInvoke.class);

    /**
     * 通过反射调用scheduleJob中定义的方法
     * 
     * @param scheduleJob
     */
    public static void invokeMethod(ScheduleJob scheduleJob) {
        Date exeTime = new Date();
        Long start = System.currentTimeMillis();
        boolean rs = true;
        String msg = "执行成功！";
        Object object = null;
        Class<?> clazz = null;
        if (scheduleJob.getBeanClass() != null && !scheduleJob.getBeanClass().isEmpty()) {
            try {
                clazz = Class.forName(scheduleJob.getBeanClass());
                object = clazz.newInstance();
            } catch (Exception ignore) {
                msg = "找不到要执行的类:\n" + parseException(ignore);
            }
        }
        if (object == null) {
            rs = false;
        } else {
            clazz = object.getClass();
            Method method = null;
            try {
                method = clazz.getDeclaredMethod(scheduleJob.getMethodName());
                method.invoke(object);
            } catch (NoSuchMethodException e) {
                rs = false;
                msg = "找不要要执行的方法:\n" + parseException(e);
            } catch (Exception e) {
                rs = false;
                msg = "执行任务方法出错:\n" + parseException(e);
            }
        }
        taskLog(scheduleJob, rs, msg, exeTime, start);
    }

    private static String parseException(Exception e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.fillInStackTrace().printStackTrace(printWriter);
        String error = result.toString();
        printWriter.close();
        return error;
    }

    /**
     * 记录任务执行日志
     */
    private static void taskLog(ScheduleJob scheduleJob, boolean success, String msg, Date exeTime, long start) {
        ScheduleJobLog jobLog = new ScheduleJobLog();
        try {
            Long end = System.currentTimeMillis();
            ScheduleJobService jobService = SpringContextHolder.getBean(ScheduleJobService.class);
            jobLog.setCreatetime(new Date());
            jobLog.setStatus(success ? 1 : 2);
            jobLog.setExeTime(exeTime);
            jobLog.setJobId(scheduleJob.getId());
            jobLog.setCost_time((int) (end - start));
            if (!success) {
                jobLog.setMsg(msg);
            } else {
                jobLog.setMsg("执行成功");
            }
            jobService.saveLog(jobLog);
        } catch (Exception e) {
            logger.error("记录定时任务执行日志失败：{}, exception: {}", JSONObject.toJSONString(jobLog), e.toString());
        }
    }
}
