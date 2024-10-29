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
import com.ruisen.rsmanage.customer.entity.WorkEntity;
import com.ruisen.rsmanage.customer.service.WorkService;

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
import java.util.Objects;

/**
 * 工单表
 *
 * @author rsmanage
 * @date 2024-10-29 17:26:35
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/work" )
@Tag(description = "work" , name = "工单表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class WorkController {

    private final  WorkService workService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param work 工单表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("rs_work_view")
    public R getWorkPage(@ParameterObject Page page, @ParameterObject WorkEntity work) {
        LambdaQueryWrapper<WorkEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(Objects.nonNull(work.getSubmitterId()),WorkEntity::getSubmitterId,work.getSubmitterId());
		wrapper.eq(StrUtil.isNotBlank(work.getTitle()),WorkEntity::getTitle,work.getTitle());
		wrapper.eq(Objects.nonNull(work.getStatus()),WorkEntity::getStatus,work.getStatus());
//		wrapper.like(StrUtil.isNotBlank(work.getAssignees()),WorkEntity::getAssignees,work.getAssignees());
        return R.ok(workService.page(page, wrapper));
    }


    /**
     * 通过条件查询工单表
     * @param work 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("rs_work_view")
    public R getDetails(@ParameterObject WorkEntity work) {
        return R.ok(workService.list(Wrappers.query(work)));
    }

    /**
     * 新增工单表
     * @param work 工单表
     * @return R
     */
    @Operation(summary = "新增工单表" , description = "新增工单表" )
    @SysLog("新增工单表" )
    @PostMapping
    @HasPermission("rs_work_add")
    public R save(@RequestBody WorkEntity work) {
        return R.ok(workService.save(work));
    }

    /**
     * 修改工单表
     * @param work 工单表
     * @return R
     */
    @Operation(summary = "修改工单表" , description = "修改工单表" )
    @SysLog("修改工单表" )
    @PutMapping
    @HasPermission("rs_work_edit")
    public R updateById(@RequestBody WorkEntity work) {
        return R.ok(workService.updateById(work));
    }

    /**
     * 通过id删除工单表
     * @param ids workId列表
     * @return R
     */
    @Operation(summary = "通过id删除工单表" , description = "通过id删除工单表" )
    @SysLog("通过id删除工单表" )
    @DeleteMapping
    @HasPermission("rs_work_del")
    public R removeById(@RequestBody Integer[] ids) {
        return R.ok(workService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param work 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("rs_work_export")
    public List<WorkEntity> exportExcel(WorkEntity work,Integer[] ids) {
        return workService.list(Wrappers.lambdaQuery(work).in(ArrayUtil.isNotEmpty(ids), WorkEntity::getWorkId, ids));
    }

    /**
     * 导入excel 表
     * @param workList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("rs_work_export")
    public R importExcel(@RequestExcel List<WorkEntity> workList, BindingResult bindingResult) {
        return R.ok(workService.saveBatch(workList));
    }
}