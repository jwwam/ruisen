<template>
	<div class="layout-padding">
		<div class="layout-padding-auto layout-padding-view">
			<el-row v-show="showSearch">
				<el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
					<!-- <el-form-item label="提交人" prop="submitterId">
						<el-select v-model="state.queryForm.submitterId" placeholder="请选择提交人" filterable>
							<el-option v-for="user in users" :key="user.userId" :label="user.name" :value="user.userId"></el-option>
						</el-select>
					</el-form-item> -->
					<el-form-item label="工单分类" prop="category">
						<el-input v-model="state.queryForm.category" placeholder="请输入工单分类" clearable style="width: 180px" />
					</el-form-item>
					<el-form-item label="工单状态" prop="status">
						<el-select v-model="state.queryForm.status" placeholder="请选择工单状态" filterable>
							<el-option :label="item.label" :value="item.value" v-for="(item, index) in work_status" :key="index"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="处理人" prop="assignees">
						<el-select v-model="state.queryForm.assignees" placeholder="请选择处理人" filterable>
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
					<!-- <el-button plain icon="upload-filled" type="primary" class="ml10" @click="excelUploadRef.show()" v-auth="'sys_user_add'"> 导 入 </el-button>
					<el-button plain :disabled="multiple" icon="Delete" type="primary" v-auth="'rs_work_del'" @click="handleDelete(selectObjs)">
						删 除
					</el-button> -->
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
				<el-table-column prop="title" label="工单标题" width="200" show-overflow-tooltip />
				<el-table-column prop="content" label="内容" width="200" show-overflow-tooltip />
				<el-table-column prop="status" label="状态" show-overflow-tooltip>
					<template #default="scope">
						<dict-tag :options="work_status" :value="scope.row.status"></dict-tag>
					</template>
				</el-table-column>
				<el-table-column prop="assignees" label="处理人" show-overflow-tooltip />
				<el-table-column prop="attachments" label="附件" width="300" show-overflow-tooltip>
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
				<el-table-column prop="copy" label="抄送人" width="180" show-overflow-tooltip />
				<el-table-column prop="createdAt" label="创建时间" width="170" show-overflow-tooltip />
				<el-table-column prop="deadline" label="截止日期" width="100" show-overflow-tooltip />
				<el-table-column prop="customerName" label="客户名称" width="100" show-overflow-tooltip />
				<el-table-column prop="partnerCode" label="合作伙伴" width="100" show-overflow-tooltip />
				<el-table-column prop="priority" label="优先级" show-overflow-tooltip />
				<!-- 处理时间和处理状态列只在状态为2时显示 -->
				<el-table-column prop="handleTime" label="处理时间" width="170" show-overflow-tooltip>
					<template #default="scope">
						{{ scope.row.handleTime || '-' }}
					</template>
				</el-table-column>
				<el-table-column prop="handleOpinion" label="处理意见" width="120" show-overflow-tooltip>
					<template #default="scope">
						{{ scope.row.handleOpinion || '-' }}
					</template>
				</el-table-column>
				<el-table-column label="操作" width="150" fixed="right">
					<template #default="scope">
						<el-button type="primary" size="small" link icon="View" @click="view(scope.row)"> 查看 </el-button>
						<el-button type="danger" size="small" link icon="CircleClose" @click="handleTerminate(scope.row)">终止</el-button>
						<!-- <el-button icon="edit-pen" text type="primary" v-auth="'rs_work_edit'" @click="formDialogRef.openDialog(scope.row.workId)"
							>编辑</el-button
						> -->
						<!-- <el-button icon="delete" text type="primary" v-auth="'rs_work_del'" @click="handleDelete([scope.row.workId])">删除</el-button> -->
					</template>
				</el-table-column>
			</el-table>
			<pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" v-bind="state.pagination" />
			<!-- <el-drawer v-model="rightDrawerVisible" direction="rtl" size="400px">
				<template #header>
					<h3>{{ currentData?.name }}</h3>
				</template>
				<template #default>
					<el-card class="box-card">
						
					</el-card>
				</template>
			</el-drawer> -->
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
import { WorkfetchList, delObjs, getObj, putObj } from '/@/api/rs/work';
import { useMessage, useMessageBox } from '/@/hooks/message';
import { useDict } from '/@/hooks/dict';
import { pageRoleList } from '/@/api/admin/user';

// const rightDrawerVisible = ref(false);
// const currentData = ref();
// const currentOpenFlowForm = ref();

//  const deal = (row) => {
// 	//当前选中数据
// 	currentData.value = row;
// 	//请求工单详情接口
// 	getObj({
// 		workId: row.workId,
// 	}).then((res) => {
// 		//数据详情
// 		currentOpenFlowForm.value = res.data[0];
// 		rightDrawerVisible.value = true;
// 	});
// };

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
	dataList: [], // 用于存储获取到的数据
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
		const response = await pageRoleList();
		users.value = response.data.records; // 假设返回的数据结构中用户列表在`records`字段中
	} catch (error) {
		console.error('Failed to fetch users:', error);
	}
};
// 定义获取数据的函数
const loadData = async () => {
	state.loading = true;
	try {
		await WorkfetchList(state.queryForm).then((res) => {
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
// 在组件挂载时获取数据
onMounted(() => {
	loadData();
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

// 在 script setup 中添加下载方法
const handleFileDownload = async (file: any) => {
	try {
		const url = `/admin/sys-file/${file.bucketName}/${file.fileName}`; // 构建下载URL
		const query = {}; // 如果需要传递额外参数（如token）可以在这里添加
		await downBlobFile(url, query, file.original);
	} catch (error) {
		console.error('文件下载失败:', error);
	}
};

// 查看工单详情
const view = (row: any) => {
	// 打开表单对话框,传入只读模式参数
	formDialogRef.value?.openDialog(row.workId, true);
};

// 添加终止工单方法
const handleTerminate = async (row: any) => {
	try {
		await useMessageBox().confirm('确认要终止该工单吗？');
		await putObj({
			...row,
			status: 3,
		});
		useMessage().success('工单已终止');
		getDataList();
	} catch (err: any) {
		if (err?.msg) {
			useMessage().error(err.msg);
		}
	}
};
</script>
