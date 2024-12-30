<template>
	<div class="layout-container">
		<component :is="layouts[themeConfig.layout]" />
		
		<!-- 添加通知栏 -->
		<notice-bar
			v-if="state.currentMessage"
			:title="state.currentMessage.title"
			:text="state.currentMessage.content"
			:color="getNoticeColor(state.currentMessage.type)"
			:background="getNoticeBackground(state.currentMessage.type)"
			mode="closeable"
			:scrollable="true"
			left-icon="Bell"
			@close="closeNotice"
		/>
		
		<!-- WebSocket组件 -->
		<Websocket @message="handleWebSocketMessage" />
	</div>
</template>

<script setup lang="ts" name="layout">
import { onBeforeMount, onUnmounted, defineAsyncComponent, reactive } from 'vue';
import { storeToRefs } from 'pinia';
import { useThemeConfig } from '/@/stores/themeConfig';
import { Local } from '/@/utils/storage';
import mittBus from '/@/utils/mitt';
import NoticeBar from '/@/components/NoticeBar/index.vue';

// 引入组件
const layouts: any = {
	defaults: defineAsyncComponent(() => import('/@/layout/main/defaults.vue')),
	classic: defineAsyncComponent(() => import('/@/layout/main/classic.vue')),
	transverse: defineAsyncComponent(() => import('/@/layout/main/transverse.vue')),
	columns: defineAsyncComponent(() => import('/@/layout/main/columns.vue')),
};

// 定义变量内容
const storesThemeConfig = useThemeConfig();
const { themeConfig } = storeToRefs(storesThemeConfig);

const state = reactive({
	currentMessage: null,
	messageQueue: [] as any[]
});

// 窗口大小改变时(适配移动端)
const onLayoutResize = () => {
	if (!Local.get('oldLayout')) Local.set('oldLayout', themeConfig.value.layout);
	const clientWidth = document.body.clientWidth;
	if (clientWidth < 1000) {
		themeConfig.value.isCollapse = false;
		mittBus.emit('layoutMobileResize', {
			layout: 'defaults',
			clientWidth,
		});
	} else {
		mittBus.emit('layoutMobileResize', {
			layout: Local.get('oldLayout') ? Local.get('oldLayout') : themeConfig.value.layout,
			clientWidth,
		});
	}
};
// 页面加载前
onBeforeMount(() => {
	onLayoutResize();
	window.addEventListener('resize', onLayoutResize);
});
// 页面卸载时
onUnmounted(() => {
	window.removeEventListener('resize', onLayoutResize);
});

const handleWebSocketMessage = (message: any) => {
    try {
        // 使用传入的消息数据，它已经包含了正确的 title
        console.log('接收到的消息数据:', message);
        
        // 添加到消息队列
        state.messageQueue.push(message);  // 直接使用传入的消息对象
        console.log('当前消息队列:', state.messageQueue);
        
        if (!state.currentMessage) {
            showNextMessage();
        }
        
        // 处理消息历史
        useMsg().handleWebSocketMsg(message);
    } catch (error) {
        console.error('处理WebSocket消息失败:', error);
    }
};
// 显示下一条消息
const showNextMessage = () => {
    try {
        if (state.messageQueue?.length > 0) {
            const message = state.messageQueue.shift();
            state.currentMessage = {
                title: message.title,
                content: message.content,
                type: message.type || 'info'
            };
            
            // 设置消息自动消失
            setTimeout(() => {
                closeNotice();
            }, 5000);
        }
    } catch (error) {
        console.error('显示消息通知失败:', error);
    }
};

// 关闭当前通知
const closeNotice = () => {
	state.currentMessage = null;
	// 检查是否还有待显示的消息
	setTimeout(() => {
		showNextMessage();
	}, 300);
};

// 获取通知栏颜色
const getNoticeColor = (type: string) => {
	switch (type) {
		case 'error': return 'var(--el-color-danger)';
		case 'warning': return 'var(--el-color-warning)';
		default: return 'var(--el-color-success)';
	}
};

// 获取通知栏背景色
const getNoticeBackground = (type: string) => {
	switch (type) {
		case 'error': return 'var(--el-color-danger-light-9)';
		case 'warning': return 'var(--el-color-warning-light-9)';
		default: return 'var(--el-color-success-light-9)';
	}
};
</script>

<style scoped>
.layout-container {
	position: relative;
}

:deep(.notice-bar) {
	position: fixed;
	top: 60px;
	left: 0;
	right: 0;
	z-index: 2000;
}
</style>
