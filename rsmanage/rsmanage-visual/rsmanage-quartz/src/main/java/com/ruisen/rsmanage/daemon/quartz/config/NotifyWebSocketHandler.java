package com.ruisen.rsmanage.daemon.quartz.config;

import cn.hutool.core.collection.CollUtil;
import com.ruisen.rsmanage.daemon.quartz.entity.WebSocketMessage;
import com.ruisen.rsmanage.daemon.quartz.service.WebSocketMessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class NotifyWebSocketHandler extends TextWebSocketHandler {
	private static final Logger log = LoggerFactory.getLogger(NotifyWebSocketHandler.class);
	private static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();
	private static final Map<String, Set<String>> messageHistory = new ConcurrentHashMap<>();
	
	private final WebSocketMessageService webSocketMessageService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		try {
			String username = getUsername(session);
			if (username == null) {
				log.error("WebSocket连接建立失败 - 无法获取用户名");
				session.close();
				return;
			}
			
			synchronized (SESSIONS) {
				WebSocketSession existingSession = SESSIONS.get(username);
				if (existingSession != null && existingSession.isOpen()) {
					try {
						existingSession.close();
					} catch (IOException e) {
						log.error("关闭旧会话失败", e);
					}
				}
				SESSIONS.put(username, session);
			}
			
			log.info("WebSocket连接建立 - username: {}, sessionId: {}", username, session.getId());
			
			// 获取并推送未读消息，添加去重逻辑
			List<WebSocketMessage> unreadMessages = webSocketMessageService.getUnreadMessages(username);
			if(!CollUtil.isEmpty(unreadMessages)) {
				Set<String> sentContents = new HashSet<>();
				for(WebSocketMessage msg : unreadMessages) {
					String content = msg.getContent();
					if(!sentContents.contains(content)) {
						sentContents.add(content);
						session.sendMessage(new TextMessage(content));
					}
					// 无论是否发送都标记为已读
					webSocketMessageService.markAsRead(Collections.singletonList(msg.getId()));
				}
			}
		} catch (Exception e) {
			log.error("WebSocket连接建立过程中发生错误", e);
			try {
				session.close();
			} catch (IOException ex) {
				log.error("关闭会话失败", ex);
			}
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		String username = getUsername(session);
		if (username != null) {
			SESSIONS.remove(username);
			log.info("WebSocket连接关闭 - username: {}", username);
		}
	}

	private String getUsername(WebSocketSession session) {
		try {
			Object usernameAttr = session.getAttributes().get("username");
			return usernameAttr != null ? usernameAttr.toString() : null;
		} catch (Exception e) {
			log.error("获取用户名失败", e);
			return null;
		}
	}

	private boolean isDuplicateMessage(String username, String content) {
		Set<String> history = messageHistory.computeIfAbsent(username, 
			k -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
		
		boolean isDuplicate = history.contains(content);
		if (!isDuplicate) {
			history.add(content);
			// 限制历史记录大小,避免内存占用过大
			if (history.size() > 100) {
				history.clear();
			}
		}
		return isDuplicate;
	}

	private void cleanupOldMessages(Set<String> history) {
		long currentTime = System.currentTimeMillis();
		history.removeIf(msg -> {
			String[] parts = msg.split("_");
			if (parts.length != 2) return false;
			try {
				long messageTime = Long.parseLong(parts[1]);
				return (currentTime - messageTime) > 60000; // 1分钟
			} catch (NumberFormatException e) {
				return false;
			}
		});
	}

	// 在发送消息前检查是否重复
	private void sendMessageWithDeduplication(WebSocketSession session, String content) throws IOException {
		String username = getUsername(session);
		if (username != null && !isDuplicateMessage(username, content)) {
			synchronized (session) {
				session.sendMessage(new TextMessage(content));
			}
		}
	}

	public static boolean sendMessage(String username, String content) {
		WebSocketSession session = SESSIONS.get(username);
		if (session != null && session.isOpen()) {
			try {
				synchronized (session) {
					// 使用静态的 messageHistory
					Set<String> history = messageHistory.computeIfAbsent(username, 
						k -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
					
					if (!history.contains(content)) {
						history.add(content);
						if (history.size() > 100) {
							history.clear();
						}
						session.sendMessage(new TextMessage(content));
						return true;
					}
				}
			} catch (IOException e) {
				log.error("发送WebSocket消息失败 - username: {}, content: {}", username, content, e);
			}
		}
		return false;
	}
}
