<template>
  <div class="layout-padding">
    <div class="layout-padding-auto layout-padding-view">
      <el-row v-show="showSearch">
        <el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
      <el-form-item label="合作伙伴名称" prop="partnerName" >
        <el-input placeholder="请输入合作伙伴名称" v-model="state.queryForm.partnerName" />
      </el-form-item>
      <el-form-item label="合作伙伴标识" prop="partnerCode" >
        <el-input placeholder="请输入合作伙伴标识" v-model="state.queryForm.partnerCode" />
      </el-form-item>
      <!-- <el-form-item label="请输入分成比例" prop="revenueShare" >
        <el-input placeholder="请输入分成比例" v-model="state.queryForm.revenueShare" />
      </el-form-item> -->
      <el-form-item label="合作开始时间" prop="startDate">
      <el-date-picker type="date" placeholder="请输入合作开始时间" v-model="state.queryForm.startDate"></el-date-picker>
      </el-form-item>
      <el-form-item label="合作结束时间" prop="endDate">
      <el-date-picker type="date" placeholder="请输入合作结束时间" v-model="state.queryForm.endDate"></el-date-picker>
      </el-form-item>
      <!-- <el-form-item label="分成有效天数" prop="validDays" >
        <el-input placeholder="请输入分成有效天数" v-model="state.queryForm.validDays" />
      </el-form-item> -->
      <el-form-item label="分成有效月数" prop="validMonths">
        <el-select v-model="state.queryForm.validMonths" placeholder="请选择分成有效月数" filterable allow-create>
          <el-option v-for="month in 12" :key="month" :label="month + '个月'" :value="month"/>
          <el-option label="无限制" value="9999"/>
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
            v-auth="'rs_partners_add'">
            新 增
          </el-button>
          <el-button plain icon="upload-filled" type="primary" class="ml10" @click="excelUploadRef.show()" v-auth="'sys_user_add'">
						导 入
					</el-button>
          <el-button plain :disabled="multiple" icon="Delete" type="primary"
            v-auth="'rs_partners_del'" @click="handleDelete(selectObjs)">
            删 除
          </el-button>
          <right-toolbar v-model:showSearch="showSearch" :export="'rs_partners_export'"
                @exportExcel="exportExcel" class="ml10 mr20" style="float: right;"
            @queryTable="getDataList"></right-toolbar>
        </div>
      </el-row>
      <el-table 
        :data="state.dataList" 
        v-loading="state.loading" border 
        :cell-style="tableStyle.cellStyle" 
        :header-cell-style="tableStyle.headerCellStyle"
				@selection-change="selectionChangHandle"
        @sort-change="sortChangeHandle">
        <el-table-column type="selection" width="40" align="center" />
        <el-table-column type="index" label="#" width="40" />
          <el-table-column prop="partnerName" label="合作伙伴名称"  show-overflow-tooltip/>
          <el-table-column prop="partnerCode" label="合作伙伴标识"  show-overflow-tooltip/>
          <el-table-column prop="revenueShare" label="分成比例" sortable="custom" show-overflow-tooltip>
            <template #default="scope">
              <el-button 
                icon="view" 
                text 
                type="primary" 
                v-auth="'rs_partners_view'"
                @click="openViewDialog(scope.row.partnerId)"
              >
                查看
              </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="startDate" label="合作开始时间"  show-overflow-tooltip/>
          <el-table-column prop="endDate" label="合作结束时间"  show-overflow-tooltip/>
          <!-- <el-table-column prop="validDays" label="分成有效天数"  show-overflow-tooltip/> -->
          <el-table-column prop="validMonths" label="分成有效月数" show-overflow-tooltip>
            <template #default="scope">
              <span v-if="scope.row.validMonths === 9999">无限制</span>
              <span v-else>{{ scope.row.validMonths }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="记录创建时间" sortable="custom" show-overflow-tooltip/>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button icon="edit-pen" text type="primary" v-auth="'rs_partners_edit'"
              @click="formDialogRef.openDialog(scope.row.partnerId)">编辑</el-button>
            <el-button icon="delete" text type="primary" v-auth="'rs_partners_del'" @click="handleDelete([scope.row.partnerId])">删除</el-button>
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
			url="/rsmanage/partners/import"
      temp-url="/admin/sys-file/local/file/partners.xlsx"
			@refreshDataList="getDataList"
		/>
    <!-- 查看分成比例 -->
    <view-dialog ref="viewDialogRef" @refresh="getDataList(false)"/>
  </div>
</template>

<script setup lang="ts" name="systemPartners">
import { BasicTableProps, useTable } from "/@/hooks/table";
import { fetchList, delObjs } from "/@/api/rs/partners";
import { useMessage, useMessageBox } from "/@/hooks/message";
import { useDict } from '/@/hooks/dict';
// 引入组件
const FormDialog = defineAsyncComponent(() => import('./form.vue'));
const ViewDialog = defineAsyncComponent(() => import('./viewSharesForm.vue'));
// 定义查询字典

// 定义变量内容
const formDialogRef = ref();
const excelUploadRef = ref();
const viewDialogRef = ref();

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
  downBlobFile('/rsmanage/partners/export', Object.assign(state.queryForm, { ids: selectObjs }), 'partners.xlsx')
}

// 多选事件
const selectionChangHandle = (objs: { partnerId: string }[]) => {
  selectObjs.value = objs.map(({ partnerId }) => partnerId);
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

// 打开查看对话框的方法
const openViewDialog = (partnerId: string) => {
  viewDialogRef.value.openDialog(partnerId);
};

</script>

