package com.yesido.proto.entity.quartz;

import java.beans.Transient;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

/**
 * 任务类
 * 
 * @author yesido
 * @date 2018年10月20日
 */
public class ScheduleJob implements RowMapper<ScheduleJob> {
    public static final Integer STATUS_NONE = 0; // 无任务
    public static final Integer STATUS_NORMAL = 1; // 正常
    public static final Integer STATUS_PAUSED = 2; // 暂停
    public static final Integer STATUS_COMPLETE = 3; // 完成
    public static final Integer STATUS_ERROR = 4; // 错误
    public static final Integer STATUS_BLOCKED = 5; // 阻塞，当前任务获取当前任务
    // 集群运行方式，只允许一个节点运行
    public final static int run_in_simple_node = 1;
    // 集群运行方式，可以多个节点运行
    public final static int run_in_many_node = 2;

    /** 主键. */
    private Long id;

    /** 所属App，spring.application.name. */
    private String belongApp;

    /** 任务名称. */
    private String jobName;

    /** 任务分组. */
    private String jobGroup;

    /** cron表达式. */
    private String cron;

    /** 任务执行时调用哪个类的方法:包名+类名. */
    private String beanClass;

    /** 任务调用的方法名. */
    private String methodName;

    /** 任务状态:是否启动任务. */
    private Integer jobStatus;

    /** 任务调用的方法名. */
    private String remark;

    /** 运行方式，1=单节点，2=集群. */
    private Integer runType;

    private Date createtime;
    private Date updatetime;

    public Integer getRunType() {
        return runType;
    }

    public void setRunType(Integer runType) {
        this.runType = runType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getBeanClass() {
        return beanClass;
    }

    public String getBelongApp() {
        return belongApp;
    }

    public void setBelongApp(String belongApp) {
        this.belongApp = belongApp;
    }

    public void setBeanClass(String beanClass) {
        this.beanClass = beanClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Integer getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    /** ----------------非数据库字段 **/
    /** 任务是否有状态. */
    private Integer isExecute = 0;

    @Transient
    public Integer getIsExecute() {
        return isExecute;
    }

    public void setIsExecute(Integer isExecute) {
        this.isExecute = isExecute;
    }

    @Override
    public ScheduleJob mapRow(ResultSet rs, int rowNum) throws SQLException {
        ScheduleJob job = new ScheduleJob();
        job.setBeanClass(rs.getString("bean_class"));
        job.setBelongApp(rs.getString("belong_app"));
        job.setCreatetime(rs.getDate("createtime"));
        job.setCron(rs.getString("cron"));
        job.setId(rs.getLong("id"));
        job.setJobGroup(rs.getString("job_group"));
        job.setJobName(rs.getString("job_name"));
        job.setJobStatus(rs.getInt("job_status"));
        job.setMethodName(rs.getString("method_name"));
        job.setRemark(rs.getString("remark"));
        job.setRunType(rs.getInt("run_type"));
        job.setUpdatetime(rs.getDate("updatetime"));
        return job;
    }
}