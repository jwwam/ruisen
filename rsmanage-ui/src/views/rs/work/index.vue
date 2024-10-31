<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row v-show="showSearch">
				<el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
					<el-form-item label="提交人ID" prop="submitterId">
						<el-select v-model="state.queryForm.submitterId" placeholder="请选择提交人ID">
							<el-option v-for="user in users" :key="user.userId" :label="user.name" :value="user.userId"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="工单标题" prop="title">
						<el-input placeholder="请输入工单标题" v-model="state.queryForm.title" />
					</el-form-item>
					<el-form-item label="工单状态" prop="status">
						<el-select v-model="state.queryForm.status" placeholder="请选择工单状态">
							<el-option :label="item.label" :value="item.value" v-for="(item, index) in work_status" :key="index"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="处理人ID" prop="assignees">
						<el-select v-model="state.queryForm.assignees" placeholder="请选择处理人ID">
							<el-option v-for="user in users" :key="user.userId" :label="user.name" :value="user.userId"></el-option>
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
					<el-button icon="folder-add" type="primary" class="ml10" @click="formDialogRef.openDialog()" v-auth="'rs_work_add'"> 新 增 </el-button>
					<el-button plain icon="upload-filled" type="primary" class="ml10" @click="excelUploadRef.show()" v-auth="'sys_user_add'"> 导 入 </el-button>
					<el-button plain :disabled="multiple" icon="Delete" type="primary" v-auth="'rs_work_del'" @click="handleDelete(selectObjs)">
						删 除
					</el-button>
					<right-toolbar
						v-model:showSearch="showSearch"
						:export="'rs_work_export'"
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
				<el-table-column prop="submitterId" label="提交人ID" show-overflow-tooltip />
				<el-table-column prop="title" label="工单标题" show-overflow-tooltip />
				<el-table-column prop="content" label="工单内容" show-overflow-tooltip />
				<el-table-column prop="status" label="工单状态" show-overflow-tooltip>
					<template #default="scope">
						<dict-tag :options="work_status" :value="scope.row.status"></dict-tag>
					</template>
				</el-table-column>
				<el-table-column prop="assignees" label="处理人ID" show-overflow-tooltip />
				<el-table-column prop="attachments" label="附件路径列表" show-overflow-tooltip />
				<el-table-column prop="createdAt" label="创建时间" sortable="custom" show-overflow-tooltip />
				<el-table-column prop="updatedAt" label="更新时间" show-overflow-tooltip />
				<el-table-column label="操作" width="150">
					<template #default="scope">
						<el-button icon="edit-pen" text type="primary" v-auth="'rs_work_edit'" @click="formDialogRef.openDialog(scope.row.workId)"
							>编辑</el-button
						>
						<el-button icon="delete" text type="primary" v-auth="'rs_work_del'" @click="handleDelete([scope.row.workId])">删除</el-button>
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
			url="/rs/work/import"
			temp-url="/admin/sys-file/local/file/work.xlsx"
			@refreshDataList="getDataList"
		/>
	</div>
</template>

<script setup lang="ts" name="systemWork">
import { BasicTableProps, useTable } from '/@/hooks/table';
import { fetchList, delObjs } from '/@/api/rs/work';
import { useMessage, useMessageBox } from '/@/hooks/message';
import { useDict } from '/@/hooks/dict';
import { pageList } from '/@/api/admin/user';

// 引入组件
const FormDialog = defineAsyncComponent(() => import('./form.vue'));
// 定义对象的类型
interface Users {
	isAdmin: string;
	userId: string;
	name: string;
}
const { work_status } = useDict('work_status');
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
	downBlobFile('/rs/work/export', Object.assign(state.queryForm, { ids: selectObjs }), 'work.xlsx');
};

// 多选事件
const selectionChangHandle = (objs: { workId: string }[]) => {
	selectObjs.value = objs.map(({ workId }) => workId);
	multiple.value = !objs.length;
};
const users = ref<Users[]>([]);

const fetchUsers = async () => {
	try {
		const response = await pageList();
		users.value = response.data.records; // 假设返回的数据结构中用户列表在`records`字段中
	} catch (error) {
		console.error('Failed to fetch users:', error);
	}
};

onMounted(() => {
	fetchUsers();
});
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
</script>
