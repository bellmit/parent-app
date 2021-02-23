package com.yesido.proto.entity.quartz;

import java.util.Date;

public class ScheduleJobLog {

    /** 主键. */
    private Long id;
    private Long jobId;
    private Integer status;
    private String msg;
    private Integer cost_time;
    private Date exeTime;
    private Date createtime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCost_time() {
        return cost_time;
    }

    public void setCost_time(Integer cost_time) {
        this.cost_time = cost_time;
    }

    public Date getExeTime() {
        return exeTime;
    }

    public void setExeTime(Date exeTime) {
        this.exeTime = exeTime;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

}
