/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the rsmanage4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package com.ruisen.rsmanage.daemon.quartz.task;

import com.ruisen.rsmanage.daemon.quartz.config.NotifyWebSocketHandler;
import com.ruisen.rsmanage.daemon.quartz.constants.RsmanageQuartzEnum;
import com.ruisen.rsmanage.daemon.quartz.entity.SysJob;
import com.ruisen.rsmanage.daemon.quartz.entity.SysJobSqlLog;
import com.ruisen.rsmanage.daemon.quartz.mapper.CustomerJobMapper;
import com.ruisen.rsmanage.daemon.quartz.service.SysJobSqlLogService;
import com.ruisen.rsmanage.daemon.quartz.service.WebSocketMessageService;
import com.ruisen.rsmanage.daemon.quartz.util.JsonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

/**
 * @author 郑健楠
 */
@Slf4j
@Component("customer")
public class CustomerTask {
	private final CustomerJobMapper customerJobMapper;
	private final SysJobSqlLogService sysJobSqlLogService;
	private final WebSocketMessageService webSocketMessageService;
	private String lastMessageContent;
	private LocalDateTime lastMessageTime;
	private static final long MESSAGE_INTERVAL = 10; // 消息去重时间间隔（分钟）

	public CustomerTask(CustomerJobMapper customerJobMapper, 
					   SysJobSqlLogService sysJobSqlLogService,
					   WebSocketMessageService webSocketMessageService) {
		this.customerJobMapper = customerJobMapper;
		this.sysJobSqlLogService = sysJobSqlLogService;
		this.webSocketMessageService = webSocketMessageService;
		this.lastMessageContent = null;
		this.lastMessageTime = null;
	}

	/**
	 * 执行自定义SQL任务
	 * @param sysJob 要执行的SQL语句
	 */
	@SneakyThrows
	public String customerTask(SysJob sysJob) {
		if (sysJob == null || sysJob.getSqlPreview() == null) {
			log.error("无效的任务参数");
			return RsmanageQuartzEnum.JOB_LOG_STATUS_FAIL.getType();
		}

		SysJobSqlLog sqlLog = new SysJobSqlLog();
		sqlLog.setJobId(sysJob.getJobId());
		sqlLog.setJobName(sysJob.getJobName());
		sqlLog.setSqlContent(sysJob.getSqlPreview());
		sqlLog.setExecutionTime(LocalDateTime.now());

		try {
			String sql = sysJob.getSqlPreview();
			log.info("执行的sql定时任务语句: {}", sql);

			// 执行SQL并获取结果
			List<Map<String, Object>> result = customerJobMapper.executeCustomSql(sql);
			
			// 日期格式化方法
			sqlLog.setExecutionResult(formatResultForJson(result));
			
			// 设置成功状态
			sqlLog.setStatus("0");
			
			// 使用 service 保存日志
			boolean success = sysJobSqlLogService.save(sqlLog);
			if (!success) {
				log.error("保存SQL执行日志失败");
			}

			// 发送WebSocket通知
			if (sysJob.getNotify() != null && !sysJob.getNotify().isEmpty()) {
				sendNotification(sysJob, result,sqlLog);
			}

			return RsmanageQuartzEnum.JOB_LOG_STATUS_SUCCESS.getType();

		} catch (Exception e) {
			log.error("定时任务执行失败: {}", e.getMessage(), e);
			// 设置失败状态和错误信息
			sqlLog.setStatus("1");
			sqlLog.setErrorInfo(e.getMessage());
			sysJobSqlLogService.save(sqlLog);
			
			return RsmanageQuartzEnum.JOB_LOG_STATUS_FAIL.getType();
		}
	}

	// 发送通知方法
	private void sendNotification(SysJob sysJob, List<Map<String, Object>> result, SysJobSqlLog sqlLog) {
		try {
			// 构造消息内容
			Map<String, Object> message = new HashMap<>();
			message.put("taskName", sysJob.getJobName());
			message.put("message", "执行成功");
			
			// 使用DateTimeFormatter格式化时间
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			message.put("executionTime", sqlLog.getExecutionTime().format(formatter));
			
			message.put("result", formatResultForJson(result));

			String messageContent = JsonUtils.toJsonString(message);
			
			// 检查消息内容和时间间隔
			LocalDateTime now = LocalDateTime.now();
			if (messageContent.equals(lastMessageContent) && 
				lastMessageTime != null && 
				now.minusMinutes(MESSAGE_INTERVAL).isBefore(lastMessageTime)) {
				log.info("消息在{}分钟内重复，跳过发送", MESSAGE_INTERVAL);
				return;
			}
			
			// 更新最后发送的消息内容和时间
			lastMessageContent = messageContent;
			lastMessageTime = now;

			List<Long> notifyUsers = Arrays.stream(sysJob.getNotify().split(","))
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.map(s -> {
						try {
							return Long.valueOf(s);
						} catch (NumberFormatException e) {
							log.error("无效的用户ID格式: {}", s);
							return null;
						}
					})
					.filter(id -> id != null)
					.collect(Collectors.toList());

			for (Long userId : notifyUsers) {
				String userName = customerJobMapper.qryUsername(userId);
				boolean sendStatus = NotifyWebSocketHandler.sendMessage(userName, messageContent);
				if(!sendStatus) {
					// 只有发送失败（用户离线）时才保存到数据库
					log.info("用户离线，消息保存到数据库: {}", userName);
					webSocketMessageService.saveMessage(userName, messageContent);
				} else {
					log.info("告警消息已发送至用户: {}", userName);
				}
			}
		} catch (Exception e) {
			log.error("发送WebSocket消息失败: {}", e.getMessage());
		}
	}

	// 在执行SQL结果序列化之前，处理日期格式
	private String formatResultForJson(List<Map<String, Object>> result) {
		result.forEach(map -> {
			map.forEach((key, value) -> {
				if (value instanceof java.sql.Date || value instanceof java.sql.Timestamp) {
					map.put(key, value.toString());
				}
			});
		});
		return JsonUtils.toJsonString(result);
	}

}
