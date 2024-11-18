<!-- excel 导入组件 -->
<template>
	<el-dialog :title="prop.title" v-model="state.upload.open" :close-on-click-modal="false" draggable>
		<el-upload
			ref="uploadRef"
			:limit="1"
			accept=".xlsx, .xls, .csv"
			:headers="headers"
			:action="baseURL + other.adaptationUrl(url)"
			:disabled="state.upload.isUploading"
			:on-progress="handleFileUploadProgress"
			:on-success="handleFileSuccess"
			:on-error="handleFileError"
			:auto-upload="false"
			drag
		>
			<i class="el-icon-upload"></i>
			<div class="el-upload__text">
				{{ $t('excel.operationNotice') }}
				<em>{{ $t('excel.clickUpload') }}</em>
			</div>
			<template #tip>
				<div class="el-upload__tip text-center">
					<span>{{ $t('excel.fileFormat') }} (.xlsx, .xls, .csv)</span>
					<el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline" @click="downExcelTemp" v-if="tempUrl"
						>{{ $t('excel.downloadTemplate') }}
					</el-link>
				</div>
			</template>
		</el-upload>
		<template #footer>
			<el-button type="primary" @click="submitFileForm">{{ $t('common.confirmButtonText') }}</el-button>
			<el-button @click="state.upload.open = false">{{ $t('common.cancelButtonText') }}</el-button>
		</template>
	</el-dialog>

	<!--校验失败错误数据-->
	<el-dialog 
		:title="$t('excel.validationFailureData')" 
		v-model="state.errorVisible"
		width="80%"
		:close-on-click-modal="false"
	>
		<!-- 添加统计信息展示 -->
		<div class="error-summary" style="margin-bottom: 15px;">
			<el-alert
				:title="`导入异常统计：共 ${state.errorData.length} 条异常数据，${state.errorTypes.size} 种错误类型`"
				type="warning"
				show-icon
				:closable="false"
			>
				<div class="error-types" style="margin-top: 8px;">
					<el-tag 
						v-for="type in Array.from(state.errorTypes)"
						:key="type"
						type="danger"
						style="margin-right: 8px; margin-bottom: 4px;"
					>
						{{ type }}
					</el-tag>
				</div>
			</el-alert>
		</div>

		<el-table :data="state.errorData" height="800">
			<el-table-column property="lineNum" :label="$t('excel.lineNumbers')" width="100"></el-table-column>
			<el-table-column property="errors" :label="$t('excel.misDescription')" min-width="300">
				<template v-slot="scope">
					<el-tag 
						v-for="error in scope.row.errors" 
						:key="error" 
						type="danger" 
						style="margin-right: 8px; margin-bottom: 4px;"
					>
						{{ error }}
					</el-tag>
				</template>
			</el-table-column>
			<el-table-column property="rowData" label="原始数据" min-width="500">
				<template v-slot="scope">
					<div class="row-data-scroll">
						<template v-if="scope.row.rowData">
							<el-tag 
								v-for="(value, key) in scope.row.rowData" 
								:key="key"
								size="small"
								style="margin-right: 8px; white-space: nowrap;"
							>
								{{ key }}: {{ value }}
							</el-tag>
						</template>
						<span v-else>{{ $t('common.noData') }}</span>
					</div>
				</template>
			</el-table-column>
		</el-table>
	</el-dialog>
</template>

<script setup lang="ts" name="upload-excel">
import { useMessage } from '/@/hooks/message';
import other from '/@/utils/other';
import { Session } from '/@/utils/storage';
import { ElLoading } from 'element-plus';

const emit = defineEmits(['sizeChange', 'refreshDataList']);
const prop = defineProps({
	url: {
		type: String,
	},
	title: {
		type: String,
	},
	tempUrl: {
		type: String,
	},
});

const uploadRef = ref();

const state = reactive({
	errorVisible: false,
	errorData: [],
	errorTypes: new Set(),
	dialog: {
		title: '',
		isShowDialog: false,
	},
	upload: {
		open: false,
		isUploading: false,
	},
});

/**
 * 下载模板文件
 */
const downExcelTemp = () => {
	other.downBlobFile(other.adaptationUrl(prop.tempUrl), {}, 'temp.xlsx');
};

/**
 * 上传进度条变化事件
 */
const handleFileUploadProgress = () => {
	state.upload.isUploading = true;
};

/**
 * 上传失败事件处理
 */
const handleFileError = () => {
	// 关闭加载提示
	ElLoading.service().close();
	
	useMessage().error('上传失败,数据格式不合法!');
	state.upload.open = false;
};

/**
 * 上传成功事件处理
 * @param {any} response - 上传成功的响应结果
 */
const handleFileSuccess = (response: any) => {
	// 关闭加载提示
	ElLoading.service().close();
	
	state.upload.isUploading = false;
	uploadRef.value.clearFiles();

	// 校验失败
	if (response.code === 1) {
		// 统计错误类型
		state.errorTypes.clear();
		response.data.forEach((item: any) => {
			item.errors.forEach((error: string) => {
				state.errorTypes.add(error);
			});
		});
		
		// 先设置错误数据
		state.errorData = response.data;
		state.errorVisible = true;
		
		// 延迟关闭上传弹框，确保错误弹框显示后再关闭
		setTimeout(() => {
			state.upload.open = false;
		}, 100);

		// 刷新表格
		emit?.('refreshDataList');
	} else {
		state.upload.open = false;
		useMessage().success(response.msg ? response.msg : '导入成功：'+response.data);
		// 刷新表格
		emit?.('refreshDataList');
	}
};

/**
 * 提交表单，触发上传
 */
const submitFileForm = () => {
	// 显示加载提示
	const loading = ElLoading.service({
		lock: true,
		text: '正在校验数据，请稍候...',
		background: 'rgba(0, 0, 0, 0.7)'
	});
	uploadRef.value.submit();
};

/**
 * 显示上传文件对话框，并清除上传信息
 */
const show = () => {
	state.upload.isUploading = false;
	state.upload.open = true;
};

/**
 * 计算请求头部信息
 */
const headers = computed(() => {
	return {
		Authorization: 'Bearer ' + Session.getToken(),
		'TENANT-ID': Session.getTenant(),
	};
});

// 暴露变量
defineExpose({
	show,
});
</script>

<style scoped>
.row-data-scroll {
	overflow-x: auto;
	white-space: nowrap;
	padding: 8px 0;
}

/* 自定义滚动条样式 */
.row-data-scroll::-webkit-scrollbar {
	height: 6px;
}

.row-data-scroll::-webkit-scrollbar-thumb {
	background-color: #909399;
	border-radius: 3px;
}

.row-data-scroll::-webkit-scrollbar-track {
	background-color: #f5f7fa;
}
</style>
