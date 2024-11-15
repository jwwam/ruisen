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
import com.ruisen.rsmanage.customer.entity.WorkLogEntity;
import com.ruisen.rsmanage.customer.service.WorkLogService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.ruisen.rsmanage.common.security.annotation.HasPermission;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;

/**
 * 工单操作表
 *
 * @author rsmanage
 * @date 2024-11-15 02:28:35
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/workLog" )
@Tag(description = "workLog" , name = "工单操作表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class WorkLogController {

    private final  WorkLogService workLogService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param workLog 工单操作表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("rs_workLog_view")
    public R getWorkLogPage(@ParameterObject Page page, @ParameterObject WorkLogEntity workLog) {
        LambdaQueryWrapper<WorkLogEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(StrUtil.isNotBlank(workLog.getOperation()),WorkLogEntity::getOperation,workLog.getOperation());
		wrapper.eq(Objects.nonNull(workLog.getWorkId()),WorkLogEntity::getPerformedBy,workLog.getWorkId());
		wrapper.eq(Objects.nonNull(workLog.getPerformedBy()),WorkLogEntity::getPerformedBy,workLog.getPerformedBy());
		wrapper.eq(Objects.nonNull(workLog.getCreatedAt()),WorkLogEntity::getCreatedAt,workLog.getCreatedAt());
        return R.ok(workLogService.page(page, wrapper));
    }


    /**
     * 通过条件查询工单操作表
     * @param workLog 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("rs_workLog_view")
    public R getDetails(@ParameterObject WorkLogEntity workLog) {
        return R.ok(workLogService.list(Wrappers.query(workLog)));
    }

    /**
     * 新增工单操作表
     * @param workLog 工单操作表
     * @return R
     */
    @Operation(summary = "新增工单操作表" , description = "新增工单操作表" )
    @SysLog("新增工单操作表" )
    @PostMapping
//    @HasPermission("rs_workLog_add")
    public R save(@RequestBody WorkLogEntity workLog) {
        return R.ok(workLogService.save(workLog));
    }

    /**
     * 修改工单操作表
     * @param workLog 工单操作表
     * @return R
     */
    @Operation(summary = "修改工单操作表" , description = "修改工单操作表" )
    @SysLog("修改工单操作表" )
    @PutMapping
    @HasPermission("rs_workLog_edit")
    public R updateById(@RequestBody WorkLogEntity workLog) {
        return R.ok(workLogService.updateById(workLog));
    }

    /**
     * 通过id删除工单操作表
     * @param ids logId列表
     * @return R
     */
    @Operation(summary = "通过id删除工单操作表" , description = "通过id删除工单操作表" )
    @SysLog("通过id删除工单操作表" )
    @DeleteMapping
    @HasPermission("rs_workLog_del")
    public R removeById(@RequestBody Integer[] ids) {
        return R.ok(workLogService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param workLog 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("rs_workLog_export")
    public List<WorkLogEntity> exportExcel(WorkLogEntity workLog,Integer[] ids) {
        return workLogService.list(Wrappers.lambdaQuery(workLog).in(ArrayUtil.isNotEmpty(ids), WorkLogEntity::getLogId, ids));
    }

    /**
     * 导入excel 表
     * @param workLogList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("rs_workLog_export")
    public R importExcel(@RequestExcel List<WorkLogEntity> workLogList, BindingResult bindingResult) {
        return R.ok(workLogService.saveBatch(workLogList));
    }

    @Operation(summary = "工单日志查询", description = "工单日志查询")
    @GetMapping("/getWorkLogs")
    // @HasPermission("rs_workLog_view")
    public R getWorkLogs(@ParameterObject Page page, @ParameterObject WorkLogEntity workLog) {
        int curPage = (int) page.getCurrent();
        int pageSize = (int) page.getSize();
        
        Map<String, Object> param = new HashMap<>();
        param.put("curPage", curPage);
        param.put("pageSize", pageSize);
        param.put("operation", workLog.getOperation());
        param.put("performedBy", workLog.getPerformedBy());
        param.put("createdAt", workLog.getCreatedAt());
        param.put("workId", workLog.getWorkId());
        
        return R.ok(workLogService.qryWorkLogs(param));
    }
}
