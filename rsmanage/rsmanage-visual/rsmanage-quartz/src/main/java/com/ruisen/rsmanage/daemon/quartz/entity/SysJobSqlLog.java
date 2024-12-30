package com.ruisen.rsmanage.daemon.quartz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_job_sql_log")
public class SysJobSqlLog {

	private Long id;

	private Long jobId;

	private String jobName;

	private String sqlContent;

	private LocalDateTime executionTime;

	private String executionResult;

	private String status;

	private String errorInfo;
}