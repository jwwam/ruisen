<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row v-show="showSearch">
				<el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
					<el-form-item label="域名" prop="domain">
						<el-input placeholder="请输入域名" v-model="state.queryForm.domain" />
					</el-form-item>
					<el-form-item label="状态" prop="status">
						<el-select v-model="state.queryForm.status" placeholder="请选择状态">
							<el-option :label="item.label" :value="item.value" v-for="(item, index) in site_status" :key="index"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="客户名称" prop="customerId">
						<el-select v-model="state.queryForm.customerId" placeholder="请选择客户姓名" filterable>
							<el-option v-for="customer in customers" :key="customer.customerId" :label="customer.name" :value="customer.customerId" />
						</el-select>
					</el-form-item>
					<el-form-item label="是否放链接" prop="hasLink">
						<el-select v-model="state.queryForm.hasLink" placeholder="请选择是否放链接">
							<el-option :label="item.label" :value="item.value" v-for="(item, index) in yes_no_type" :key="index"></el-option>
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
					<el-button icon="folder-add" type="primary" class="ml10" @click="formDialogRef.openDialog()" v-auth="'rs_site_add'"> 新 增 </el-button>
					<!-- <el-button plain icon="upload-filled" type="primary" class="ml10" @click="excelUploadRef.show()" v-auth="'sys_user_add'"> 导 入 </el-button> -->
					<el-button plain :disabled="multiple" icon="Delete" type="primary" v-auth="'rs_site_del'" @click="handleDelete(selectObjs)">
						删 除
					</el-button>
					<right-toolbar
						v-model:showSearch="showSearch"
						:export="'rs_site_export'"
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
				<el-table-column prop="domain" label="域名" show-overflow-tooltip />
				<el-table-column prop="status" label="状态" show-overflow-tooltip>
					<template #default="scope">
						<dict-tag :options="site_status" :value="scope.row.status"></dict-tag>
					</template>
				</el-table-column>
				<el-table-column prop="customerName" label="客户" show-overflow-tooltip />
				<el-table-column prop="partnerIds" label="上游合作伙伴" show-overflow-tooltip />
				<el-table-column prop="isBorrowedAccount" label="是否借用账号" show-overflow-tooltip>
					<template #default="scope">
						<dict-tag :options="yes_no_type" :value="scope.row.isBorrowedAccount"></dict-tag>
					</template>
				</el-table-column>
				<el-table-column prop="borrowedCustomerName" label="借用账号客户" show-overflow-tooltip />
				<el-table-column prop="email" label="客户GAM" show-overflow-tooltip />
				<el-table-column prop="hasLink" label="是否放链接" show-overflow-tooltip>
					<template #default="scope">
						<dict-tag :options="yes_no_type" :value="scope.row.hasLink"></dict-tag>
					</template>
				</el-table-column>
				<el-table-column prop="reviewAccount" label="站点提审账号" show-overflow-tooltip />
				<el-table-column label="操作" width="150">
					<template #default="scope">
						<el-button icon="edit-pen" text type="primary" v-auth="'rs_site_edit'" @click="formDialogRef.openDialog(scope.row)">编辑</el-button>
						<el-button icon="delete" text type="primary" v-auth="'rs_site_del'" @click="handleDelete([scope.row.id])">删除</el-button>
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
			url="/rs/site/import"
			temp-url="/admin/sys-file/local/file/site.xlsx"
			@refreshDataList="getDataList"
		/>
	</div>
</template>

<script setup lang="ts" name="systemSite">
import { BasicTableProps, useTable } from '/@/hooks/table';
import { fetchNewList, delObjs } from '/@/api/rs/site';
import { useMessage, useMessageBox } from '/@/hooks/message';
import { useDict } from '/@/hooks/dict';
import { fetchListWithoutRole as fetchCustomerList } from '/@/api/rs/customers';

// 引入组件
const FormDialog = defineAsyncComponent(() => import('./form.vue'));

// 定义客户对象的类型
interface Customer {
	customerId: string;
	name: string;
}
const { site_status, yes_no_type } = useDict('site_status', 'yes_no_type');
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
	// pageList: fetchList,
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
	downBlobFile('/rs/site/export', Object.assign(state.queryForm, { ids: selectObjs }), 'site.xlsx');
};

// 多选事件
const selectionChangHandle = (objs: { id: string }[]) => {
	selectObjs.value = objs.map(({ id }) => id);
	multiple.value = !objs.length;
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
onMounted(() => {
	fetchCustomers();
	loadData();
});
</script>
