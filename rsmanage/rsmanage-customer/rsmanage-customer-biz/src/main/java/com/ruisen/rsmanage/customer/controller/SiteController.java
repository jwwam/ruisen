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
import com.ruisen.rsmanage.customer.entity.SiteEntity;
import com.ruisen.rsmanage.customer.service.SiteService;

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
 * 站点管理表
 *
 * @author rsmanage
 * @date 2024-12-12 00:49:22
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/site" )
@Tag(description = "site" , name = "站点管理表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SiteController {

    private final  SiteService siteService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param site 站点管理表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("rs_site_view")
    public R getSitePage(@ParameterObject Page page, @ParameterObject SiteEntity site) {
        LambdaQueryWrapper<SiteEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(StrUtil.isNotBlank(site.getDomain()),SiteEntity::getDomain,site.getDomain());
		wrapper.eq(Objects.nonNull(site.getStatus()),SiteEntity::getStatus,site.getStatus());
		wrapper.eq(Objects.nonNull(site.getCustomerId()),SiteEntity::getCustomerId,site.getCustomerId());
		wrapper.eq(Objects.nonNull(site.getIsBorrowedAccount()),SiteEntity::getIsBorrowedAccount,site.getIsBorrowedAccount());
		wrapper.eq(Objects.nonNull(site.getHasLink()),SiteEntity::getHasLink,site.getHasLink());
		wrapper.eq(Objects.nonNull(site.getCreatedAt()),SiteEntity::getCreatedAt,site.getCreatedAt());
        return R.ok(siteService.page(page, wrapper));
    }


    /**
     * 通过条件查询站点管理表
     * @param site 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("rs_site_view")
    public R getDetails(@ParameterObject SiteEntity site) {
        return R.ok(siteService.list(Wrappers.query(site)));
    }

    /**
     * 新增站点管理表
     * @param site 站点管理表
     * @return R
     */
    @Operation(summary = "新增站点管理表" , description = "新增站点管理表" )
    @SysLog("新增站点管理表" )
    @PostMapping
    @HasPermission("rs_site_add")
    public R save(@RequestBody SiteEntity site) {
        return R.ok(siteService.save(site));
    }

    /**
     * 修改站点管理表
     * @param site 站点管理表
     * @return R
     */
    @Operation(summary = "修改站点管理表" , description = "修改站点管理表" )
    @SysLog("修改站点管理表" )
    @PutMapping
    @HasPermission("rs_site_edit")
    public R updateById(@RequestBody SiteEntity site) {
        return R.ok(siteService.updateById(site));
    }

    /**
     * 通过id删除站点管理表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除站点管理表" , description = "通过id删除站点管理表" )
    @SysLog("通过id删除站点管理表" )
    @DeleteMapping
    @HasPermission("rs_site_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(siteService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param site 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("rs_site_export")
    public List<SiteEntity> exportExcel(SiteEntity site,Long[] ids) {
        return siteService.list(Wrappers.lambdaQuery(site).in(ArrayUtil.isNotEmpty(ids), SiteEntity::getId, ids));
    }

    /**
     * 导入excel 表
     * @param siteList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("rs_site_export")
    public R importExcel(@RequestExcel List<SiteEntity> siteList, BindingResult bindingResult) {
        return R.ok(siteService.saveBatch(siteList));
    }
}
