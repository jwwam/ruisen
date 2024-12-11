package com.ruisen.rsmanage.customer.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruisen.rsmanage.customer.entity.IncomeRecordEntity;
import com.ruisen.rsmanage.customer.mapper.IncomeRecordMapper;
import com.ruisen.rsmanage.customer.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl extends ServiceImpl<IncomeRecordMapper, IncomeRecordEntity> implements ReportService {

    private final IncomeRecordMapper incomeRecordMapper;

    @Override
    public Map<String, Object> getDailyIncome(IncomeRecordEntity query) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 构建查询条件
            LambdaQueryWrapper<IncomeRecordEntity> wrapper = Wrappers.lambdaQuery();
            if (query != null) {
                // 添加日期条件
                if (query.getDate() != null) {
                    wrapper.eq(IncomeRecordEntity::getDate, query.getDate());
                }
                // 添加站点条件
                if (StrUtil.isNotBlank(query.getSite())) {
                    wrapper.eq(IncomeRecordEntity::getSite, query.getSite());
                }
                // 添加国家条件
                if (StrUtil.isNotBlank(query.getCountry())) {
                    wrapper.eq(IncomeRecordEntity::getCountry, query.getCountry());
                }
            }
            
            // 获取收入数据
            List<IncomeRecordEntity> records = incomeRecordMapper.selectList(wrapper);
            log.info("getDailyIncome查询结果数量: {}", records.size());
            
            if (CollUtil.isEmpty(records)) {
                result.put("totalIncome", BigDecimal.ZERO);
                result.put("totalImpressions", 0L);
                result.put("records", Collections.emptyList());
                return result;
            }
            
            // 计算总收入
            BigDecimal totalIncome = records.stream()
                    .map(IncomeRecordEntity::getRevenue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
                    
            // 计算展示次数
            Long totalImpressions = records.stream()
                    .mapToLong(record -> Optional.ofNullable(record.getImpressions()).orElse(0))
                    .sum();
                    
            result.put("totalIncome", totalIncome);
            result.put("totalImpressions", totalImpressions);
            result.put("records", records);
            
            log.info("getDailyIncome统计结果 - 总收入: {}, 总展示次数: {}", totalIncome, totalImpressions);
            
        } catch (Exception e) {
            log.error("getDailyIncome执行异常", e);
            throw new RuntimeException("获取每日收入统计数据失败", e);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getIncomeTrend(IncomeRecordEntity query) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取最近30天的数据
        LocalDate endDate = query.getDate() != null ? query.getDate() : LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);
        
        LambdaQueryWrapper<IncomeRecordEntity> wrapper = buildQueryWrapper(query);
        wrapper.ge(IncomeRecordEntity::getDate, startDate)
               .le(IncomeRecordEntity::getDate, endDate)
               .orderByAsc(IncomeRecordEntity::getDate);
               
        List<IncomeRecordEntity> records = incomeRecordMapper.selectList(wrapper);
        
        // 按日期和站点分组统计收入
        Map<String, List<BigDecimal>> siteIncomes = new HashMap<>();
        List<LocalDate> dateList = new ArrayList<>();
        
        // 初始化日期列表
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dateList.add(date);
        }
        
        // 按站点分组
        Map<String, Map<LocalDate, BigDecimal>> siteDailyIncomes = records.stream()
                .collect(Collectors.groupingBy(
                        IncomeRecordEntity::getSite,
                        Collectors.groupingBy(
                                IncomeRecordEntity::getDate,
                                Collectors.mapping(
                                        IncomeRecordEntity::getRevenue,
                                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                                )
                        )
                ));
                
        // 处理每个站点的数据
        for (Map.Entry<String, Map<LocalDate, BigDecimal>> entry : siteDailyIncomes.entrySet()) {
            List<BigDecimal> incomes = new ArrayList<>();
            for (LocalDate date : dateList) {
                incomes.add(entry.getValue().getOrDefault(date, BigDecimal.ZERO));
            }
            siteIncomes.put(entry.getKey(), incomes);
        }
        
        result.put("dates", dateList);
        result.put("siteIncomes", siteIncomes);
        
        return result;
    }

    @Override
    public Map<String, Object> getSiteIncomeRatio(IncomeRecordEntity query) {
        Map<String, Object> result = new HashMap<>();
        
        LambdaQueryWrapper<IncomeRecordEntity> wrapper = buildQueryWrapper(query);
        List<IncomeRecordEntity> records = incomeRecordMapper.selectList(wrapper);
        
        // 按站点分组统计收入
        Map<String, BigDecimal> siteIncome = records.stream()
                .collect(Collectors.groupingBy(
                        IncomeRecordEntity::getSite,
                        Collectors.mapping(
                                IncomeRecordEntity::getRevenue,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
                
        result.put("sites", siteIncome.keySet());
        result.put("values", siteIncome.values());
        
        return result;
    }

    @Override
    public Map<String, Object> getIncomeOverview(IncomeRecordEntity query) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取今日数据
        LocalDate today = LocalDate.now();
        query.setDate(today);
        Map<String, Object> todayData = getDailyIncome(query);
        
        // 获取昨日数据
        query.setDate(today.minusDays(1));
        Map<String, Object> yesterdayData = getDailyIncome(query);
        
        // 获取本月数据
        query.setDate(null);
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LambdaQueryWrapper<IncomeRecordEntity> monthWrapper = buildQueryWrapper(query);
        monthWrapper.ge(IncomeRecordEntity::getDate, firstDayOfMonth)
                   .le(IncomeRecordEntity::getDate, today);
                   
        List<IncomeRecordEntity> monthRecords = incomeRecordMapper.selectList(monthWrapper);
        BigDecimal monthIncome = monthRecords.stream()
                .map(IncomeRecordEntity::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        result.put("todayIncome", todayData.get("totalIncome"));
        result.put("yesterdayIncome", yesterdayData.get("totalIncome"));
        result.put("monthIncome", monthIncome);
        
        return result;
    }

    @Override
    public void exportIncomeReport(IncomeRecordEntity query) {
        // 使用现有的导出功能
        LambdaQueryWrapper<IncomeRecordEntity> wrapper = buildQueryWrapper(query);
        List<IncomeRecordEntity> records = incomeRecordMapper.selectList(wrapper);
        // TODO: 实现导出逻辑
    }

    @Override
    public List<String> getSiteList() {
        List<IncomeRecordEntity> records = incomeRecordMapper.selectList(null);
        return records.stream()
                .map(IncomeRecordEntity::getSite)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getCountryList() {
        List<IncomeRecordEntity> records = incomeRecordMapper.selectList(null);
        return records.stream()
                .map(IncomeRecordEntity::getCountry)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<IncomeRecordEntity> buildQueryWrapper(IncomeRecordEntity query) {
        LambdaQueryWrapper<IncomeRecordEntity> wrapper = Wrappers.lambdaQuery();
        
        if (query != null) {
            wrapper.eq(query.getDate() != null, IncomeRecordEntity::getDate, query.getDate())
                   .eq(query.getSite() != null, IncomeRecordEntity::getSite, query.getSite())
                   .eq(query.getCountry() != null, IncomeRecordEntity::getCountry, query.getCountry());
        }
        
        return wrapper;
    }

    @Override
    public Map<String, Object> getClickAnalysis(IncomeRecordEntity query) {
        LambdaQueryWrapper<IncomeRecordEntity> wrapper = new LambdaQueryWrapper<>();
        // 添加查询条件
        if (query.getDateRange() != null && query.getDateRange().length == 2) {
            wrapper.between(IncomeRecordEntity::getDate, query.getDateRange()[0], query.getDateRange()[1]);
        }
        if (StringUtils.isNotBlank(query.getSite())) {
            wrapper.eq(IncomeRecordEntity::getSite, query.getSite());
        }
        
        List<IncomeRecordEntity> records = baseMapper.selectList(wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalClicks", records.stream().mapToLong(IncomeRecordEntity::getClicks).sum());
        result.put("averageCtr", records.stream().mapToDouble(record -> record.getCtr().doubleValue()).average().orElse(0.0));
        result.put("maxCtr", records.stream()
            .map(record -> record.getCtr().setScale(2, RoundingMode.HALF_UP))
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO)
            .doubleValue());
        result.put("records", records);
        
        return result;
    }

    @Override
    public Map<String, Object> getCtrTrend(IncomeRecordEntity query) {
        List<Map<String, Object>> trendData = baseMapper.selectCtrTrend(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("dates", trendData.stream().map(m -> m.get("date")).collect(Collectors.toList()));
        result.put("ctrs", trendData.stream().map(m -> m.get("ctr")).collect(Collectors.toList()));
        
        return result;
    }

    @Override
    public Map<String, Object> getSiteCtrRatio(IncomeRecordEntity query) {
        List<Map<String, Object>> siteData = baseMapper.selectSiteCtrRatio(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("sites", siteData.stream().map(m -> m.get("site")).collect(Collectors.toList()));
        result.put("ctrs", siteData.stream().map(m -> m.get("ctr")).collect(Collectors.toList()));
        
        return result;
    }

    @Override
    public Map<String, Object> getCountryClicksTop10(IncomeRecordEntity query) {
        List<Map<String, Object>> countryData = baseMapper.selectCountryClicksTop10(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("countries", countryData.stream().map(m -> m.get("country")).collect(Collectors.toList()));
        result.put("clicks", countryData.stream().map(m -> m.get("clicks")).collect(Collectors.toList()));
        
        return result;
    }
}
