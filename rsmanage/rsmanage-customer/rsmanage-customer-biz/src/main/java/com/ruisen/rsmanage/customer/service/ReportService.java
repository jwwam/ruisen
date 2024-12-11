package com.ruisen.rsmanage.customer.service;

import com.ruisen.rsmanage.customer.entity.IncomeRecordEntity;
import java.util.List;
import java.util.Map;

public interface ReportService {
    
    /**
     * 获取每日收入统计数据
     */
    Map<String, Object> getDailyIncome(IncomeRecordEntity query);
    
    /**
     * 获取收入趋势数据
     */
    Map<String, Object> getIncomeTrend(IncomeRecordEntity query);
    
    /**
     * 获取站点收入占比数据
     */
    Map<String, Object> getSiteIncomeRatio(IncomeRecordEntity query);
    
    /**
     * 获取收入汇总数据
     */
    Map<String, Object> getIncomeOverview(IncomeRecordEntity query);
    
    /**
     * 导出收入报表
     */
    void exportIncomeReport(IncomeRecordEntity query);
    
    /**
     * 获取站点列表
     */
    List<String> getSiteList();
    
    /**
     * 获取国家/地区列表
     */
    List<String> getCountryList();
    
    /**
     * 获取广告点击分析数据
     */
    Map<String, Object> getClickAnalysis(IncomeRecordEntity query);
    
    /**
     * 获取点击率趋势数据
     */
    Map<String, Object> getCtrTrend(IncomeRecordEntity query);
    
    /**
     * 获取站点点击率对比数据
     */
    Map<String, Object> getSiteCtrRatio(IncomeRecordEntity query);
    
    /**
     * 获取国家点击量TOP10数据
     */
    Map<String, Object> getCountryClicksTop10(IncomeRecordEntity query);
}
