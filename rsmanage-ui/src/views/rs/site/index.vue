<template>
  <div class="layout-padding">
    <div class="layout-padding-auto layout-padding-view">
      <el-row v-show="showSearch">
        <el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
      <el-form-item label="域名" prop="domain" >
        <el-input placeholder="请输入域名" v-model="state.queryForm.domain" />
      </el-form-item>
      <el-form-item label="状态 1-正常 2-禁用" prop="status" >
        <el-input placeholder="请输入状态 1-正常 2-禁用" v-model="state.queryForm.status" />
      </el-form-item>
      <el-form-item label="客户ID" prop="customerId">
            <el-select v-model="state.queryForm.customerId" placeholder="请选择客户ID">
              <el-option label="请选择">0</el-option>
            </el-select>
      </el-form-item>
      <el-form-item label="是否借用账号 0-否 1-是" prop="isBorrowedAccount">
            <el-select v-model="state.queryForm.isBorrowedAccount" placeholder="请选择是否借用账号 0-否 1-是">
              <el-option :label="item.label" :value="item.value" v-for="(item, index) in yes_no_type" :key="index"></el-option>
            </el-select>
      </el-form-item>
      <el-form-item label="是否放链接 0-否 1-是" prop="hasLink">
            <el-select v-model="state.queryForm.hasLink" placeholder="请选择是否放链接 0-否 1-是">
              <el-option :label="item.label" :value="item.value" v-for="(item, index) in yes_no_type" :key="index"></el-option>
            </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createdAt">
      <el-date-picker type="date" placeholder="请输入创建时间" v-model="state.queryForm.createdAt"></el-date-picker>
      </el-form-item>
          <el-form-item>
            <el-button icon="search" type="primary" @click="getDataList">
              查 询
            </el-button>
            <el-button icon="Refresh" @click="resetQuery">重 置</el-button>
          </el-form-item>
        </el-form>
      </el-row>
      <el-row>
        <div class="mb8" style="width: 100%">
          <el-button icon="folder-add" type="primary" class="ml10" @click="formDialogRef.openDialog()"
            v-auth="'rs_site_add'">
            新 增
          </el-button>
          <el-button plain icon="upload-filled" type="primary" class="ml10" @click="excelUploadRef.show()" v-auth="'sys_user_add'">
						导 入
					</el-button>
          <el-button plain :disabled="multiple" icon="Delete" type="primary"
            v-auth="'rs_site_del'" @click="handleDelete(selectObjs)">
            删 除
          </el-button>
          <right-toolbar v-model:showSearch="showSearch" :export="'rs_site_export'"
                @exportExcel="exportExcel" class="ml10 mr20" style="float: right;"
            @queryTable="getDataList"></right-toolbar>
        </div>
      </el-row>
      <el-table :data="state.dataList" v-loading="state.loading" border 
        :cell-style="tableStyle.cellStyle" :header-cell-style="tableStyle.headerCellStyle"
				@selection-change="selectionChangHandle"
        @sort-change="sortChangeHandle">
        <el-table-column type="selection" width="40" align="center" />
        <el-table-column type="index" label="#" width="40" />
          <el-table-column prop="domain" label="域名"  show-overflow-tooltip/>
          <el-table-column prop="status" label="状态 1-正常 2-禁用" show-overflow-tooltip>
      <template #default="scope">
                <dict-tag :options="site_status" :value="scope.row.status"></dict-tag>
            </template>
          </el-table-column>
          <el-table-column prop="customerId" label="客户ID"  show-overflow-tooltip/>
          <el-table-column prop="partnerIds" label="上游合作伙伴ID列表，多个用逗号分隔"  show-overflow-tooltip/>
          <el-table-column prop="isBorrowedAccount" label="是否借用账号 0-否 1-是" show-overflow-tooltip>
      <template #default="scope">
                <dict-tag :options="yes_no_type" :value="scope.row.isBorrowedAccount"></dict-tag>
            </template>
          </el-table-column>
          <el-table-column prop="borrowedCustomerId" label="借用账号客户ID"  show-overflow-tooltip/>
          <el-table-column prop="gamEmailId" label="客户GAM邮箱"  show-overflow-tooltip/>
          <el-table-column prop="hasLink" label="是否放链接 0-否 1-是" show-overflow-tooltip>
      <template #default="scope">
                <dict-tag :options="yes_no_type" :value="scope.row.hasLink"></dict-tag>
            </template>
          </el-table-column>
          <el-table-column prop="reviewAccount" label="站点提审账号"  show-overflow-tooltip/>
          <el-table-column prop="createdAt" label="创建时间" sortable="custom" show-overflow-tooltip/>
          <el-table-column prop="updatedAt" label="更新时间"  show-overflow-tooltip/>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button icon="edit-pen" text type="primary" v-auth="'rs_site_edit'"
              @click="formDialogRef.openDialog(scope.row.id)">编辑</el-button>
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
import { BasicTableProps, useTable } from "/@/hooks/table";
import { fetchList, delObjs } from "/@/api/rs/site";
import { useMessage, useMessageBox } from "/@/hooks/message";
import { useDict } from '/@/hooks/dict';

// 引入组件
const FormDialog = defineAsyncComponent(() => import('./form.vue'));
// 定义查询字典

const { site_status,yes_no_type } = useDict('site_status','yes_no_type')
// 定义变量内容
const formDialogRef = ref()
const excelUploadRef = ref();
// 搜索变量
const queryRef = ref()
const showSearch = ref(true)
// 多选变量
const selectObjs = ref([]) as any
const multiple = ref(true)

const state: BasicTableProps = reactive<BasicTableProps>({
  queryForm: {},
  pageList: fetchList
})

//  table hook
const {
  getDataList,
  currentChangeHandle,
  sizeChangeHandle,
  sortChangeHandle,
  downBlobFile,
	tableStyle
} = useTable(state)

// 清空搜索条件
const resetQuery = () => {
  // 清空搜索条件
  queryRef.value?.resetFields()
  // 清空多选
  selectObjs.value = []
  getDataList()
}

// 导出excel
const exportExcel = () => {
  downBlobFile('/rs/site/export', Object.assign(state.queryForm, { ids: selectObjs }), 'site.xlsx')
}

// 多选事件
const selectionChangHandle = (objs: { id: string }[]) => {
  selectObjs.value = objs.map(({ id }) => id);
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
</script>
