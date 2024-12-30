package com.ruisen.rsmanage.daemon.quartz.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruisen.rsmanage.daemon.quartz.entity.SysJob;
import com.ruisen.rsmanage.daemon.quartz.entity.WebSocketMessage;
import com.ruisen.rsmanage.daemon.quartz.mapper.SysJobMapper;
import com.ruisen.rsmanage.daemon.quartz.mapper.WebSocketMessageMapper;
import com.ruisen.rsmanage.daemon.quartz.service.WebSocketMessageService;
import com.ruisen.rsmanage.daemon.quartz.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class WebSocketMessageServiceImpl  extends ServiceImpl<WebSocketMessageMapper, WebSocketMessage> implements WebSocketMessageService {

	private final WebSocketMessageMapper messageMapper;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public void saveMessage(String username, String message) {
		// 检查最近5分钟内是否有相同消息且未读
		boolean exists = messageMapper.exists(
			new QueryWrapper<WebSocketMessage>()
				.eq("username", username)
				.eq("content", message)
				.eq("is_read", false)  // 只检查未读的消息
				.gt("create_time", LocalDateTime.now().minusMinutes(5))
		);
		
		if (!exists) {
			// 清理超过1小时的已读消息
			messageMapper.delete(
				new QueryWrapper<WebSocketMessage>()
					.eq("username", username)
					.eq("is_read", true)
					.lt("create_time", LocalDateTime.now().minusHours(1))
			);
			
			// 解析消息内容以更新时间格式
			try {
				Map<String, Object> messageMap = JsonUtils.parseMap(message);
				if (messageMap.containsKey("executionTime")) {
					LocalDateTime dateTime = LocalDateTime.parse(
						messageMap.get("executionTime").toString(), 
						formatter
					);
					messageMap.put("executionTime", dateTime.format(formatter));
					message = JsonUtils.toJsonString(messageMap);
				}
			} catch (Exception e) {
				log.error("解析消息时间格式失败: {}", e.getMessage());
			}
			
			WebSocketMessage msg = new WebSocketMessage();
			msg.setUsername(username);
			msg.setContent(message);
			msg.setCreateTime(LocalDateTime.now());
			msg.setIsRead(false);
			messageMapper.insert(msg);
		}
	}

	@Override
	public List<WebSocketMessage> getUnreadMessages(String username) {
		return messageMapper.selectList(
				new QueryWrapper<WebSocketMessage>()
						.eq("username", username)
						.eq("is_read", false)
						.orderByAsc("create_time")
		);
	}

	@Override
	public void markAsRead(List<Long> messageIds) {
		if(CollUtil.isEmpty(messageIds)) {
			return;
		}
		messageMapper.update(
				null,
				new UpdateWrapper<WebSocketMessage>()
						.set("is_read", true)
						.in("id", messageIds)
		);
	}
}