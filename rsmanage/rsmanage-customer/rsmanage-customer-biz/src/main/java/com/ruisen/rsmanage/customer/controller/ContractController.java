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
import com.ruisen.rsmanage.customer.entity.ContractEntity;
import com.ruisen.rsmanage.customer.service.ContractService;

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
 * 合同管理表
 *
 * @author rsmanage
 * @date 2024-11-13 13:31:03
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/contract" )
@Tag(description = "contract" , name = "合同管理表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class ContractController {

    private final  ContractService contractService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param contract 合同管理表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("rs_contract_view")
    public R getContractPage(@ParameterObject Page page, @ParameterObject ContractEntity contract) {
        LambdaQueryWrapper<ContractEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(StrUtil.isNotBlank(contract.getContractNumber()),ContractEntity::getContractNumber,contract.getContractNumber());
		wrapper.eq(Objects.nonNull(contract.getCustomerId()),ContractEntity::getCustomerId,contract.getCustomerId());
		wrapper.eq(Objects.nonNull(contract.getIsActive()),ContractEntity::getIsActive,contract.getIsActive());
		wrapper.eq(Objects.nonNull(contract.getSignedDate()),ContractEntity::getSignedDate,contract.getSignedDate());
        return R.ok(contractService.page(page, wrapper));
    }


    /**
     * 通过条件查询合同管理表
     * @param contract 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("rs_contract_view")
    public R getDetails(@ParameterObject ContractEntity contract) {
        return R.ok(contractService.list(Wrappers.query(contract)));
    }

    /**
     * 新增合同管理表
     * @param contract 合同管理表
     * @return R
     */
    @Operation(summary = "新增合同管理表" , description = "新增合同管理表" )
    @SysLog("新增合同管理表" )
    @PostMapping
    @HasPermission("rs_contract_add")
    public R save(@RequestBody ContractEntity contract) {
        return R.ok(contractService.save(contract));
    }

    /**
     * 修改合同管理表
     * @param contract 合同管理表
     * @return R
     */
    @Operation(summary = "修改合同管理表" , description = "修改合同管理表" )
    @SysLog("修改合同管理表" )
    @PutMapping
    @HasPermission("rs_contract_edit")
    public R updateById(@RequestBody ContractEntity contract) {
        return R.ok(contractService.updateById(contract));
    }

    /**
     * 通过id删除合同管理表
     * @param ids contractId列表
     * @return R
     */
    @Operation(summary = "通过id删除合同管理表" , description = "通过id删除合同管理表" )
    @SysLog("通过id删除合同管理表" )
    @DeleteMapping
    @HasPermission("rs_contract_del")
    public R removeById(@RequestBody Integer[] ids) {
        return R.ok(contractService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param contract 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("rs_contract_export")
    public List<ContractEntity> exportExcel(ContractEntity contract,Integer[] ids) {
        return contractService.list(Wrappers.lambdaQuery(contract).in(ArrayUtil.isNotEmpty(ids), ContractEntity::getContractId, ids));
    }

    /**
     * 导入excel 表
     * @param contractList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("rs_contract_export")
    public R importExcel(@RequestExcel List<ContractEntity> contractList, BindingResult bindingResult) {
        return R.ok(contractService.saveBatch(contractList));
    }
}
