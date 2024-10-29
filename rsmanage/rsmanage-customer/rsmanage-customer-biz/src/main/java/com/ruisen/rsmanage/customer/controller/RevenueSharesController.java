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
import com.ruisen.rsmanage.customer.entity.RevenueSharesEntity;
import com.ruisen.rsmanage.customer.service.RevenueSharesService;

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
 * 分成比例表
 *
 * @author rsmanage
 * @date 2024-10-30 04:20:36
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/revenueShares" )
@Tag(description = "revenueShares" , name = "分成比例表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class RevenueSharesController {

    private final  RevenueSharesService revenueSharesService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param revenueShares 分成比例表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("rs_revenueShares_view")
    public R getRevenueSharesPage(@ParameterObject Page page, @ParameterObject RevenueSharesEntity revenueShares) {
        LambdaQueryWrapper<RevenueSharesEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(Objects.nonNull(revenueShares.getPartnerId()),RevenueSharesEntity::getPartnerId,revenueShares.getPartnerId());
		wrapper.eq(StrUtil.isNotBlank(revenueShares.getName()),RevenueSharesEntity::getName,revenueShares.getName());
		wrapper.eq(Objects.nonNull(revenueShares.getIsActive()),RevenueSharesEntity::getIsActive,revenueShares.getIsActive());
        return R.ok(revenueSharesService.page(page, wrapper));
    }


    /**
     * 通过条件查询分成比例表
     * @param revenueShares 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("rs_revenueShares_view")
    public R getDetails(@ParameterObject RevenueSharesEntity revenueShares) {
        return R.ok(revenueSharesService.list(Wrappers.query(revenueShares)));
    }

    /**
     * 新增分成比例表
     * @param revenueShares 分成比例表
     * @return R
     */
    @Operation(summary = "新增分成比例表" , description = "新增分成比例表" )
    @SysLog("新增分成比例表" )
    @PostMapping
    @HasPermission("rs_revenueShares_add")
    public R save(@RequestBody RevenueSharesEntity revenueShares) {
        return R.ok(revenueSharesService.save(revenueShares));
    }

    /**
     * 修改分成比例表
     * @param revenueShares 分成比例表
     * @return R
     */
    @Operation(summary = "修改分成比例表" , description = "修改分成比例表" )
    @SysLog("修改分成比例表" )
    @PutMapping
    @HasPermission("rs_revenueShares_edit")
    public R updateById(@RequestBody RevenueSharesEntity revenueShares) {
        return R.ok(revenueSharesService.updateById(revenueShares));
    }

    /**
     * 通过id删除分成比例表
     * @param ids shareId列表
     * @return R
     */
    @Operation(summary = "通过id删除分成比例表" , description = "通过id删除分成比例表" )
    @SysLog("通过id删除分成比例表" )
    @DeleteMapping
    @HasPermission("rs_revenueShares_del")
    public R removeById(@RequestBody Integer[] ids) {
        return R.ok(revenueSharesService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param revenueShares 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("rs_revenueShares_export")
    public List<RevenueSharesEntity> exportExcel(RevenueSharesEntity revenueShares,Integer[] ids) {
        return revenueSharesService.list(Wrappers.lambdaQuery(revenueShares).in(ArrayUtil.isNotEmpty(ids), RevenueSharesEntity::getShareId, ids));
    }

    /**
     * 导入excel 表
     * @param revenueSharesList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("rs_revenueShares_export")
    public R importExcel(@RequestExcel List<RevenueSharesEntity> revenueSharesList, BindingResult bindingResult) {
        return R.ok(revenueSharesService.saveBatch(revenueSharesList));
    }
}
