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
import com.ruisen.rsmanage.customer.entity.CustomersEntity;
import com.ruisen.rsmanage.customer.service.CustomersService;

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
 * 客户信息表
 *
 * @author rsmanage
 * @date 2024-10-16 01:26:37
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/customers" )
@Tag(description = "customers" , name = "客户信息表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class CustomersController {

    private final  CustomersService customersService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param customers 客户信息表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("rs_customers_view")
    public R getCustomersPage(@ParameterObject Page page, @ParameterObject CustomersEntity customers) {
        LambdaQueryWrapper<CustomersEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(StrUtil.isNotBlank(customers.getName()),CustomersEntity::getName,customers.getName());
		wrapper.like(StrUtil.isNotBlank(customers.getLinkman()),CustomersEntity::getLinkman,customers.getLinkman());
		wrapper.eq(StrUtil.isNotBlank(customers.getEmail()),CustomersEntity::getEmail,customers.getEmail());
		wrapper.eq(StrUtil.isNotBlank(customers.getPhoneNumber()),CustomersEntity::getPhoneNumber,customers.getPhoneNumber());
		wrapper.eq(StrUtil.isNotBlank(customers.getSalesRepId()),CustomersEntity::getSalesRepId,customers.getSalesRepId());
        return R.ok(customersService.page(page, wrapper));
    }


    /**
     * 通过条件查询客户信息表
     * @param customers 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("rs_customers_view")
    public R getDetails(@ParameterObject CustomersEntity customers) {
        return R.ok(customersService.list(Wrappers.query(customers)));
    }

    /**
     * 新增客户信息表
     * @param customers 客户信息表
     * @return R
     */
    @Operation(summary = "新增客户信息表" , description = "新增客户信息表" )
    @SysLog("新增客户信息表" )
    @PostMapping
    @HasPermission("rs_customers_add")
    public R save(@RequestBody CustomersEntity customers) {
        return R.ok(customersService.save(customers));
    }

    /**
     * 修改客户信息表
     * @param customers 客户信息表
     * @return R
     */
    @Operation(summary = "修改客户信息表" , description = "修改客户信息表" )
    @SysLog("修改客户信息表" )
    @PutMapping
    @HasPermission("rs_customers_edit")
    public R updateById(@RequestBody CustomersEntity customers) {
        return R.ok(customersService.updateById(customers));
    }

    /**
     * 通过id删除客户信息表
     * @param ids customerId列表
     * @return R
     */
    @Operation(summary = "通过id删除客户信息表" , description = "通过id删除客户信息表" )
    @SysLog("通过id删除客户信息表" )
    @DeleteMapping
    @HasPermission("rs_customers_del")
    public R removeById(@RequestBody Integer[] ids) {
        return R.ok(customersService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param customers 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("rs_customers_export")
    public List<CustomersEntity> exportExcel(CustomersEntity customers,Integer[] ids) {
        return customersService.list(Wrappers.lambdaQuery(customers).in(ArrayUtil.isNotEmpty(ids), CustomersEntity::getCustomerId, ids));
    }

    /**
     * 导入excel 表
     * @param customersList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("rs_customers_export")
    public R importExcel(@RequestExcel List<CustomersEntity> customersList, BindingResult bindingResult) {
        return R.ok(customersService.saveBatch(customersList));
    }
}
