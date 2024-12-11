package com.ruisen.rsmanage.customer.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruisen.rsmanage.customer.entity.IncomeRecordEntity;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IncomeRecordMapper extends BaseMapper<IncomeRecordEntity> {
    List<Map<String, Object>> selectCtrTrend(IncomeRecordEntity query);
    List<Map<String, Object>> selectSiteCtrRatio(IncomeRecordEntity query);
    List<Map<String, Object>> selectCountryClicksTop10(IncomeRecordEntity query);
    List<Map<String, Object>> selectIncomeTrend(IncomeRecordEntity query);
    List<Map<String, Object>> selectSiteIncomeRatio(IncomeRecordEntity query);
    Map<String, Object> selectIncomeOverview(IncomeRecordEntity query);
}
