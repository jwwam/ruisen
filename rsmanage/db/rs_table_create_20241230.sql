use rsmanage;
CREATE TABLE sys_job_sql_log (
 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
 `job_id` bigint NOT NULL COMMENT '任务ID',
 `job_name` varchar(64) DEFAULT NULL COMMENT '任务名称',
 `sql_content` text COMMENT '执行的SQL内容',
 `execution_time` datetime NOT NULL COMMENT '执行时间',
 `execution_result` longtext COMMENT '执行结果',
 `status` char(1) DEFAULT '0' COMMENT '执行状态（0成功 1失败）',
 `error_info` text COMMENT '错误信息',
  PRIMARY KEY (`id`),
 KEY `idx_job_id` (`job_id`)
) COMMENT = '定时任务SQL执行日志表';


CREATE TABLE `sys_websocket_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `content` text NOT NULL COMMENT '消息内容',
  `is_read` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已读',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='WebSocket消息表';



ALTER TABLE sys_job
ADD COLUMN data_source varchar(64)  DEFAULT NULL COMMENT '数据源',
ADD COLUMN table_name varchar(64)  DEFAULT NULL COMMENT '表名',
ADD COLUMN field_name varchar(64)  DEFAULT NULL COMMENT '字段名',
ADD COLUMN execute_rule varchar(32)  DEFAULT NULL COMMENT '执行规则',
ADD COLUMN notify  varchar(255) COMMENT '告警对象';
ADD COLUMN sql_preview text  COMMENT 'SQL预览',
ADD COLUMN filter_by_date TINYINT(1) DEFAULT 0 COMMENT '是否按日期过滤',
ADD COLUMN date_field VARCHAR(64) DEFAULT NULL COMMENT '日期字段名';
ADD COLUMN config_mode varchar(10) DEFAULT 'auto' COMMENT '配置模式(auto=自动配置,manual=手动配置)';

