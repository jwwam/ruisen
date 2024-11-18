<template>
  <div class="layout-padding">
    <div class="layout-padding-auto layout-padding-view">
      <el-row v-show="showSearch">
        <el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
      <el-form-item label="数据日期" prop="date">
      <el-date-picker type="date" placeholder="请输入数据日期" v-model="state.queryForm.date"></el-date-picker>
      </el-form-item>
      <el-form-item label="站点域名" prop="site">
            <el-select v-model="state.queryForm.site" placeholder="请选择站点域名">
              <el-option
                v-for="item in state.siteList"
                :key="item"
                :label="item"
                :value="item"
              />
            </el-select>
      </el-form-item>
      <el-form-item label="国家/地区" prop="country">
            <el-select v-model="state.queryForm.country" placeholder="请选择国家/地区">
              <el-option
                v-for="item in state.countryList"
                :key="item"
                :label="item"
                :value="item"
              />
            </el-select>
      </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><search /></el-icon>查询
            </el-button>
            <el-button icon="Refresh" @click="resetQuery">重 置</el-button>
          </el-form-item>
        </el-form>
      </el-row>
      <el-row>
        <div class="mb8" style="width: 100%">
          <el-button icon="folder-add" type="primary" class="ml10" @click="formDialogRef.openDialog()"
            v-auth="'rs_incomeRecord_add'">
            新 增
          </el-button>
          <el-button plain icon="upload-filled" type="primary" class="ml10" @click="handleUploadClick" v-auth="'sys_user_add'">
						导 入
					</el-button>
          <el-button plain :disabled="multiple" icon="Delete" type="primary"
            v-auth="'rs_incomeRecord_del'" @click="handleDelete(selectObjs)">
            删 除
          </el-button>
          <right-toolbar v-model:showSearch="showSearch" :export="'rs_incomeRecord_export'"
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
          <el-table-column prop="date" label="数据日期" sortable="custom" show-overflow-tooltip/>
          <el-table-column prop="site" label="站点域名"  show-overflow-tooltip/>
          <el-table-column prop="country" label="国家/地区"  show-overflow-tooltip/>
          <el-table-column prop="impressions" label="广告展示次数" sortable="custom" show-overflow-tooltip/>
          <el-table-column prop="clicks" label="广告点击次数" sortable="custom" show-overflow-tooltip/>
          <el-table-column prop="ctr" label="点击率(Click Through Rate)" sortable="custom" show-overflow-tooltip>
            <template #default="scope">
              {{ formatPercent(scope.row.ctr) }}
            </template>
          </el-table-column>
          <el-table-column prop="revenue" label="广告收入(美元)" sortable="custom" show-overflow-tooltip/>
          <el-table-column prop="ecpm" label="千次展示收益(Effective Cost Per Mille)" sortable="custom" show-overflow-tooltip/>
          <el-table-column prop="adRequests" label="广告请求数" sortable="custom" show-overflow-tooltip/>
          <el-table-column prop="matchRate" label="广告匹配率(%)" sortable="custom" show-overflow-tooltip>
            <template #default="scope">
              {{ formatPercent(scope.row.matchRate) }}
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" sortable="custom" show-overflow-tooltip/>
          <el-table-column prop="updatedAt" label="更新时间"  show-overflow-tooltip/>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button icon="edit-pen" text type="primary" v-auth="'rs_incomeRecord_edit'"
              @click="formDialogRef.openDialog(scope.row.id)">编辑</el-button>
            <el-button icon="delete" text type="primary" v-auth="'rs_incomeRecord_del'" @click="handleDelete([scope.row.id])">删除</el-button>
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
			url="/rs/incomeRecord/import"
      temp-url="/admin/sys-file/local/file/incomeRecord.xlsx"
			@refreshDataList="handleImportSuccess"
      :tip="'支持 .xlsx, .xls, .csv 格式文件'"
      accept=".xlsx,.xls,.csv"
		/>
  </div>
</template>

<script setup lang="ts" name="systemIncomeRecord">
import { BasicTableProps, useTable } from "/@/hooks/table";
import { fetchList, delObjs, getObj } from "/@/api/rs/incomeRecord";
import { useMessage, useMessageBox } from "/@/hooks/message";      
import { useDict } from '/@/hooks/dict';

// 引入组件
const FormDialog = defineAsyncComponent(() => import('./form.vue'));
// 定义查询字典

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
  pageList: fetchList,
  countryList: [] as string[],
  siteList: [] as string[]  // 添加站点列表
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

// 获取国家和站点列表
const getCountryAndSiteList = async () => {
  try {
    const res = await getObj();
    
    if (res?.data && Array.isArray(res.data)) {
      // 处理国家列表
      const countries = new Set(res.data.map((item: any) => item.country))
      state.countryList = Array.from(countries).filter(Boolean).sort()
      
      // 处理站点列表
      const sites = new Set(res.data.map((item: any) => item.site))
      state.siteList = Array.from(sites).filter(Boolean).sort()
    } else {
      console.error('API返回数据格式不正确:', res)
    }
  } catch (err) {
    console.error('获取数据列表失败:', err)
  }
}

// 在组件挂载时获取列表
onMounted(() => {
  getCountryAndSiteList()
})

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
  downBlobFile('/rs/incomeRecord/export', Object.assign(state.queryForm, { ids: selectObjs }), 'incomeRecord.xlsx')
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

// 格式化百分比
const formatPercent = (value: number) => {
  if (value === null || value === undefined) return '-';
  return `${(value * 100).toFixed(2)}%`;
};

// 处理上传点击
const handleUploadClick = () => {
  excelUploadRef.value.show();
};

// 导入成功后的回调处理
const handleImportSuccess = async () => {
  await getDataList(false); // 刷新表格数据
  await getCountryAndSiteList(); // 重新获取国家和站点列表
};

// 在 script 部分添加
const formatDate = (date: string) => {
  if (!date) return null;
  // 将带时区的日期字符串转换为本地日期
  const localDate = new Date(date);
  return `${localDate.getFullYear()}-${String(localDate.getMonth() + 1).padStart(2, '0')}-${String(localDate.getDate()).padStart(2, '0')}`;
};

// 修改 getDataList 调用前的处理
const handleSearch = () => {
  if (state.queryForm.date) {
    state.queryForm.date = formatDate(state.queryForm.date);
  }
  getDataList();
};
</script>
