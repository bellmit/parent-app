package com.yesido.quartz.lib.service;

import java.util.List;

import com.yesido.proto.entity.quartz.ScheduleJob;
import com.yesido.proto.entity.quartz.ScheduleJobLog;

public interface ScheduleJobService {

    public List<ScheduleJob> findByAppName(String appName);

    public ScheduleJob getOne(Long id);

    public void updateJobStatus(Long id, Integer jobStatus);

    public void saveLog(ScheduleJobLog jobLog);
}
