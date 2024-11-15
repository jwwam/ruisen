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
import utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;

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
		wrapper.like(StrUtil.isNotBlank(work.getAssignees()),WorkEntity::getAssignees,work.getAssignees());
        return R.ok(workService.page(page, wrapper));
    }

	/**
	 * 分页查询
	 * @param page 分页对象
	 * @param work 工单表
	 * @return
	 */
	@Operation(summary = "分页查询" , description = "分页查询" )
	@GetMapping("/workPage" )
	@HasPermission("rs_work_view")
	public R getNewWorkPage(@ParameterObject Page page, @ParameterObject WorkEntity work) {
		int curPage = (int) page.getCurrent();
		int pageSize = (int) page.getSize();
		String submitterId = StringUtils.stringUtils(work.getSubmitterId());
		String category = StringUtils.stringUtils(work.getCategory());
		String status = StringUtils.stringUtils(work.getStatus());
		String assignees = StringUtils.stringUtils(work.getAssignees());
		String copy = StringUtils.stringUtils(work.getCopy());
		
		Map<String,Object> param = new HashMap<>();
		param.put("curPage",curPage);
		param.put("pageSize",pageSize);
		param.put("submitterId",submitterId);
		param.put("category",category);
		param.put("status",status);
		param.put("assignees",assignees);
		param.put("copy",copy);
		
		return R.ok(workService.qry(param));
	}

	/**
	 * 统计查询
	 * @param work 工单表
	 * @return
	 */
	@Operation(summary = "统计查询", description = "统计查询")
	@GetMapping("/getWorkDataCount")
	@HasPermission("rs_work_view")
	public R getWorkDataCount(@ParameterObject WorkEntity work) {
		Map<String, Integer> countMap = new HashMap<>();
		
		// 统计待办事项(状态为0和1)
		if (work.getAssignees() != null) {
			LambdaQueryWrapper<WorkEntity> todoQuery = Wrappers.<WorkEntity>lambdaQuery()
				.eq(WorkEntity::getAssignees, work.getAssignees())
				.in(WorkEntity::getStatus, Arrays.asList(0, 1));
			int todoCount = (int) workService.count(todoQuery);
			countMap.put("pendingNum", todoCount);
		}

		// 统计已办事项(状态为2)
		if (work.getSubmitterId() != null) {
			LambdaQueryWrapper<WorkEntity> doneQuery = Wrappers.<WorkEntity>lambdaQuery()
				.eq(WorkEntity::getSubmitterId, work.getSubmitterId())
				.eq(WorkEntity::getStatus, 2);
			int doneCount = (int) workService.count(doneQuery);
			countMap.put("completedNum", doneCount);
		}

		// 统计抄送事项(状态为0和1)
		if (work.getCopy() != null) {
			LambdaQueryWrapper<WorkEntity> copyQuery = Wrappers.<WorkEntity>lambdaQuery()
				.like(WorkEntity::getCopy, work.getCopy())
				.in(WorkEntity::getStatus, Arrays.asList(0, 1));
			int copyCount = (int) workService.count(copyQuery);
			countMap.put("copyNum", copyCount);
		}

		return R.ok(countMap);
	}



	/**
     * 通过条件查询工单表
     * @param work 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/qryDetails" )
    @HasPermission("rs_work_view")
    public R getWorkDetails(@ParameterObject WorkEntity work) {
        return R.ok(workService.qryDetails(work));
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
        boolean saved = workService.save(work);
        if(saved) {
            return R.ok(work);  // 返回保存后的实体(包含自增ID)
        }
        return R.failed("保存失败");
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
