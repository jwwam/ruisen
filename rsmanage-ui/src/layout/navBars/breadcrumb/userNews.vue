<template>
	<div class="layout-navbars-breadcrumb-user-news">
		<div class="head-box">
			<div class="head-box-title">{{ $t('user.newTitle') }}</div>
			<el-button 
				v-if="newsList.length > 0"
				type="primary"
				link
				@click="onAllReadClick"
			>
				<el-icon><Delete /></el-icon>
				{{ $t('user.newBtn') }}
			</el-button>
		</div>
		<el-scrollbar height="400px">
			<div class="content-box">
				<template v-if="newsList.length > 0">
					<div v-for="(item, index) in newsList" 
						 :key="index" 
						 class="content-box-item"
						 @click="showFullMessage(item)">
						<div class="content-box-header">
							<span class="content-box-title">{{ item.label }}</span>
							<el-button type="danger" link @click.stop="deleteMessage(index)">
								<el-icon><Delete /></el-icon>
							</el-button>
						</div>
						<div class="content-box-msg" v-html="truncateText(item.value)"></div>
						<div class="content-box-time">{{ item.time }}</div>
					</div>
				</template>
				<el-empty :description="$t('user.newDesc')" v-else></el-empty>
			</div>
		</el-scrollbar>

		<el-dialog
			v-model="dialogVisible"
			:title="currentMessage?.label"
			width="50%"
		>
			<div class="message-content" v-html="currentMessage?.value"></div>
			<div class="message-time">{{ currentMessage?.time }}</div>
		</el-dialog>
	</div>
</template>

<script setup lang="ts" name="layoutBreadcrumbUserNews">
import { useMsg } from '/@/stores/msg';
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Delete } from '@element-plus/icons-vue';

const newsList = computed(() => {
	return useMsg().getAllMsg();
});

const dialogVisible = ref(false);
const currentMessage = ref(null);

// 截断文本
const truncateText = (text: string) => {
	if (!text) return '';
	
	// 创建临时 div 来解析 HTML
	const div = document.createElement('div');
	div.innerHTML = text;
	const plainText = div.textContent || div.innerText || '';
	
	if (plainText.length <= 50) return text;
	
	// 返回原始 HTML，但在适当位置添加省略号
	const truncated = plainText.substring(0, 50) + '...';
	div.textContent = truncated;
	return div.innerHTML;
};

// 显示完整消息
const showFullMessage = (message: any) => {
	currentMessage.value = message;
	dialogVisible.value = true;
};

// 删除单条消息
const deleteMessage = (index: number) => {
	try {
		const result = useMsg().removeMsg(index);
		if (result) {
			ElMessage.success('删除成功');
		} else {
			ElMessage.error('删除失败：消息索引无效');
		}
	} catch (error) {
		console.error('删除消息时发生错误:', error);
		ElMessage.error('删除失败：系统错误');
	}
};

// 全部已读点击
const onAllReadClick = () => {
	useMsg().removeAll();
};
</script>

<style scoped lang="scss">
.layout-navbars-breadcrumb-user-news {
	.head-box {
		position: sticky;
		top: 0;
		background: var(--el-bg-color);
		z-index: 1;
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 15px;
		border-bottom: 1px solid var(--el-border-color-lighter);

		.head-box-title {
			font-weight: bold;
			font-size: 15px;
		}
	}

	.content-box {
		font-size: 13px;

		.content-box-item {
			padding: 12px;
			border-bottom: 1px solid var(--el-border-color-lighter);
			transition: background-color 0.3s;

			&:hover {
				background-color: var(--el-fill-color-light);
			}

			.content-box-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-bottom: 5px;

				.content-box-title {
					font-weight: bold;
				}
			}

			.content-box-msg {
				color: var(--el-text-color-secondary);
				margin: 5px 0;
				line-height: 1.5;
			}

			.content-box-time {
				color: var(--el-text-color-secondary);
				font-size: 12px;
			}
		}
	}

	.message-content {
		line-height: 1.6;
		white-space: pre-wrap;
		word-break: break-word;
	}

	.message-time {
		margin-top: 15px;
		color: var(--el-text-color-secondary);
		font-size: 12px;
		text-align: right;
	}
}

:deep(.el-scrollbar__wrap) {
	overflow-x: hidden;
}
</style>

