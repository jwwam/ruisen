<template>
	<div></div>
</template>
<script setup lang="ts" name="global-websocket">
import { Session } from '/@/utils/storage';

const emit = defineEmits(['rollback', 'message']);

const props = defineProps({
	uri: {
		type: String,
	},
});

const state = reactive({
	webSocket: null as WebSocket | null,
	lockReconnect: false,
	maxReconnect: 10,
	reconnectTime: 0,
	reconnectInterval: 5000,
	heartbeat: {
		interval: 30 * 1000,
		timeout: 10 * 1000,
		pingTimeoutObj: null as any,
		pongTimeoutObj: null as any,
		pingMessage: JSON.stringify({ type: 'ping' })
	}
});

onMounted(() => {
	initWebSocket();
	// 连接建立后会自动接收未读消息
	// 未读消息会通过正常的消息处理流程处理
});

onUnmounted(() => {
	closeConnection();
});

const closeConnection = () => {
	if (state.webSocket) {
		state.webSocket.close(1000, "正常关闭");
		state.webSocket = null;
	}
	clearTimeoutObj(state.heartbeat);
};

const initWebSocket = () => {
	const token = Session.getToken();
	if (!token) {
		console.error('未获取到token，WebSocket无法连接');
		return;
	}

	try {
		closeConnection();
		
		const wsUri = `ws://127.0.0.1:9999/job/ws/info?access_token=${token}`;
		// const wsUri = `${window.location.protocol === 'https :: 'ws:'}//localhost:9999/job/ws/info?access_token=${token}`;
		state.webSocket = new WebSocket(wsUri);
		
		state.webSocket.onopen = onOpen;
		state.webSocket.onerror = onError;
		state.webSocket.onmessage = onMessage;
		state.webSocket.onclose = onClose;

		setTimeout(() => {
			if (state.webSocket?.readyState !== WebSocket.OPEN) {
				console.warn('WebSocket连接超时，准备重连');
				closeConnection();
				reconnect();
			}
		}, 10000);
	} catch (error) {
		console.error('WebSocket连接初始化失败:', error);
		reconnect();
	}
};

const reconnect = () => {
	if (state.lockReconnect || (state.maxReconnect !== -1 && state.reconnectTime >= state.maxReconnect)) {
		return;
	}
	
	state.lockReconnect = true;
	setTimeout(() => {
		state.reconnectTime++;
		console.log(`尝试第 ${state.reconnectTime} 次重连`);
		initWebSocket();
		state.lockReconnect = false;
	}, state.reconnectInterval * (1 + Math.random()));
};

const clearTimeoutObj = (heartbeat: any) => {
	if (heartbeat.pingTimeoutObj) {
		clearTimeout(heartbeat.pingTimeoutObj);
		heartbeat.pingTimeoutObj = null;
	}
	if (heartbeat.pongTimeoutObj) {
		clearTimeout(heartbeat.pongTimeoutObj);
		heartbeat.pongTimeoutObj = null;
	}
};

const startHeartbeat = () => {
	if (!state.webSocket || state.webSocket.readyState !== WebSocket.OPEN) {
		return;
	}

	clearTimeoutObj(state.heartbeat);
	
	state.heartbeat.pingTimeoutObj = setTimeout(() => {
		try {
			state.webSocket?.send(state.heartbeat.pingMessage);
			state.heartbeat.pongTimeoutObj = setTimeout(() => {
				state.webSocket?.close();
			}, state.heartbeat.timeout);
		} catch (error) {
			console.error('发送心跳消息失败:', error);
		}
	}, state.heartbeat.interval);
};

const onOpen = () => {
	console.log('WebSocket连接已建立');
	startHeartbeat();
	state.reconnectTime = 0;
};

const onError = (event: Event) => {
	console.error('WebSocket连接错误:', event);
	clearTimeoutObj(state.heartbeat);
};

const onClose = (event: CloseEvent) => {
	console.log('WebSocket连接关闭:', event);
	clearTimeoutObj(state.heartbeat);
	
	if (event.code !== 1000) {
		reconnect();
	}
};

const onMessage = (event: MessageEvent) => {
	try {
		const data = JSON.parse(event.data);
		// console.log('WebSocket原始消息:', data)
		if (data.type === 'pong') {
			startHeartbeat();
		} else {
			const timestamp = data.executionTime ? 
				new Date(data.executionTime).getTime() : 
				Date.now();
			
			const messageData = {
				title: formatTitle(data),
				content: formatMessage(data),
				type: getNotificationType(data),
				timestamp: timestamp,
				executionTime: data.executionTime || formatDateTime(new Date())
			};
			// console.log('完整的消息数据:', messageData);
			emit('message', messageData);
		}
	} catch (error) {
		console.warn('处理WebSocket消息失败:', error);
	}
};

// 格式化标题
const formatTitle = (data: any) => {
	if (data.taskName) {
		const title = `${data.taskName} - 告警通知`;
		return title;
	}
	return '系统通知';
};

// 格式化消息内容
const formatMessage = (data: any) => {
	if (typeof data === 'string') return data;
	
	let message = '<div class="notification-content">';
	
	if (data.message) {
		message += `<div class="message-item">
			<i class="el-icon-info"></i>
			<span>${data.message}</span>
		</div>`;
	}
	
	if (data.result) {
		message += `<div class="message-item">
			<i class="el-icon-document"></i>
			<span>${data.result}</span>
		</div>`;
	}
	
	if (data.executionTime) {
		message += `<div class="message-item time">
			<i class="el-icon-time"></i>
			<span>${data.executionTime}</span>
		</div>`;
	}
	
	message += '</div>';
	return message;
};

// 根据消息类型返回通知类型
const getNotificationType = (data: any) => {
	if (data.status === 'error') return 'error';
	if (data.status === 'warning') return 'warning'; 
	return 'success';
};

// 获取通知图标
const getNotificationIcon = (data: any) => {
	if (data.status === 'error') return 'el-icon-e	rror';
	if (data.status === 'warning') return 'el-icon-warning';
	return 'el-icon-success';
};

const handleWebSocketMessage = (message: any) => {
	console.log('接收到的消息标题:', message.title);
	console.log('接收到的完整消息:', message);
	// ... 其余处理逻辑
};

// 添加 formatDateTime 函数
const formatDateTime = (date: Date | string) => {
	const d = new Date(date);
	return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`;
};
</script>

<style>
.custom-notification {
	background: #fff;
	border-radius: 8px;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
	padding: 16px;
}

.notification-content {
	margin-top: 8px;
}

.message-item {
	display: flex;
	align-items: center;
	margin-bottom: 8px;
	color: #606266;
	font-size: 14px;
}

.message-item i {
	margin-right: 8px;
	font-size: 16px;
}

.message-item.time {
	color: #909399;
	font-size: 12px;
}

.custom-notification .el-notification__title {
	font-weight: 600;
	font-size: 16px;
	color: #303133;
}

.custom-notification .el-notification__content {
	margin: 6px 0 0;
	font-size: 14px;
	line-height: 1.5;
}
</style>
