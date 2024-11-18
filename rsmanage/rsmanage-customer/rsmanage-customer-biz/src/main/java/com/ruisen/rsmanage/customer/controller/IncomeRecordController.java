package com.ruisen.rsmanage.customer.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruisen.rsmanage.common.core.util.R;
import com.ruisen.rsmanage.common.log.annotation.SysLog;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.ruisen.rsmanage.customer.entity.IncomeRecordEntity;
import com.ruisen.rsmanage.customer.service.IncomeRecordService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.ruisen.rsmanage.common.security.annotation.HasPermission;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import utils.ConversionResult;

import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 广告交易统计数据表
 *
 * @author rsmanage
 * @date 2024-11-17 04:25:15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/incomeRecord" )
@Tag(description = "incomeRecord" , name = "广告交易统计数据表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class IncomeRecordController {

    private final  IncomeRecordService incomeRecordService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param incomeRecord 广告交易统计数据表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("rs_incomeRecord_view")
    public R getIncomeRecordPage(@ParameterObject Page page, @ParameterObject IncomeRecordEntity incomeRecord) {
        LambdaQueryWrapper<IncomeRecordEntity> wrapper = Wrappers.lambdaQuery();
        
        // 修改日期查询条件
        if (incomeRecord != null && incomeRecord.getDate() != null) {
            wrapper.eq(IncomeRecordEntity::getDate, incomeRecord.getDate());
        }
        
        // 其他查询条件
        wrapper.eq(incomeRecord != null && StrUtil.isNotBlank(incomeRecord.getSite()),
                  IncomeRecordEntity::getSite,
                  incomeRecord.getSite());
                  
        wrapper.eq(incomeRecord != null && StrUtil.isNotBlank(incomeRecord.getCountry()),
                  IncomeRecordEntity::getCountry,
                  incomeRecord.getCountry());
                  
        return R.ok(incomeRecordService.page(page, wrapper));
    }


    /**
     * 通过条件查询广告交易统计数据表
     * @param incomeRecord 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("rs_incomeRecord_view")
    public R getDetails(@ParameterObject IncomeRecordEntity incomeRecord) {
        LambdaQueryWrapper<IncomeRecordEntity> wrapper = Wrappers.lambdaQuery();
        
        // 添加日期查询条件
        if (incomeRecord != null && incomeRecord.getDate() != null) {
            wrapper.eq(IncomeRecordEntity::getDate, incomeRecord.getDate());
        }
        
        return R.ok(incomeRecordService.list(wrapper));
    }

    /**
     * 新增广告交易统计数据表
     * @param incomeRecord 广告交易统计数据表
     * @return R
     */
    @Operation(summary = "新增广告交易统计数据表" , description = "新增广告交易统计数据表" )
    @SysLog("新增广告交易统计数据表" )
    @PostMapping
    @HasPermission("rs_incomeRecord_add")
    public R save(@RequestBody IncomeRecordEntity incomeRecord) {
        return R.ok(incomeRecordService.save(incomeRecord));
    }

    /**
     * 修改广告交易统计数据表
     * @param incomeRecord 广告交易统计数据表
     * @return R
     */
    @Operation(summary = "修改广告交易统计数据表" , description = "修改广告交易统计数据表" )
    @SysLog("修改广告交易统计数据表" )
    @PutMapping
    @HasPermission("rs_incomeRecord_edit")
    public R updateById(@RequestBody IncomeRecordEntity incomeRecord) {
        return R.ok(incomeRecordService.updateById(incomeRecord));
    }

    /**
     * 通过id删除广告交易统计数据表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除广告交易统计数据表" , description = "通过id删除广告交易统计数据表" )
    @SysLog("通过id删除广告交易统计数据表" )
    @DeleteMapping
    @HasPermission("rs_incomeRecord_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(incomeRecordService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param incomeRecord 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("rs_incomeRecord_export")
    public List<IncomeRecordEntity> exportExcel(IncomeRecordEntity incomeRecord,Long[] ids) {
        return incomeRecordService.list(Wrappers.lambdaQuery(incomeRecord).in(ArrayUtil.isNotEmpty(ids), IncomeRecordEntity::getId, ids));
    }

    /**
     * 导入excel 表
     * @param incomeRecordList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("rs_incomeRecord_export")
    public R importExcel(@RequestExcel List<IncomeRecordEntity> incomeRecordList, BindingResult bindingResult) {
        List<Map<String, Object>> errorRecords = new ArrayList<>();
        
        if (bindingResult.hasErrors()) {
            // 使用Map记录每个字段对应的错误
            Map<String, List<String>> fieldErrors = new HashMap<>();
            bindingResult.getAllErrors().forEach(error -> {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    String field = fieldError.getField();
                    // 从字段名中提取索引
                    String indexStr = field.replaceAll("\\D+", "");
                    if (!indexStr.isEmpty()) {
                        int index = Integer.parseInt(indexStr);
                        Map<String, Object> errorMap = new HashMap<>();
                        errorMap.put("lineNum", index + 2); // 使用列表索引+3作为行号
                        errorMap.put("errors", Collections.singletonList(fieldError.getDefaultMessage()));
                        errorRecords.add(errorMap);
                    }
                }
            });
        }
        
        // 修改过滤逻辑
        List<IncomeRecordEntity> filteredList = incomeRecordList.stream()
            .filter(Objects::nonNull)
            .filter(record -> record.getDateResult() != null)
            .filter(record -> !isInvalidRow(record))
            .collect(Collectors.toList());
        
        try {
            List<IncomeRecordEntity> newRecords = new ArrayList<>();
            
            // 检查并筛选出新数据
            for (int i = 0; i < filteredList.size(); i++) {
                IncomeRecordEntity record = filteredList.get(i);
                List<String> errors = new ArrayList<>();
                
                // 日期格式验证
                ConversionResult<LocalDate> dateResult = record.getDateResult();
                if (!dateResult.isSuccess()) {
                    errors.add(dateResult.getError());
                }
                
                // 校验必填字段
                if (StrUtil.isBlank(record.getSite())) {
                    errors.add("站点不能为空");
                }
                if (StrUtil.isBlank(record.getCountry())) {
                    errors.add("国家不能为空");
                }
                
                // 如果有字段为空,添加到错误记录
                if (!errors.isEmpty()) {
                    errorRecords.add(createErrorRecord(i + 2, errors, record));
                    continue;
                }
                
                // 检查数据是否已存在
                LambdaQueryWrapper<IncomeRecordEntity> wrapper = Wrappers.lambdaQuery();
                wrapper.eq(record.getDateResult() != null && record.getDateResult().isSuccess(),
                          IncomeRecordEntity::getDate, 
                          record.getDateResult().getValue())
                      .eq(IncomeRecordEntity::getSite, record.getSite())
                      .eq(IncomeRecordEntity::getCountry, record.getCountry());
                
                if (incomeRecordService.count(wrapper) > 0) {
                    errorRecords.add(createErrorRecord(i + 2, Collections.singletonList("数据已存在"), record));
                    continue;
                }

                // 处理百分比格式
                if (record.getCtr() != null) {
                    if (record.getCtr().compareTo(BigDecimal.ONE) > 0) {
                        record.setCtr(record.getCtr().divide(BigDecimal.valueOf(100), 4, RoundingMode.DOWN));
                    }
                }
                
                if (record.getMatchRate() != null) {
                    if (record.getMatchRate().compareTo(BigDecimal.ONE) > 0) {
                        record.setMatchRate(record.getMatchRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.DOWN));
                    }
                }
                
                record.setCreatedAt(LocalDateTime.now());
                record.setUpdatedAt(LocalDateTime.now());

                // 在保存记录之前设置 date 字段
                if (record.getDateResult() != null && record.getDateResult().isSuccess()) {
                    record.setDate(record.getDateResult().getValue());
                }   
                newRecords.add(record);
            }
            
            // 保存新数据
            if (!newRecords.isEmpty()) {
                incomeRecordService.saveBatch(newRecords);
            }

            // 如果有任何错误记录,回错误信息
            if (!errorRecords.isEmpty()) {
                return R.failed(errorRecords, String.format("总数据:%d,成功导入:%d,异常数据:%d", 
                    filteredList.size(), 
                    newRecords.size(),
                    errorRecords.size()));
            }
            
            return R.ok(String.format("总数据:%d,成功导入:%d", 
                filteredList.size(), 
                newRecords.size()));
                
        } catch (Exception e) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("lineNum", "0");
            errorMap.put("errors", Collections.singletonList(e.getMessage()));
            return R.failed(Collections.singletonList(errorMap), "导入失败");
        }
    }

    // 添加新的辅助方法
    private boolean isInvalidRow(IncomeRecordEntity record) {
        if (record == null || record.getDateResult() == null) {
            System.out.println("记录为null或日期为null");
            return true;
        }
        
        boolean isInvalid = "Total".equalsIgnoreCase(record.getSite()) || 
                           "Site".equalsIgnoreCase(record.getSite());
        
        if (record.getDateResult().isSuccess() && record.getDateResult().getValue() != null) {
            isInvalid = isInvalid || "Date".equalsIgnoreCase(record.getDateResult().getValue().toString());
        }
        
        if (isInvalid) {
            System.out.println("被过滤的数据: date=" + 
                (record.getDateResult().isSuccess() ? record.getDateResult().getValue() : "无效日期") + 
                ", site=" + record.getSite());
        }
        
        return isInvalid;
    }

    private Map<String, Object> createErrorRecord(int lineNum, List<String> errors, IncomeRecordEntity record) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("lineNum", lineNum);
        errorMap.put("errors", errors);
        
        // 保存原始数据
        Map<String, Object> rowData = new HashMap<>();
        rowData.put("date", record.getDateResult().getOriginalData());
        rowData.put("site", record.getSite());
        rowData.put("country", record.getCountry());
        rowData.put("impressions", record.getOriginalImpressions());
        rowData.put("clicks", record.getOriginalClicks());
        rowData.put("ctr", record.getOriginalCtr());
        rowData.put("revenue", record.getOriginalRevenue());
        rowData.put("ecpm", record.getOriginalEcpm());
        rowData.put("adRequests", record.getOriginalAdRequests());
        rowData.put("matchRate", record.getOriginalMatchRate());
        errorMap.put("rowData", rowData);
        
        return errorMap;
    }
}
