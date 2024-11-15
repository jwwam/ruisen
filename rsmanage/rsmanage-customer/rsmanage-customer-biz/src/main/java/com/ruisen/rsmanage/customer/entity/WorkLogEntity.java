package com.ruisen.rsmanage.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 工单操作表
 *
 * @author rsmanage
 * @date 2024-11-15 02:28:35
 */
@Data
@TableName("rs_work_log")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "工单操作表")
public class WorkLogEntity extends Model<WorkLogEntity> {


	/**
	* 工单操作日志唯一标识
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="工单操作日志唯一标识")
    private Integer logId;

	/**
	* 工单唯一标识
	*/
    @Schema(description="工单唯一标识")
    private Integer workId;

	/**
	* 操作类型（如创建、更新、关闭等）
	*/
    @Schema(description="操作类型（如创建、更新、关闭等）")
    private String operation;

	/**
	* 操作执行者的用户 ID
	*/
    @Schema(description="操作执行者的用户 ID")
    private Long performedBy;

	/**
	* 操作时间
	*/
    @Schema(description="操作时间")
    private LocalDateTime createdAt;

	/**
	* 操作详情或备注
	*/
    @Schema(description="操作详情或备注")
    private String details;
}
