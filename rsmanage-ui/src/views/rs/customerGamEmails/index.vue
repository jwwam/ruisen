<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row v-show="showSearch">
				<el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
					<el-form-item label="客户名称" prop="customerName">
						<el-input placeholder="" v-model="state.queryForm.customerName" />
					</el-form-item>
					<el-form-item label="GAM邮箱" prop="email">
						<el-input placeholder="" v-model="state.queryForm.email" />
					</el-form-item>
					<el-form-item label="NetworkCode" prop="networkCode">
						<el-input placeholder="" v-model="state.queryForm.networkCode" />
					</el-form-item>
					<el-form-item label="合作伙伴标识" prop="partnerCode">
						<el-input placeholder="" v-model="state.queryForm.partnerCode" />
					</el-form-item>
					<el-form-item>
						<el-button icon="search" type="primary" @click="getDataList"> 查 询 </el-button>
						<el-button icon="Refresh" @click="resetQuery">重 置</el-button>
					</el-form-item>
				</el-form>
			</el-row>
			<el-row>
				<div class="mb8" style="width: 100%">
					<el-button icon="folder-add" type="primary" class="ml10" @click="handleAdd" v-auth="'rs_customerGamEmails_add'"> 新 增 </el-button>
					<el-button plain icon="upload-filled" type="primary" class="ml10" @click="excelUploadRef.show()" v-auth="'sys_user_add'"> 导 入 </el-button>
					<el-button plain :disabled="multiple" icon="Delete" type="primary" v-auth="'rs_customerGamEmails_del'" @click="handleDelete(selectObjs)">
						删 除
					</el-button>
					<right-toolbar
						v-model:showSearch="showSearch"
						:export="'rs_customerGamEmails_export'"
						@exportExcel="exportExcel"
						class="ml10 mr20"
						style="float: right"
						@queryTable="getDataList"
					></right-toolbar>
				</div>
			</el-row>
			<el-table
				:data="state.dataList"
				v-loading="state.loading"
				border
				:cell-style="tableStyle.cellStyle"
				:header-cell-style="tableStyle.headerCellStyle"
				@selection-change="selectionChangHandle"
				@sort-change="sortChangeHandle"
			>
				<el-table-column type="selection" width="40" align="center" />
				<el-table-column type="index" label="#" width="40" />
				<el-table-column prop="customerName" label="客户名称" show-overflow-tooltip />
				<el-table-column prop="email" label="GAM邮箱" show-overflow-tooltip />
				<el-table-column prop="networkCode" label="NetworkCode" show-overflow-tooltip />
				<el-table-column prop="partnerCode" label="合作伙伴标识" show-overflow-tooltip />
				<el-table-column label="操作" width="150">
					<template #default="scope">
						<el-button icon="edit-pen" text type="primary" v-auth="'rs_customerGamEmails_edit'" @click="handleEdit(scope.row)">编辑</el-button>
						<el-button icon="delete" text type="primary" v-auth="'rs_customerGamEmails_del'" @click="handleDelete([scope.row.emailId])"
							>删除</el-button
						>
					</template>
				</el-table-column>
			</el-table>
			<pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" v-bind="state.pagination" />
		</div>

		<!-- 编辑、新增  -->
		<form-dialog ref="formDialogRef" @refresh="getDataList(false)" />

		<!-- 导入excel (需要在 upms-biz/resources/file 下维护模板) -->
		<upload-excel
			ref="excelUploadRef"
			title="导入"
			url="/rs/customerGamEmails/import"
			temp-url="/admin/sys-file/local/file/customerGamEmails.xlsx"
			@refreshDataList="getDataList"
		/>
	</div>
</template>

<script setup lang="ts" name="systemCustomerGamEmails">
import { BasicTableProps, useTable } from '/@/hooks/table';
import { fetchNewList, delObjs } from '/@/api/rs/customerGamEmails';
import { useMessage, useMessageBox } from '/@/hooks/message';
import { useDict } from '/@/hooks/dict';

// 引入组件
const FormDialog = defineAsyncComponent(() => import('./form.vue'));
// 定义查询字典

// 定义变量内容
const formDialogRef = ref();
const excelUploadRef = ref();
// 搜索变量
const queryRef = ref();
const showSearch = ref(true);
// 多选变量
const selectObjs = ref([]) as any;
const multiple = ref(true);

const state: BasicTableProps = reactive<BasicTableProps>({
	queryForm: {},
	dataList: [], // 用于存储获取到的数据
	// pageList: fetchNewList,
});

//  table hook
const { currentChangeHandle, sizeChangeHandle, sortChangeHandle, downBlobFile, tableStyle } = useTable(state);
const getDataList = () => loadData();
// 清空搜索条件
const resetQuery = () => {
	// 清空搜索条件
	queryRef.value?.resetFields();
	// 清空多选
	selectObjs.value = [];
	getDataList();
};

// 导出excel
const exportExcel = () => {
	downBlobFile('/rs/customerGamEmails/export', Object.assign(state.queryForm, { ids: selectObjs }), 'customerGamEmails.xlsx');
};

// 多选事件
const selectionChangHandle = (objs: { emailId: string }[]) => {
	selectObjs.value = objs.map(({ emailId }) => emailId);
	multiple.value = !objs.length;
};

// 删除操作
const handleDelete = async (ids: string[]) => {
	try {
		await useMessageBox().confirm('此操作将永久删除');
	} catch {
		return;
	}

	try {
		await delObjs(ids);
		getDataList();
		useMessage().success('删除成功');
	} catch (err: any) {
		useMessage().error(err.msg);
	}
};
// 定义获取数据的函数
const loadData = async () => {
	state.loading = true;
	try {
		const response = await fetchNewList(state.queryForm);
		state.dataList = response.data.data;
		state.pagination = response.data.page;
	} catch (error) {
		console.error('Error loading data:', error);
	} finally {
		state.loading = false;
	}
};
// 在组件挂载时获取数据
onMounted(() => {
	loadData();
});

const handleEdit = (row: any) => {
	if (!row || !row.emailId) {
		useMessage().error('编辑数据异常');
		return;
	}

	if (!formDialogRef.value) {
		return;
	}

	formDialogRef.value.openDialog(row.emailId.toString());
};

// 添加新增处理函数
const handleAdd = () => {
	if (!formDialogRef.value) {
		return;
	}
	formDialogRef.value.openDialog();
};
</script>
