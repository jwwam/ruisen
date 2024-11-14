<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row v-show="showSearch">
				<el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
					<el-form-item label="合同编号" prop="contractNumber">
						<el-input placeholder="" v-model="state.queryForm.contractNumber" />
					</el-form-item>
					<el-form-item label="合同名称" prop="contractName">
						<el-input v-model="state.queryForm.contractName" placeholder="" />
					</el-form-item>
					<el-form-item label="客户名称" prop="customerId">
						<el-select v-model="state.queryForm.customerId" placeholder="" filterable>
							<el-option v-for="customer in customers" :key="customer.customerId" :label="customer.name" :value="customer.customerId" />
						</el-select>
					</el-form-item>
					<el-form-item label="合同是否生效" prop="isActive">
						<el-select v-model="state.queryForm.isActive" placeholder="">
							<el-option :label="item.label" :value="item.value" v-for="(item, index) in yes_no_type" :key="index"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="签署日期" prop="signedDate">
						<el-date-picker
							type="date"
							placeholder=""
							v-model="state.queryForm.signedDate"
							value-format="YYYY-MM-DD"
							:default-time="new Date(2000, 0, 1, 0, 0, 0)"
						></el-date-picker>
					</el-form-item>
					<el-form-item>
						<el-button icon="search" type="primary" @click="getDataList"> 查 询 </el-button>
						<el-button icon="Refresh" @click="resetQuery">重 置</el-button>
					</el-form-item>
				</el-form>
			</el-row>
			<el-row>
				<div class="mb8" style="width: 100%">
					<el-button icon="folder-add" type="primary" class="ml10" @click="handleAdd" v-auth="'rs_contract_add'"> 新 增 </el-button>
					<el-button plain icon="upload-filled" type="primary" class="ml10" @click="excelUploadRef.show()" v-auth="'sys_user_add'"> 导 入 </el-button>
					<el-button plain :disabled="multiple" icon="Delete" type="primary" v-auth="'rs_contract_del'" @click="handleDelete(selectObjs)">
						删 除
					</el-button>
					<right-toolbar
						v-model:showSearch="showSearch"
						:export="'rs_contract_export'"
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
				style="width: 100%"
			>
				<el-table-column type="selection" width="40" align="center" fixed="left" />
				<el-table-column type="index" label="#" width="40" fixed="left" />
				<el-table-column prop="contractNumber" label="合同编号" min-width="150" show-overflow-tooltip />
				<el-table-column prop="contractName" label="合同名称" min-width="150" show-overflow-tooltip />
				<el-table-column prop="customerName" label="客户名称" min-width="150" show-overflow-tooltip />
				<el-table-column prop="isActive" label="合同是否生效" min-width="110" show-overflow-tooltip>
					<template #default="scope">
						<dict-tag :options="yes_no_type" :value="scope.row.isActive"></dict-tag>
					</template>
				</el-table-column>
				<el-table-column prop="signedDate" label="签署日期" min-width="120" show-overflow-tooltip />
				<el-table-column prop="fileUrl" label="合同文件" min-width="300">
					<template #default="scope">
						<template v-if="scope.row.attachmentsList && scope.row.attachmentsList.length">
							<el-tooltip v-for="(file, index) in scope.row.attachmentsList" :key="file.id" :content="file.original" placement="top">
								<el-link type="primary" :underline="false" style="margin-right: 5px" @click="handleFileDownload(file)">
									{{ file.original }}{{ index < scope.row.attachmentsList.length - 1 ? ',' : '' }}
								</el-link>
							</el-tooltip>
						</template>
						<span v-else>-</span>
					</template>
				</el-table-column>
				<el-table-column prop="payeeName" label="收款人名称" min-width="120" show-overflow-tooltip />
				<el-table-column prop="payeeAccount" label="收款人账号" min-width="200" show-overflow-tooltip />
				<el-table-column prop="bankName" label="银行名称" min-width="150" show-overflow-tooltip />
				<el-table-column prop="bankAddress" label="银行地址" min-width="200" show-overflow-tooltip />
				<el-table-column prop="swiftCode" label="SwiftCode" min-width="150" show-overflow-tooltip />
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<template v-if="canEdit(scope.row)">
							<el-button icon="edit-pen" text type="primary" v-auth="'rs_contract_edit'" @click="formDialogRef.openDialog(scope.row.contractId)"
								>编辑</el-button
							>
							<el-button icon="delete" text type="primary" v-auth="'rs_contract_del'" @click="handleDelete([scope.row.contractId])">删除</el-button>
						</template>
						<template v-else>
							<el-button icon="view" text type="primary" @click="formDialogRef.openDialog(scope.row.contractId, true)">查看</el-button>
						</template>
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
			url="/rs/contract/import"
			temp-url="/admin/sys-file/local/file/contract.xlsx"
			@refreshDataList="getDataList"
		/>
	</div>
</template>

<script setup lang="ts" name="systemContract">
import { BasicTableProps, useTable } from '/@/hooks/table';
import { fetchNewList, delObjs } from '/@/api/rs/contract';
import { useMessage, useMessageBox } from '/@/hooks/message';
import { useDict } from '/@/hooks/dict';
import { fetchListWithoutRole as fetchCustomerList } from '/@/api/rs/customers';
import dayjs from 'dayjs';
import { useUserInfo } from '/@/stores/userInfo';

const FormDialog = defineAsyncComponent(() => import('./form.vue'));

// 定义客户对象的类型
interface Customer {
	customerId: string;
	name: string;
}
const { yes_no_type } = useDict('yes_no_type');
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
const customers = ref<Customer[]>([]); // 使用定义的类型
const fetchCustomers = async () => {
	try {
		const response = await fetchCustomerList();
		customers.value = response.data.records as Customer[]; // 假设返回的数据结构中客户列表在`records`字段中
	} catch (error) {
		console.error('Failed to fetch customers:', error);
	}
};

// 定义获取数据的函数
const loadData = async () => {
	state.loading = true;
	try {
		const queryData = { ...state.queryForm };
		if (queryData.signedDate) {
			// 确保日期格式正确
			queryData.signedDate = dayjs(queryData.signedDate).format('YYYY-MM-DD');
		}
		await fetchNewList(queryData).then((res) => {
			state.dataList = res.data.data;
			state.pagination = res.data.page;
		});
	} catch (error) {
		console.error('Error loading data:', error);
	} finally {
		state.loading = false;
	}
};
// 在组件挂载时获取数据

// 获取当前用户信息
const currentUserId = ref('');

const fetchCurrentUser = () => {
	const data = useUserInfo().userInfos;
	currentUserId.value = data.user.userId;
};

// 判断是否可以编辑
const canEdit = (row: any) => {
	return currentUserId.value === row.createUserId;
};

onMounted(() => {
	fetchCurrentUser();
	fetchCustomers();
	loadData();
});

// 导出excel
const exportExcel = () => {
	downBlobFile('/rs/contract/export', Object.assign(state.queryForm, { ids: selectObjs }), 'contract.xlsx');
};

// 多选事件
const selectionChangHandle = (objs: { contractId: string }[]) => {
	selectObjs.value = objs.map(({ contractId }) => contractId);
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

// 新增按钮处理函数
const handleAdd = () => {
	formDialogRef.value.openDialog();
};

// 添加文件下载处理函数
const handleFileDownload = async (file: any) => {
	try {
		const url = `/admin/sys-file/${file.bucketName}/${file.fileName}`; // 构建下载URL
		const query = {}; // 如果需要传递额外参数可以在这里添加
		await downBlobFile(url, query, file.original);
	} catch (error) {
		console.error('文件下载失败:', error);
		useMessage().error('文件下载失败');
	}
};
</script>
