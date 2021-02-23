SET NAMES utf8;
CREATE database `task`;

-- schedule_job任务表
CREATE TABLE `task`.`schedule_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PK: id',
  `belong_app` varchar(100) NOT NULL COMMENT '所属App，spring.application.name',
  `job_name` varchar(50) NOT NULL COMMENT '任务名称',
  `job_group` varchar(50) NOT NULL COMMENT '任务组',
  `cron` varchar(50) NOT NULL COMMENT '任务cron表达式',
  `bean_class` varchar(250) NOT NULL COMMENT '执行的类名',
  `method_name` varchar(50) NOT NULL COMMENT '执行的方法名',
  `job_status` int(1) NOT NULL COMMENT '任务状态',
  `remark` varchar(250) NOT NULL COMMENT '备注',
  `run_type` int(1) NOT NULL COMMENT '运行方式，1=单节点，2=集群',
  `createtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updatetime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_belong_app` (`belong_app`),
  KEY `uidx_job_name_job_group` (`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='schedule_job任务表';

