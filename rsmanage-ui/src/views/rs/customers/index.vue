<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row v-show="showSearch">
				<el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
					<el-form-item label="客户名称" prop="name">
						<el-input placeholder="" v-model="state.queryForm.name" />
					</el-form-item>
					<el-form-item label="联系人" prop="linkman">
						<el-input placeholder="" v-model="state.queryForm.linkman" />
					</el-form-item>
					<el-form-item label="客户电子邮件" prop="email">
						<el-input placeholder="" v-model="state.queryForm.email" />
					</el-form-item>
					<el-form-item label="客户电话号码" prop="phoneNumber">
						<el-input placeholder="" v-model="state.queryForm.phoneNumber" />
					</el-form-item>
					<el-form-item label="我方商务" prop="salesRepId">
						<el-select v-model="state.queryForm.salesRepId" placeholder="" filterable>
							<el-option v-for="user in users" :key="user.userId" :label="user.name" :value="user.name" />
						</el-select>
					</el-form-item>
					<el-form-item>
						<el-button icon="search" type="primary" @click="getDataList"> 查 询 </el-button>
						<el-button icon="Refresh" @click="resetQuery">重 置</el-button>
					</el-form-item>
				</el-form>
			</el-row>
			<el-row>
				<div class="mb8" style="width: 100%">
					<el-button icon="folder-add" type="primary" class="ml10" @click="formDialogRef.openDialog()" v-auth="'rs_customers_add'"> 新 增 </el-button>
					<el-button plain icon="upload-filled" type="primary" class="ml10" @click="excelUploadRef.show()" v-auth="'sys_user_add'"> 导 入 </el-button>
					<el-button plain :disabled="multiple" icon="Delete" type="primary" v-auth="'rs_customers_del'" @click="handleDelete(selectObjs)">
						删 除
					</el-button>
					<right-toolbar
						v-model:showSearch="showSearch"
						:export="'rs_customers_export'"
						@exportExcel="exportExcel"
						class="ml10 mr20"
						style="float: right"
						@queryTable="getDataList"
					></right-toolbar>
				</div>
			</el-row>
			<!-- size="small" :scroll="{ x: 1000 }" -->
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
				<el-table-column prop="name" label="客户名称" show-overflow-tooltip />
				<el-table-column prop="isOurCustomer" label="是否我方客户" width="120" align="center">
					<template #default="scope">
						<el-tag :type="scope.row.isOurCustomer ? 'success' : 'info'">
							{{ scope.row.isOurCustomer ? '是' : '否' }}
						</el-tag>
					</template>
				</el-table-column>
				<el-table-column prop="linkman" label="联系人" show-overflow-tooltip />
				<el-table-column prop="email" label="客户电子邮件" show-overflow-tooltip />
				<el-table-column prop="phoneNumber" label="客户电话号码" show-overflow-tooltip />
				<el-table-column prop="salesRepId" label="我方商务人员" show-overflow-tooltip />
				<el-table-column prop="companyName" label="客户主体" show-overflow-tooltip />
				<el-table-column prop="financeContactUser" label="财务人员" show-overflow-tooltip />
				<el-table-column prop="financeEmail" label="财务人员邮箱" show-overflow-tooltip />
				<el-table-column prop="financePhone" label="财务人员电话" show-overflow-tooltip />
				<el-table-column label="操作" width="150">
					<template #default="scope">
						<el-button icon="edit-pen" text type="primary" v-auth="'rs_customers_edit'" @click="formDialogRef.openDialog(scope.row.customerId)"
							>编辑</el-button
						>
						<el-button icon="delete" text type="primary" v-auth="'rs_customers_del'" @click="handleDelete([scope.row.customerId])">删除</el-button>
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
			url="/rs/customers/import"
			temp-url="/admin/sys-file/local/file/customers.xlsx"
			@refreshDataList="getDataList"
		/>
	</div>
</template>

<script setup lang="ts" name="systemCustomers">
import { BasicTableProps, useTable } from '/@/hooks/table';
import { fetchList, delObjs } from '/@/api/rs/customers';
import { useMessage, useMessageBox } from '/@/hooks/message';
import { useDict } from '/@/hooks/dict';
import { pageSalesRepList } from '/@/api/admin/user';

// 引入组件
const FormDialog = defineAsyncComponent(() => import('./form.vue'));
// 定义查询字典
// 定义对象的类型
interface Users {
	isAdmin: string;
	userId: string;
	name: string;
}
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
	pageList: fetchList,
});

//  table hook
const { getDataList, currentChangeHandle, sizeChangeHandle, sortChangeHandle, downBlobFile, tableStyle } = useTable(state);

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
	downBlobFile('/rs/customers/export', Object.assign(state.queryForm, { ids: selectObjs }), 'customers.xlsx');
};

// 多选事件
const selectionChangHandle = (objs: { customerId: string }[]) => {
	selectObjs.value = objs.map(({ customerId }) => customerId);
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
const users = ref<Users[]>([]);

const fetchUsers = async () => {
	try {
		const response = await pageSalesRepList();
		users.value = response.data.records; // 假设返回的数据结构中用户列表在`records`字段中
	} catch (error) {
		console.error('Failed to fetch users:', error);
	}
};

onMounted(() => {
	fetchUsers();
});
</script>
