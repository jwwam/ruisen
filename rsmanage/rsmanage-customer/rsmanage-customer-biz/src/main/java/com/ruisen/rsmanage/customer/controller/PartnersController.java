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
import com.ruisen.rsmanage.customer.entity.PartnersEntity;
import com.ruisen.rsmanage.customer.service.PartnersService;

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
 * 合作伙伴信息表
 *
 * @author rsmanage
 * @date 2024-10-15 21:20:48
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/partners" )
@Tag(description = "partners" , name = "合作伙伴信息表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class PartnersController {

    private final  PartnersService partnersService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param partners 合作伙伴信息表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("rs_partners_view")
    public R getPartnersPage(@ParameterObject Page page, @ParameterObject PartnersEntity partners) {
        LambdaQueryWrapper<PartnersEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(StrUtil.isNotBlank(partners.getPartnerName()),PartnersEntity::getPartnerName,partners.getPartnerName());
		wrapper.eq(StrUtil.isNotBlank(partners.getPartnerCode()),PartnersEntity::getPartnerCode,partners.getPartnerCode());
		wrapper.eq(Objects.nonNull(partners.getRevenueShare()),PartnersEntity::getRevenueShare,partners.getRevenueShare());
		wrapper.eq(Objects.nonNull(partners.getStartDate()),PartnersEntity::getStartDate,partners.getStartDate());
		wrapper.eq(Objects.nonNull(partners.getEndDate()),PartnersEntity::getEndDate,partners.getEndDate());
		wrapper.eq(Objects.nonNull(partners.getValidDays()),PartnersEntity::getValidDays,partners.getValidDays());
        return R.ok(partnersService.page(page, wrapper));
    }


    /**
     * 通过条件查询合作伙伴信息表
     * @param partners 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("rs_partners_view")
    public R getDetails(@ParameterObject PartnersEntity partners) {
        return R.ok(partnersService.list(Wrappers.query(partners)));
    }

    /**
     * 新增合作伙伴信息表
     * @param partners 合作伙伴信息表
     * @return R
     */
    @Operation(summary = "新增合作伙伴信息表" , description = "新增合作伙伴信息表" )
    @SysLog("新增合作伙伴信息表" )
    @PostMapping
    @HasPermission("rs_partners_add")
    public R save(@RequestBody PartnersEntity partners) {
        return R.ok(partnersService.save(partners));
    }

    /**
     * 修改合作伙伴信息表
     * @param partners 合作伙伴信息表
     * @return R
     */
    @Operation(summary = "修改合作伙伴信息表" , description = "修改合作伙伴信息表" )
    @SysLog("修改合作伙伴信息表" )
    @PutMapping
    @HasPermission("rs_partners_edit")
    public R updateById(@RequestBody PartnersEntity partners) {
        return R.ok(partnersService.updateById(partners));
    }

    /**
     * 通过id删除合作伙伴信息表
     * @param ids partnerId列表
     * @return R
     */
    @Operation(summary = "通过id删除合作伙伴信息表" , description = "通过id删除合作伙伴信息表" )
    @SysLog("通过id删除合作伙伴信息表" )
    @DeleteMapping
    @HasPermission("rs_partners_del")
    public R removeById(@RequestBody Integer[] ids) {
        return R.ok(partnersService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param partners 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("rs_partners_export")
    public List<PartnersEntity> exportExcel(PartnersEntity partners,Integer[] ids) {
        return partnersService.list(Wrappers.lambdaQuery(partners).in(ArrayUtil.isNotEmpty(ids), PartnersEntity::getPartnerId, ids));
    }

    /**
     * 导入excel 表
     * @param partnersList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("rs_partners_export")
    public R importExcel(@RequestExcel List<PartnersEntity> partnersList, BindingResult bindingResult) {
        return R.ok(partnersService.saveBatch(partnersList));
    }
}
