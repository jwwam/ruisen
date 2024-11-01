<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row v-show="showSearch">
				<el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
					<el-form-item label="客户名称" prop="customerName">
						<el-input placeholder="" v-model="state.queryForm.customerName" />
					</el-form-item>
					<el-form-item label="合作伙伴标识" prop="partnerCode">
						<el-input placeholder="" v-model="state.queryForm.partnerCode" />
					</el-form-item>
					<el-form-item label="客户在合作伙伴平台的账号" prop="partnerAccount">
						<el-input placeholder="" v-model="state.queryForm.partnerAccount" />
					</el-form-item>
					<el-form-item>
						<el-button icon="search" type="primary" @click="getDataList"> 查 询 </el-button>
						<el-button icon="Refresh" @click="resetQuery">重 置</el-button>
					</el-form-item>
				</el-form>
			</el-row>
			<el-row>
				<div class="mb8" style="width: 100%">
					<el-button icon="folder-add" type="primary" class="ml10" @click="formDialogRef.openDialog()" v-auth="'rs_customerPartner_add'">
						新 增
					</el-button>
					<el-button plain icon="upload-filled" type="primary" class="ml10" @click="excelUploadRef.show()" v-auth="'sys_user_add'"> 导 入 </el-button>
					<el-button plain :disabled="multiple" icon="Delete" type="primary" v-auth="'rs_customerPartner_del'" @click="handleDelete(selectObjs)">
						删 除
					</el-button>
					<right-toolbar
						v-model:showSearch="showSearch"
						:export="'rs_customerPartner_export'"
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
				<el-table-column prop="partnerCode" label="合作伙伴标识" show-overflow-tooltip />
				<el-table-column prop="customerRevenueShare" label="客户分成比例（百分比）" show-overflow-tooltip />
				<el-table-column prop="partnerAccount" label="客户在合作伙伴平台的账号" show-overflow-tooltip />
				<el-table-column prop="partnerPassword" label="客户在合作伙伴平台的密码" show-overflow-tooltip />
				<el-table-column prop="adsTxtContent" label="对应的ads.txt内容" show-overflow-tooltip />
				<!-- <el-table-column prop="createdAt" label="记录创建时间" sortable="custom" show-overflow-tooltip /> -->
				<el-table-column prop="url" label="客户在合作伙伴平台的地址" show-overflow-tooltip />
				<el-table-column label="操作" width="150">
					<template #default="scope">
						<el-button icon="edit-pen" text type="primary" v-auth="'rs_customerPartner_edit'" @click="formDialogRef.openDialog(scope.row.relationId)"
							>编辑</el-button
						>
						<el-button icon="delete" text type="primary" v-auth="'rs_customerPartner_del'" @click="handleDelete([scope.row.relationId])"
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
			url="/rs/customerPartner/import"
			temp-url="/admin/sys-file/local/file/customerPartner.xlsx"
			@refreshDataList="getDataList"
		/>
	</div>
</template>

<script setup lang="ts" name="systemCustomerPartner">
import { BasicTableProps, useTable } from '/@/hooks/table';
import { fetchNewList, delObjs } from '/@/api/rs/customerPartner';
import { useMessage, useMessageBox } from '/@/hooks/message';
import { onMounted, ref, reactive } from 'vue';
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
	downBlobFile('/rs/customerPartner/export', Object.assign(state.queryForm, { ids: selectObjs }), 'customerPartner.xlsx');
};

// 多选事件
const selectionChangHandle = (objs: { relationId: string }[]) => {
	selectObjs.value = objs.map(({ relationId }) => relationId);
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
		await fetchNewList(state.queryForm).then((res) => {
			state.dataList = res.data.data;
			state.pagination = res.data.page;
			// console.log('111', state.pagination);
		});

		// state.dataList = response.data; // 假设返回的数据在 response.data 中
		// console.log('111111', state.dataList);
	} catch (error) {
		console.error('Error loading data:', error);
	} finally {
		state.loading = false;
	}
};
//在组件挂载时获取数据
onMounted(() => {
	loadData();
});
</script>
