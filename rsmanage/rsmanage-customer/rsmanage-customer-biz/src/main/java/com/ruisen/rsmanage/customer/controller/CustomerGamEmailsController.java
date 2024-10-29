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
import com.ruisen.rsmanage.customer.entity.CustomerGamEmailsEntity;
import com.ruisen.rsmanage.customer.service.CustomerGamEmailsService;

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
 * 客户GAM邮箱管理表
 *
 * @author rsmanage
 * @date 2024-10-29 16:33:25
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/customerGamEmails" )
@Tag(description = "customerGamEmails" , name = "客户GAM邮箱管理表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class CustomerGamEmailsController {

    private final  CustomerGamEmailsService customerGamEmailsService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param customerGamEmails 客户GAM邮箱管理表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("rs_customerGamEmails_view")
    public R getCustomerGamEmailsPage(@ParameterObject Page page, @ParameterObject CustomerGamEmailsEntity customerGamEmails) {
        LambdaQueryWrapper<CustomerGamEmailsEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(Objects.nonNull(customerGamEmails.getCustomerId()),CustomerGamEmailsEntity::getCustomerId,customerGamEmails.getCustomerId());
		wrapper.eq(StrUtil.isNotBlank(customerGamEmails.getEmail()),CustomerGamEmailsEntity::getEmail,customerGamEmails.getEmail());
		wrapper.eq(StrUtil.isNotBlank(customerGamEmails.getNetworkCode()),CustomerGamEmailsEntity::getNetworkCode,customerGamEmails.getNetworkCode());
		wrapper.eq(StrUtil.isNotBlank(customerGamEmails.getPartnerCode()),CustomerGamEmailsEntity::getPartnerCode,customerGamEmails.getPartnerCode());
		wrapper.eq(StrUtil.isNotBlank(customerGamEmails.getName()),CustomerGamEmailsEntity::getName,customerGamEmails.getName());
        return R.ok(customerGamEmailsService.page(page, wrapper));
    }


    /**
     * 通过条件查询客户GAM邮箱管理表
     * @param customerGamEmails 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("rs_customerGamEmails_view")
    public R getDetails(@ParameterObject CustomerGamEmailsEntity customerGamEmails) {
        return R.ok(customerGamEmailsService.list(Wrappers.query(customerGamEmails)));
    }

    /**
     * 新增客户GAM邮箱管理表
     * @param customerGamEmails 客户GAM邮箱管理表
     * @return R
     */
    @Operation(summary = "新增客户GAM邮箱管理表" , description = "新增客户GAM邮箱管理表" )
    @SysLog("新增客户GAM邮箱管理表" )
    @PostMapping
    @HasPermission("rs_customerGamEmails_add")
    public R save(@RequestBody CustomerGamEmailsEntity customerGamEmails) {
        return R.ok(customerGamEmailsService.save(customerGamEmails));
    }

    /**
     * 修改客户GAM邮箱管理表
     * @param customerGamEmails 客户GAM邮箱管理表
     * @return R
     */
    @Operation(summary = "修改客户GAM邮箱管理表" , description = "修改客户GAM邮箱管理表" )
    @SysLog("修改客户GAM邮箱管理表" )
    @PutMapping
    @HasPermission("rs_customerGamEmails_edit")
    public R updateById(@RequestBody CustomerGamEmailsEntity customerGamEmails) {
        return R.ok(customerGamEmailsService.updateById(customerGamEmails));
    }

    /**
     * 通过id删除客户GAM邮箱管理表
     * @param ids emailId列表
     * @return R
     */
    @Operation(summary = "通过id删除客户GAM邮箱管理表" , description = "通过id删除客户GAM邮箱管理表" )
    @SysLog("通过id删除客户GAM邮箱管理表" )
    @DeleteMapping
    @HasPermission("rs_customerGamEmails_del")
    public R removeById(@RequestBody Integer[] ids) {
        return R.ok(customerGamEmailsService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param customerGamEmails 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("rs_customerGamEmails_export")
    public List<CustomerGamEmailsEntity> exportExcel(CustomerGamEmailsEntity customerGamEmails,Integer[] ids) {
        return customerGamEmailsService.list(Wrappers.lambdaQuery(customerGamEmails).in(ArrayUtil.isNotEmpty(ids), CustomerGamEmailsEntity::getEmailId, ids));
    }

    /**
     * 导入excel 表
     * @param customerGamEmailsList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("rs_customerGamEmails_export")
    public R importExcel(@RequestExcel List<CustomerGamEmailsEntity> customerGamEmailsList, BindingResult bindingResult) {
        return R.ok(customerGamEmailsService.saveBatch(customerGamEmailsList));
    }
}
