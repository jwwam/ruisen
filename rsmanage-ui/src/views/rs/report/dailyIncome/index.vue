<template>
  <div class="layout-padding">
    <div class="layout-padding-auto layout-padding-view h-full overflow-auto">
      <!-- 搜索表单 -->
      <el-row v-show="state.showSearch">
        <el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
          <el-form-item label="数据日期" prop="date">
            <el-date-picker type="date" placeholder="请选择数据日期" v-model="state.queryForm.date"></el-date-picker>
          </el-form-item>
          <el-form-item label="站点域名" prop="site">
            <el-select v-model="state.queryForm.site" placeholder="请选择站点域名" filterable>
              <el-option v-for="item in state.siteList" :key="item" :label="item" :value="item"/>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </el-row>

      <!-- 统计卡片 -->
      <div class="p-4 min-w-[375px] md:min-w-[700px] xl:min-w-[800px] mt-3 grid grid-cols-1 gap-5 md:grid-cols-2 lg:grid-cols-3 2xl:grid-cols-3 3xl:grid-cols-6">
        <!-- 总收入卡片 -->
        <div class="relative flex flex-grow !flex-row flex-col items-center rounded-[10px] border-[1px] border-gray-200 bg-blue-50 hover:scale-105 hover:shadow-lg">
          <div class="ml-[18px] flex h-[90px] w-auto flex-row items-center">
            <div class="h-50 ml-4 flex w-auto flex-col justify-center">
              <p class="font-dm text-sm font-medium text-gray-600">总收入(美元)</p>
              <h4 class="text-xl font-bold text-navy-700">{{ state.totalIncome.toFixed(2) }}</h4>
            </div>
          </div>
        </div>

        <!-- 今日收入卡片 -->
        <div class="relative flex flex-grow !flex-row flex-col items-center rounded-[10px] border-[1px] border-gray-200 bg-blue-50 hover:scale-105 hover:shadow-lg">
          <div class="ml-[18px] flex h-[90px] w-auto flex-row items-center">
            <div class="h-50 ml-4 flex w-auto flex-col justify-center">
              <p class="font-dm text-sm font-medium text-gray-600">今日收入(美元)</p>
              <h4 class="text-xl font-bold text-navy-700">{{ state.todayIncome.toFixed(2) }}</h4>
            </div>
          </div>
        </div>

        <!-- 昨日收入卡片 -->
        <div class="relative flex flex-grow !flex-row flex-col items-center rounded-[10px] border-[1px] border-gray-200 bg-blue-50 hover:scale-105 hover:shadow-lg">
          <div class="ml-[18px] flex h-[90px] w-auto flex-row items-center">
            <div class="h-50 ml-4 flex w-auto flex-col justify-center">
              <p class="font-dm text-sm font-medium text-gray-600">昨日收入(美元)</p>
              <h4 class="text-xl font-bold text-navy-700">{{ state.yesterdayIncome.toFixed(2) }}</h4>
            </div>
          </div>
        </div>

        <!-- 总展示次数卡片 -->
        <div class="relative flex flex-grow !flex-row flex-col items-center rounded-[10px] border-[1px] border-gray-200 bg-blue-50 hover:scale-105 hover:shadow-lg">
          <div class="ml-[18px] flex h-[90px] w-auto flex-row items-center">
            <div class="h-50 ml-4 flex w-auto flex-col justify-center">
              <p class="font-dm text-sm font-medium text-gray-600">总展示次数</p>
              <h4 class="text-xl font-bold text-navy-700">{{ state.totalImpressions }}</h4>
            </div>
          </div>
        </div>

        <!-- 平均点击率卡片 -->
        <div class="relative flex flex-grow !flex-row flex-col items-center rounded-[10px] border-[1px] border-gray-200 bg-blue-50 hover:scale-105 hover:shadow-lg">
          <div class="ml-[18px] flex h-[90px] w-auto flex-row items-center">
            <div class="h-50 ml-4 flex w-auto flex-col justify-center">
              <p class="font-dm text-sm font-medium text-gray-600">平均点击率</p>
              <h4 class="text-xl font-bold text-navy-700">{{ state.averageCtr.toFixed(2) }}%</h4>
            </div>
          </div>
        </div>

        <!-- 平均广告匹配率卡片 -->
        <div class="relative flex flex-grow !flex-row flex-col items-center rounded-[10px] border-[1px] border-gray-200 bg-blue-50 hover:scale-105 hover:shadow-lg">
          <div class="ml-[18px] flex h-[90px] w-auto flex-row items-center">
            <div class="h-50 ml-4 flex w-auto flex-col justify-center">
              <p class="font-dm text-sm font-medium text-gray-600">平均广告匹配率</p>
              <h4 class="text-xl font-bold text-navy-700">{{ state.averageMatchRate.toFixed(2) }}%</h4>
            </div>
          </div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="sm:flex flex-col">
        <!-- 收入变化趋势 -->
        <el-card class="w-full !border-none mt-4" shadow="never">
          <div>
            <div class="chart-title">收入变化趋势</div>
            <div class="flex h-[30vh] items-center" ref="incomeChartRef"></div>
          </div>
        </el-card>

        <!-- 其他图表 -->
        <div class="sm:flex">
          <!-- 收入趋势图 -->
          <el-card class="sm:mr-4 flex-1 !border-none mt-4" shadow="never">
            <div>
              <div class="chart-title">站点收入统计</div>
              <div class="flex h-[30vh] items-center" ref="revenueChartRef"></div>
            </div>
          </el-card>

          <!-- 站点收入占比 -->
          <el-card class="flex-1 !border-none mt-4" shadow="never">
            <div>
              <div class="chart-title">站点收入占比</div>
              <div class="flex h-[30vh] items-center" ref="siteChartRef"></div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 添加数据表格 -->
      <div class="table-container">
        <div class="table-scroll">
          <el-table :data="state.records" v-loading="state.loading" border style="width: 100%">
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="date" label="数据日期" sortable />
            <el-table-column prop="site" label="站点域名" />
            <el-table-column prop="country" label="国家/地区" />
            <el-table-column prop="impressions" label="广告展示次数" sortable />
            <el-table-column prop="clicks" label="广告点击次数" sortable />
            <el-table-column prop="ctr" label="点击率" sortable>
              <template #default="scope">
                {{ (scope.row.ctr * 100).toFixed(2) }}%
              </template>
            </el-table-column>
            <el-table-column prop="revenue" label="广告收入(美元)" sortable />
            <el-table-column prop="ecpm" label="千次展示收益" sortable />
            <el-table-column prop="adRequests" label="广告请求数" sortable />
            <el-table-column prop="matchRate" label="广告匹配率" sortable>
              <template #default="scope">
                {{ (scope.row.matchRate * 100).toFixed(2) }}%
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="dailyIncome">
import { BasicTableProps, useTable } from "/@/hooks/table";
import { markRaw } from 'vue';
import * as echarts from 'echarts';
import { fetchDailyIncome, fetchSiteList } from '/@/api/rs/report';
import { onMounted, onUnmounted, reactive, ref } from 'vue';

// 定义状态
const state = reactive({
  loading: false,
  queryForm: {
    date: '',
    site: ''
  },
  showSearch: true,
  siteList: [] as string[],
  records: [] as any[],
  totalIncome: 0,
  totalImpressions: 0,
  todayIncome: 0,
  yesterdayIncome: 0,
  averageCtr: 0,
  averageMatchRate: 0,
  totalAdRequests: 0,
  averageEcpm: 0
});

// 图表引用
const revenueChartRef = ref();
const siteChartRef = ref();
const incomeChartRef = ref();

// 图表配置
const chartOptions = reactive({
  revenueChartOption: {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: []
    },
    yAxis: {
      type: 'value',
      name: '收入(美元)'
    },
    series: [{
      data: [],
      type: 'line',
      smooth: true
    }]
  },
  siteChartOption: {
    tooltip: {
      trigger: 'item'
    },
    series: [{
      type: 'pie',
      radius: '50%',
      data: []
    }]
  },
  incomeChartOption: {
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const value = params[0].value;
        return `${params[0].axisValue}<br/>${params[0].seriesName}: $${Number(value).toFixed(2)}`;
      }
    },
    xAxis: {
      type: 'category',
      data: [],
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '收入(美元)',
      axisLabel: {
        formatter: '${value}'
      }
    },
    series: [{
      name: '日收入',
      data: [],
      type: 'line',
      smooth: true,
      areaStyle: {
        opacity: 0.3
      }
    }]
  }
});

// 获取数据方法
const getDataList = async () => {
  state.loading = true;
  try {
    const res = await fetchDailyIncome(state.queryForm);
    if (res.code === 0) {
      state.records = res.data.records;
      state.totalIncome = res.data.totalIncome;
      state.totalImpressions = res.data.totalImpressions;
      
      // 计算今日和昨日收入
      const today = new Date().toISOString().split('T')[0];
      const yesterday = new Date(Date.now() - 86400000).toISOString().split('T')[0];
      
      state.todayIncome = state.records
        .filter(record => record.date === today)
        .reduce((sum, record) => sum + Number(record.revenue), 0);
        
      state.yesterdayIncome = state.records
        .filter(record => record.date === yesterday)
        .reduce((sum, record) => sum + Number(record.revenue), 0);
        
      // 计算平均CTR
      state.averageCtr = state.records
        .reduce((sum, record) => sum + Number(record.ctr), 0) / state.records.length * 100;
        
      // 计算平均广告匹配率
      state.averageMatchRate = state.records
        .reduce((sum, record) => sum + Number(record.matchRate), 0) / state.records.length * 100;
        
      // 计算总广告请求数
      state.totalAdRequests = state.records
        .reduce((sum, record) => sum + Number(record.adRequests), 0);
        
      // 计算平均千次展示收益(eCPM)
      state.averageEcpm = state.records
        .reduce((sum, record) => sum + Number(record.ecpm), 0) / state.records.length;
        
      // 更新图表
      updateCharts(res.data.records);
    }
  } catch (error) {
    console.error(error);
  } finally {
    state.loading = false;
  }
};

// 更新图表数据的方法
const updateCharts = (data: any[]) => {
  // 处理收入趋势数据
  const revenueData = data.reduce((acc, cur) => {
    const site = cur.site;
    if (!acc[site]) {
      acc[site] = 0;
    }
    acc[site] = Number((acc[site] + Number(cur.revenue)).toFixed(2));
    return acc;
  }, {});

  // 更新收入趋势图
  chartOptions.revenueChartOption.xAxis.data = Object.keys(revenueData);
  chartOptions.revenueChartOption.series[0].data = Object.values(revenueData);

  // 更新站点收入占比图
  chartOptions.siteChartOption.series[0].data = Object.entries(revenueData).map(([name, value]) => ({
    name,
    value
  }));

  // 初始化图表实例并设置数据
  const revenueChart = markRaw(echarts.init(revenueChartRef.value));
  const siteChart = markRaw(echarts.init(siteChartRef.value));
  
  // 设置图表数据
  revenueChart.setOption(chartOptions.revenueChartOption);
  siteChart.setOption(chartOptions.siteChartOption);

  // 处理收入变化数据
  const dailyData = data.reduce((acc, cur) => {
    const date = cur.date;
    if (!acc[date]) {
      acc[date] = 0;
    }
    acc[date] = Number((acc[date] + Number(cur.revenue)).toFixed(2));
    return acc;
  }, {});

  // 按日期排序
  const sortedDates = Object.keys(dailyData).sort();
  
  // 更新收入变化趋势图
  chartOptions.incomeChartOption.xAxis.data = sortedDates;
  chartOptions.incomeChartOption.series[0].data = sortedDates.map(date => dailyData[date]);

  // 初始化图表实例并设置数据
  const incomeChart = markRaw(echarts.init(incomeChartRef.value));
  
  // 设置图表数据
  incomeChart.setOption(chartOptions.incomeChartOption);
};

// 搜索和重置方法
const handleSearch = () => {
  getDataList();
};

const resetQuery = () => {
  state.queryForm.date = '';
  state.queryForm.site = '';
  getDataList();
};

// 监听窗口大小变化
onMounted(async () => {
  await getSiteList();
  getDataList();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
});

const handleResize = () => {
  const revenueChart = echarts.getInstanceByDom(revenueChartRef.value);
  const siteChart = echarts.getInstanceByDom(siteChartRef.value);
  
  revenueChart?.resize();
  siteChart?.resize();

  const incomeChart = echarts.getInstanceByDom(incomeChartRef.value);
  incomeChart?.resize();
};

// 获取站点列表
const getSiteList = async () => {
  try {
    const res = await fetchSiteList();
    state.siteList = res.data || [];
  } catch (error) {
    console.error('获取站点列表失败:', error);
  }
};
</script>

<style scoped>
.layout-padding-auto {
  padding: 15px;
  height: calc(100vh - 85px);
  overflow-y: auto;
}

.el-card {
  border-radius: 8px;
  margin-bottom: 20px;
}

/* 修改图表容器高度为固定值 */
.h-[30vh] {
  height: 300px !important;
  min-height: 300px;
}

/* 修改表格容器样式 */
.table-container {
  margin-top: 20px;
  padding: 0 20px;
  background-color: #fff;
}

/* 添加表格滚动容器样式 */
.table-scroll {
  height: 200px;
  overflow-y: auto;
}

/* 确保表格宽度100% */
.el-table {
  width: 100% !important;
}

.chart-title {
  position: relative;
  padding-left: 15px;
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.chart-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: #409EFF;
  border-radius: 2px;
}
</style>
  