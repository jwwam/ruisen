<template>
  <div class="layout-padding">
    <div class="layout-padding-auto layout-padding-view">
      <el-row v-show="showSearch">
        <el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
      <el-form-item label="合作伙伴ID" prop="partnerId">
            <el-select v-model="state.queryForm.partnerId" placeholder="请选择合作伙伴ID">
              <el-option label="请选择">0</el-option>
            </el-select>
      </el-form-item>
      <el-form-item label="分成比例名称" prop="name" >
        <el-input placeholder="请输入分成比例名称" v-model="state.queryForm.name" />
      </el-form-item>
      <el-form-item label="是否启用" prop="isActive">
            <el-select v-model="state.queryForm.isActive" placeholder="请选择是否启用">
              <el-option :label="item.label" :value="item.value" v-for="(item, index) in yes_no_type" :key="index"></el-option>
            </el-select>
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
            v-auth="'rs_revenueShares_add'">
            新 增
          </el-button>
          <el-button plain icon="upload-filled" type="primary" class="ml10" @click="excelUploadRef.show()" v-auth="'sys_user_add'">
						导 入
					</el-button>
          <el-button plain :disabled="multiple" icon="Delete" type="primary"
            v-auth="'rs_revenueShares_del'" @click="handleDelete(selectObjs)">
            删 除
          </el-button>
          <right-toolbar v-model:showSearch="showSearch" :export="'rs_revenueShares_export'"
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
          <el-table-column prop="partnerId" label="合作伙伴ID"  show-overflow-tooltip/>
          <el-table-column prop="name" label="分成比例名称"  show-overflow-tooltip/>
          <el-table-column prop="share" label="分成比例" show-overflow-tooltip>
            <template #default="scope">
              {{ scope.row.share }}%
            </template>
          </el-table-column>
          <el-table-column prop="description" label="分成比例描述"  show-overflow-tooltip/>
          <el-table-column prop="validDays" label="有效天数"  show-overflow-tooltip/>
          <el-table-column prop="isActive" label="是否启用" show-overflow-tooltip>
      <template #default="scope">
                <el-button 
                  :type="scope.row.isActive ? 'success' : 'danger'" 
                  disabled
                >
                  {{ scope.row.isActive ? '启用' : '禁用' }}
                </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="sortOrder" label="排序顺序" sortable="custom" show-overflow-tooltip/>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button icon="edit-pen" text type="primary" v-auth="'rs_revenueShares_edit'"
              @click="formDialogRef.openDialog(scope.row.shareId)">编辑</el-button>
            <el-button icon="delete" text type="primary" v-auth="'rs_revenueShares_del'" @click="handleDelete([scope.row.shareId])">删除</el-button>
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
			url="/rs/revenueShares/import"
      temp-url="/admin/sys-file/local/file/revenueShares.xlsx"
			@refreshDataList="getDataList"
		/>
  </div>
</template>

<script setup lang="ts" name="systemRevenueShares">
import { BasicTableProps, useTable } from "/@/hooks/table";
import { fetchList, delObjs } from "/@/api/rs/revenueShares";
import { useMessage, useMessageBox } from "/@/hooks/message";
import { useDict } from '/@/hooks/dict';

// 引入组件
const FormDialog = defineAsyncComponent(() => import('./form.vue'));
// 定义查询字典

const { yes_no_type } = useDict('yes_no_type')
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
  downBlobFile('/rs/revenueShares/export', Object.assign(state.queryForm, { ids: selectObjs }), 'revenueShares.xlsx')
}

// 多选事件
const selectionChangHandle = (objs: { shareId: string }[]) => {
  selectObjs.value = objs.map(({ shareId }) => shareId);
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
