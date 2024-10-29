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
import com.ruisen.rsmanage.customer.entity.CustomerPartnerEntity;
import com.ruisen.rsmanage.customer.service.CustomerPartnerService;

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
 * 客户与合作伙伴的合作关系表
 *
 * @author rsmanage
 * @date 2024-10-29 15:54:18
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/customerPartner" )
@Tag(description = "customerPartner" , name = "客户与合作伙伴的合作关系表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class CustomerPartnerController {

    private final  CustomerPartnerService customerPartnerService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param customerPartner 客户与合作伙伴的合作关系表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("rs_customerPartner_view")
    public R getCustomerPartnerPage(@ParameterObject Page page, @ParameterObject CustomerPartnerEntity customerPartner) {
        LambdaQueryWrapper<CustomerPartnerEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(Objects.nonNull(customerPartner.getCustomerId()),CustomerPartnerEntity::getCustomerId,customerPartner.getCustomerId());
		wrapper.eq(StrUtil.isNotBlank(customerPartner.getPartnerCode()),CustomerPartnerEntity::getPartnerCode,customerPartner.getPartnerCode());
		wrapper.like(StrUtil.isNotBlank(customerPartner.getPartnerAccount()),CustomerPartnerEntity::getPartnerAccount,customerPartner.getPartnerAccount());
        return R.ok(customerPartnerService.page(page, wrapper));
    }


    /**
     * 通过条件查询客户与合作伙伴的合作关系表
     * @param customerPartner 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("rs_customerPartner_view")
    public R getDetails(@ParameterObject CustomerPartnerEntity customerPartner) {
        return R.ok(customerPartnerService.list(Wrappers.query(customerPartner)));
    }

    /**
     * 新增客户与合作伙伴的合作关系表
     * @param customerPartner 客户与合作伙伴的合作关系表
     * @return R
     */
    @Operation(summary = "新增客户与合作伙伴的合作关系表" , description = "新增客户与合作伙伴的合作关系表" )
    @SysLog("新增客户与合作伙伴的合作关系表" )
    @PostMapping
    @HasPermission("rs_customerPartner_add")
    public R save(@RequestBody CustomerPartnerEntity customerPartner) {
        return R.ok(customerPartnerService.save(customerPartner));
    }

    /**
     * 修改客户与合作伙伴的合作关系表
     * @param customerPartner 客户与合作伙伴的合作关系表
     * @return R
     */
    @Operation(summary = "修改客户与合作伙伴的合作关系表" , description = "修改客户与合作伙伴的合作关系表" )
    @SysLog("修改客户与合作伙伴的合作关系表" )
    @PutMapping
    @HasPermission("rs_customerPartner_edit")
    public R updateById(@RequestBody CustomerPartnerEntity customerPartner) {
        return R.ok(customerPartnerService.updateById(customerPartner));
    }

    /**
     * 通过id删除客户与合作伙伴的合作关系表
     * @param ids relationId列表
     * @return R
     */
    @Operation(summary = "通过id删除客户与合作伙伴的合作关系表" , description = "通过id删除客户与合作伙伴的合作关系表" )
    @SysLog("通过id删除客户与合作伙伴的合作关系表" )
    @DeleteMapping
    @HasPermission("rs_customerPartner_del")
    public R removeById(@RequestBody Integer[] ids) {
        return R.ok(customerPartnerService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param customerPartner 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("rs_customerPartner_export")
    public List<CustomerPartnerEntity> exportExcel(CustomerPartnerEntity customerPartner,Integer[] ids) {
        return customerPartnerService.list(Wrappers.lambdaQuery(customerPartner).in(ArrayUtil.isNotEmpty(ids), CustomerPartnerEntity::getRelationId, ids));
    }

    /**
     * 导入excel 表
     * @param customerPartnerList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("rs_customerPartner_export")
    public R importExcel(@RequestExcel List<CustomerPartnerEntity> customerPartnerList, BindingResult bindingResult) {
        return R.ok(customerPartnerService.saveBatch(customerPartnerList));
    }
}
