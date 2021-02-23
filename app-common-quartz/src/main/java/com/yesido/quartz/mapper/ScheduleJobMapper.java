package com.yesido.quartz.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.yesido.proto.entity.quartz.ScheduleJob;

@Mapper
public interface ScheduleJobMapper {

    public List<ScheduleJob> findAll();

    public ScheduleJob getOne(@Param("id") Long id);

    public void updateJobStatus(@Param("id") Long id, @Param("job_status") Integer jobStatus);

}
