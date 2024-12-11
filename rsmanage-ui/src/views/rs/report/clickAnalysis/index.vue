<template>
  <div v-loading="state.loading" class="layout-padding">
    <div class="layout-padding-auto layout-padding-view h-full overflow-auto">
      <!-- 搜索表单 -->
      <el-row v-show="state.showSearch">
        <el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
          <el-form-item label="数据日期" prop="date">
            <el-date-picker type="daterange" placeholder="请选择日期范围" v-model="state.queryForm.dateRange"></el-date-picker>
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
        <!-- 总点击量卡片 -->
        <div class="relative flex flex-grow !flex-row flex-col items-center rounded-[10px] border-[1px] border-gray-200 bg-blue-50 hover:scale-105 hover:shadow-lg">
          <div class="ml-[18px] flex h-[90px] w-auto flex-row items-center">
            <div class="h-50 ml-4 flex w-auto flex-col justify-center">
              <p class="font-dm text-sm font-medium text-gray-600">总点击量</p>
              <h4 class="text-xl font-bold text-navy-700">{{ state.totalClicks }}</h4>
            </div>
          </div>
        </div>

        <!-- 平均点击率卡片 -->
        <div class="relative flex flex-grow !flex-row flex-col items-center rounded-[10px] border-[1px] border-gray-200 bg-blue-50 hover:scale-105 hover:shadow-lg">
          <div class="ml-[18px] flex h-[90px] w-auto flex-row items-center">
            <div class="h-50 ml-4 flex w-auto flex-col justify-center">
              <p class="font-dm text-sm font-medium text-gray-600">平均点击率</p>
              <h4 class="text-xl font-bold text-navy-700">{{ (state.averageCtr * 100).toFixed(2) }}%</h4>
            </div>
          </div>
        </div>

        <!-- 最高点击率卡片 -->
        <div class="relative flex flex-grow !flex-row flex-col items-center rounded-[10px] border-[1px] border-gray-200 bg-blue-50 hover:scale-105 hover:shadow-lg">
          <div class="ml-[18px] flex h-[90px] w-auto flex-row items-center">
            <div class="h-50 ml-4 flex w-auto flex-col justify-center">
              <p class="font-dm text-sm font-medium text-gray-600">最高点击率</p>
              <h4 class="text-xl font-bold text-navy-700">{{ (state.maxCtr * 100).toFixed(2) }}%</h4>
            </div>
          </div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="sm:flex flex-col">
        <!-- 点击率趋势 -->
        <el-card class="w-full !border-none mt-4" shadow="never">
          <div>
            <div class="chart-title">点击率趋势</div>
            <div class="flex h-[30vh] items-center" ref="ctrTrendChartRef"></div>
          </div>
        </el-card>

        <!-- 其他图表 -->
        <div class="sm:flex">
          <!-- 站点点击率对比 -->
          <el-card class="sm:mr-4 flex-1 !border-none mt-4" shadow="never">
            <div>
              <div class="chart-title">站点点击率对比</div>
              <div class="flex h-[30vh] items-center" ref="siteCtrChartRef"></div>
            </div>
          </el-card>

          <!-- Top10国家点击量 -->
          <el-card class="flex-1 !border-none mt-4" shadow="never">
            <div>
              <div class="chart-title">Top10国家点击量</div>
              <div class="flex h-[30vh] items-center" ref="countryClicksChartRef"></div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="clickAnalysis">
import { onMounted, onUnmounted, reactive, ref } from 'vue';
import { markRaw } from 'vue';
import * as echarts from 'echarts';
import { fetchClickAnalysis, fetchCtrTrend, fetchSiteCtrRatio, fetchCountryClicksTop10, fetchSiteList } from '/@/api/rs/report';

// 定义状态
const state = reactive({
  loading: false,
  queryForm: {
    dateRange: [],
    site: ''
  },
  showSearch: true,
  siteList: [] as string[],
  records: [] as any[],
  totalClicks: 0,
  averageCtr: 0,
  maxCtr: 0
});

// 图表引用
const ctrTrendChartRef = ref();
const siteCtrChartRef = ref();
const countryClicksChartRef = ref();

// 图表配置
const chartOptions = reactive({
  ctrTrendOption: {
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const data = Array.isArray(params) ? params[0] : params;
        return `${data.name}<br/>${data.seriesName}: ${data.value.toFixed(2)}%`;
      }
    },
    xAxis: {
      type: 'category',
      data: []
    },
    yAxis: {
      type: 'value',
      name: '点击率(%)',
      axisLabel: {
        formatter: (value: number) => value.toFixed(2)
      }
    },
    series: [{
      name: '点击率',
      type: 'line',
      smooth: true,
      data: []
    }]
  },
  siteCtrOption: {
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const data = Array.isArray(params) ? params[0] : params;
        return `${data.name}<br/>点击率: ${data.value.toFixed(2)}%`;
      }
    },
    xAxis: {
      type: 'value',
      name: '点击率(%)',
      axisLabel: {
        formatter: (value: number) => value.toFixed(2)
      }
    },
    yAxis: {
      type: 'category',
      data: []
    },
    series: [{
      type: 'bar',
      data: []
    }]
  },
  countryClicksOption: {
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const data = Array.isArray(params) ? params[0] : params;
        return `${data.name}<br/>点击量: ${data.value.toFixed(2)}`;
      }
    },
    xAxis: {
      type: 'category',
      data: []
    },
    yAxis: {
      type: 'value',
      name: '点击量',
      axisLabel: {
        formatter: (value: number) => value.toFixed(2)
      }
    },
    series: [{
      type: 'bar',
      data: []
    }]
  }
});

// 获取数据方法
const getDataList = async () => {
  try {
    state.loading = true;
    const res = await fetchClickAnalysis(state.queryForm);
    if (res.data) {
      state.records = res.data.records;
      state.totalClicks = res.data.totalClicks;
      state.averageCtr = res.data.averageCtr;
      state.maxCtr = res.data.maxCtr;
      
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
const updateCharts = async (data: any[]) => {
  try {
    // 获取点击率趋势数据
    const trendRes = await fetchCtrTrend(state.queryForm);
    chartOptions.ctrTrendOption.xAxis.data = trendRes.data.dates;
    chartOptions.ctrTrendOption.series[0].data = trendRes.data.ctrs;
    
    // 获取站点点击率对比数据
    const siteRes = await fetchSiteCtrRatio(state.queryForm);
    chartOptions.siteCtrOption.yAxis.data = siteRes.data.sites;
    chartOptions.siteCtrOption.series[0].data = siteRes.data.ctrs;
    
    // 获取国家点击量TOP10数据
    const countryRes = await fetchCountryClicksTop10(state.queryForm);
    chartOptions.countryClicksOption.xAxis.data = countryRes.data.countries;
    chartOptions.countryClicksOption.series[0].data = countryRes.data.clicks;
    
    // 初始化图表实例并设置数据
    const ctrTrendChart = markRaw(echarts.init(ctrTrendChartRef.value));
    const siteCtrChart = markRaw(echarts.init(siteCtrChartRef.value));
    const countryClicksChart = markRaw(echarts.init(countryClicksChartRef.value));
    
    ctrTrendChart.setOption(chartOptions.ctrTrendOption);
    siteCtrChart.setOption(chartOptions.siteCtrOption);
    countryClicksChart.setOption(chartOptions.countryClicksOption);
  } catch (error) {
    console.error(error);
  }
};

// 搜索方法
const handleSearch = () => {
  getDataList();
};

// 重置方法
const resetQuery = () => {
  state.queryForm.dateRange = [];
  state.queryForm.site = '';
  getDataList();
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

onMounted(async () => {
  await getSiteList(); // 先获取站点列表
  getDataList(); // 获取统计数据
});

// ... 其他方法和生命周期钩子
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
  height: 400px;  /* 可以根据需要调整高度 */
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
