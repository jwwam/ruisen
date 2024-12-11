package com.ruisen.rsmanage.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruisen.rsmanage.common.core.util.R;
import com.ruisen.rsmanage.customer.entity.IncomeRecordEntity;
import com.ruisen.rsmanage.customer.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
@Tag(name = "报表管理模块")
public class ReportController {

    private final ReportService reportService;

    /**
     * 获取每日收入统计数据
     */
    @Operation(summary = "获取每日收入统计数据")
    @GetMapping("/dailyIncome")
    public R getDailyIncome(@ParameterObject IncomeRecordEntity query) {
        return R.ok(reportService.getDailyIncome(query));
    }

    /**
     * 获取收入趋势数据
     */
    @Operation(summary = "获取收入趋势数据")
    @GetMapping("/incomeTrend")
    public R getIncomeTrend(@ParameterObject IncomeRecordEntity query) {
        return R.ok(reportService.getIncomeTrend(query));
    }

    /**
     * 获取站点收入占比数据
     */
    @Operation(summary = "获取站点收入占比数据")
    @GetMapping("/siteIncomeRatio")
    public R getSiteIncomeRatio(@ParameterObject IncomeRecordEntity query) {
        return R.ok(reportService.getSiteIncomeRatio(query));
    }

    /**
     * 获取收入汇总数据
     */
    @Operation(summary = "获取收入汇总数据")
    @GetMapping("/incomeOverview")
    public R getIncomeOverview(@ParameterObject IncomeRecordEntity query) {
        return R.ok(reportService.getIncomeOverview(query));
    }

    /**
     * 导出收入报表
     */
    @Operation(summary = "导出收入报表")
    @GetMapping("/export")
    public void exportIncomeReport(@ParameterObject IncomeRecordEntity query) {
        reportService.exportIncomeReport(query);
    }

    /**
     * 获取站点列表
     */
    @Operation(summary = "获取站点列表")
    @GetMapping("/siteList")
    public R getSiteList() {
        return R.ok(reportService.getSiteList());
    }

    /**
     * 获取国家/地区列表
     */
    @Operation(summary = "获取国家/地区列表")
    @GetMapping("/countryList")
    public R getCountryList() {
        return R.ok(reportService.getCountryList());
    }

    /**
     * 获取广告点击分析数据
     */
    @Operation(summary = "获取广告点击分析数据")
    @GetMapping("/clickAnalysis")
    public R getClickAnalysis(@ParameterObject IncomeRecordEntity query) {
        return R.ok(reportService.getClickAnalysis(query));
    }

    /**
     * 获取点击率趋势数据
     */
    @Operation(summary = "获取点击率趋势数据")
    @GetMapping("/ctrTrend")
    public R getCtrTrend(@ParameterObject IncomeRecordEntity query) {
        return R.ok(reportService.getCtrTrend(query));
    }

    /**
     * 获取站点点击率对比数据
     */
    @Operation(summary = "获取站点点击率对比数据")
    @GetMapping("/siteCtrRatio")
    public R getSiteCtrRatio(@ParameterObject IncomeRecordEntity query) {
        return R.ok(reportService.getSiteCtrRatio(query));
    }

    /**
     * 获取国家点击量TOP10数据
     */
    @Operation(summary = "获取国家点击量TOP10数据")
    @GetMapping("/countryClicksTop10")
    public R getCountryClicksTop10(@ParameterObject IncomeRecordEntity query) {
        return R.ok(reportService.getCountryClicksTop10(query));
    }

}
