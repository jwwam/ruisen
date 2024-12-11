import request from '/@/utils/request';

/**
 * 获取每日收入统计数据
 */
export function fetchDailyIncome(query?: Object) {
  return request({
    url: '/rs/report/dailyIncome',
    method: 'get',
    params: query
  });
}

/**
 * 获取收入趋势数据
 */
export function fetchIncomeTrend(query?: Object) {
  return request({
    url: '/rs/report/incomeTrend',
    method: 'get',
    params: query
  });
}

/**
 * 获取站点收入占比数据
 */
export function fetchSiteIncomeRatio(query?: Object) {
  return request({
    url: '/rs/report/siteIncomeRatio',
    method: 'get',
    params: query
  });
}

/**
 * 获取收入汇总数据
 */
export function fetchIncomeOverview(query?: Object) {
  return request({
    url: '/rs/report/incomeOverview',
    method: 'get',
    params: query
  });
}

/**
 * 导出收入报表
 */
export function exportIncomeReport(query?: Object) {
  return request({
    url: '/rs/report/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  });
}

/**
 * 获取站点列表
 */
export function fetchSiteList() {
  return request({
    url: '/rs/report/siteList',
    method: 'get'
  });
}

/**
 * 获取国家/地区列表
 */
export function fetchCountryList() {
  return request({
    url: '/rs/report/countryList',
    method: 'get'
  });
}

/**
 * 获取广告点击分析数据
 */
export function fetchClickAnalysis(query?: Object) {
  return request({
    url: '/rs/report/clickAnalysis',
    method: 'get',
    params: query
  });
}

/**
 * 获取点击率趋势数据
 */
export function fetchCtrTrend(query?: Object) {
  return request({
    url: '/rs/report/ctrTrend',
    method: 'get',
    params: query
  });
}

/**
 * 获取站点点击率对比数据
 */
export function fetchSiteCtrRatio(query?: Object) {
  return request({
    url: '/rs/report/siteCtrRatio',
    method: 'get',
    params: query
  });
}

/**
 * 获取国家点击量TOP10数据
 */
export function fetchCountryClicksTop10(query?: Object) {
  return request({
    url: '/rs/report/countryClicksTop10',
    method: 'get',
    params: query
  });
}
