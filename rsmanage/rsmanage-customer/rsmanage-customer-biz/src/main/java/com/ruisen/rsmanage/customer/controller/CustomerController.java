package com.ruisen.rsmanage.customer.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruisen.rsmanage.common.core.util.R;
import com.ruisen.rsmanage.common.log.annotation.SysLog;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;

import com.ruisen.rsmanage.customer.entity.CustomerEntity;
import com.ruisen.rsmanage.customer.service.CustomerService;
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

/**
 * 客户信息表
 *
 * @author rsmanage
 * @date 2024-10-12 19:36:35
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer" )
@Tag(description = "customer" , name = "客户信息表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param customer 客户信息表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("rs_customer_view")
    public R getCustomerPage(@ParameterObject Page page, @ParameterObject CustomerEntity customer) {
        LambdaQueryWrapper<CustomerEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(customerService.page(page, wrapper));
    }


    /**
     * 通过条件查询客户信息表
     * @param customer 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("rs_customer_view")
    public R getDetails(@ParameterObject CustomerEntity customer) {
        return R.ok(customerService.list(Wrappers.query(customer)));
    }

    /**
     * 新增客户信息表
     * @param customer 客户信息表
     * @return R
     */
    @Operation(summary = "新增客户信息表" , description = "新增客户信息表" )
    @SysLog("新增客户信息表" )
    @PostMapping
    @HasPermission("rs_customer_add")
    public R save(@RequestBody CustomerEntity customer) {
        return R.ok(customerService.save(customer));
    }

    /**
     * 修改客户信息表
     * @param customer 客户信息表
     * @return R
     */
    @Operation(summary = "修改客户信息表" , description = "修改客户信息表" )
    @SysLog("修改客户信息表" )
    @PutMapping
    @HasPermission("rs_customer_edit")
    public R updateById(@RequestBody CustomerEntity customer) {
        return R.ok(customerService.updateById(customer));
    }

    /**
     * 通过id删除客户信息表
     * @param ids customerId列表
     * @return R
     */
    @Operation(summary = "通过id删除客户信息表" , description = "通过id删除客户信息表" )
    @SysLog("通过id删除客户信息表" )
    @DeleteMapping
    @HasPermission("rs_customer_del")
    public R removeById(@RequestBody Integer[] ids) {
        return R.ok(customerService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param customer 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("rs_customer_export")
    public List<CustomerEntity> exportExcel(CustomerEntity customer,Integer[] ids) {
        return customerService.list(Wrappers.lambdaQuery(customer).in(ArrayUtil.isNotEmpty(ids), CustomerEntity::getCustomerId, ids));
    }

    /**
     * 导入excel 表
     * @param customerList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("rs_customer_export")
    public R importExcel(@RequestExcel List<CustomerEntity> customerList, BindingResult bindingResult) {
        return R.ok(customerService.saveBatch(customerList));
    }
}
