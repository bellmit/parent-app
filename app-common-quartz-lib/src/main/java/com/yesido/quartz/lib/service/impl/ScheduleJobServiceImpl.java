package com.yesido.quartz.lib.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.yesido.proto.entity.quartz.ScheduleJob;
import com.yesido.proto.entity.quartz.ScheduleJobLog;
import com.yesido.quartz.lib.service.ScheduleJobService;

@Service
public class ScheduleJobServiceImpl implements ScheduleJobService {

    @Autowired
    @Qualifier("quartzJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public List<ScheduleJob> findByAppName(String appName) {
        String sql = "select * from schedule_job where belong_app = ?";
        Object[] params = new Object[]{appName};
        return jdbcTemplate.query(sql, params, new ScheduleJob());
    }

    @Override
    public ScheduleJob getOne(Long id) {
        String sql = "select * from schedule_job where id = ?";
        Object[] params = new Object[]{id};
        return jdbcTemplate.queryForObject(sql, params, ScheduleJob.class);
    }

    @Override
    public void updateJobStatus(Long id, Integer jobStatus) {
        String sql = "update schedule_job set job_status = ? where id = ?";
        Object[] params = new Object[]{jobStatus, id};
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void saveLog(ScheduleJobLog jobLog) {
        String sql = "insert into schedule_job_log (`job_id`, `status`, `msg`, `cost_time`, `exe_time`, `createtime`) VALUES (?, ?, ?, ?, ?, ?)";
        Object[] params =
                new Object[]{jobLog.getJobId(), jobLog.getStatus(), jobLog.getMsg(), jobLog.getCost_time(), jobLog.getExeTime(),
                        jobLog.getCreatetime()};
        jdbcTemplate.update(sql, params);
    }

}
