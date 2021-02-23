package com.yesido.quartz.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yesido.proto.entity.quartz.ScheduleJob;
import com.yesido.quartz.service.JobService;
import com.yesido.quartz.utils.JsonResult;

@Controller
@RequestMapping(value = "/schedule/job")
public class ScheduleJobController {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private JsonResult jsonResult;
    @Autowired
    private JobService jobService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        List<ScheduleJob> list = jobService.list();
        model.addAttribute("jobs", list);
        return "/job_list";
    }

    @RequestMapping(value = "/start_scheduler", method = RequestMethod.POST)
    public void startScheduler(HttpServletResponse response) {
        try {
            jobService.startScheduler();
            jsonResult.success(response);
        } catch (Exception e) {
            logger.error("启动任务计划失败:{}", e.toString());
            jsonResult.fail(response, "异常：" + e.toString());

        }
    }

    @RequestMapping(value = "/shutdown", method = RequestMethod.POST)
    public void shutdownScheduler(HttpServletResponse response) {
        try {
            jobService.shutdown();
            jsonResult.success(response);
        } catch (SchedulerException e) {
            logger.error("shutdown任务计划失败:{}", e.toString());
            jsonResult.fail(response, "异常：" + e.toString());
        }
    }

    @RequestMapping(value = "/startup", method = RequestMethod.POST)
    public void startupScheduler(HttpServletResponse response) {
        jobService.startup();
        jsonResult.success(response);
    }

    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public void status(HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("start", jobService.isSchedulerStart());
        result.put("shutdown", jobService.isSchedulerShutdown());
        jsonResult.data(result, response);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public void addJob(Long id, HttpServletResponse response) {
        jobService.addJob(id);
        jsonResult.success(response);
    }

    @RequestMapping(value = "/pause", method = RequestMethod.POST)
    public void pauseJob(Long id, HttpServletResponse response) {
        jobService.pauseJob(id);
        jsonResult.success(response);
    }

    @RequestMapping(value = "/resume", method = RequestMethod.POST)
    public void resumeJob(Long id, HttpServletResponse response) {
        jobService.resumeJob(id);
        jsonResult.success(response);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public void removeJob(Long id, HttpServletResponse response) {
        jobService.removeJob(id);
        jsonResult.success(response);
    }

    @RequestMapping(value = "/run", method = RequestMethod.POST)
    public void runJobOneTime(Long id, HttpServletResponse response) {
        jobService.runJobOneTime(id);
        jsonResult.success(response);
    }

}
