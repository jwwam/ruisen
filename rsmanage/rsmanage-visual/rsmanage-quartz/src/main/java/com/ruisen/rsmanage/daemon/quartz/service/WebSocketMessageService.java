package com.ruisen.rsmanage.daemon.quartz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruisen.rsmanage.daemon.quartz.entity.WebSocketMessage;
import java.util.List;

public interface WebSocketMessageService extends IService<WebSocketMessage> {

	// 保存消息
	void saveMessage(String username, String message);

	// 获取用户未读消息
	List<WebSocketMessage> getUnreadMessages(String username);

	// 标记消息为已读
	void markAsRead(List<Long> messageIds);
}