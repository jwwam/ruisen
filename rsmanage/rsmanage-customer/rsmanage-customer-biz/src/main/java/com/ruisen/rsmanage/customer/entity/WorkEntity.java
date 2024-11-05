package com.ruisen.rsmanage.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 工单表
 *
 * @author rsmanage
 * @date 2024-10-29 17:26:35
 */
@Data
@TableName("rs_work_form")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "工单表")
public class WorkEntity extends Model<WorkEntity> {
	/**
	* 工单唯一标识
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="工单唯一标识")
    private Integer workId;

	/**
	* 提交人ID
	*/
    @Schema(description="提交人ID")
    private Long submitterId;

	/**
	* 工单标题
	*/
    @Schema(description="工单标题")
    private String title;

	/**
	* 工单内容
	*/
    @Schema(description="工单内容")
    private String content;

	/**
	* 工单状态，0:待处理, 1:处理中, 2:已处理
	*/
    @Schema(description="工单状态，0:待处理, 1:处理中, 2:已处理")
    private Integer status;

	/**
	* 处理人ID
	*/
    @Schema(description="处理人ID")
    private String assignees;

	/**
	* 附件路径列表，JSON格式
	*/
    @Schema(description="附件路径列表，JSON格式")
    private String attachments;

	/**
	* 创建时间
	*/
    @Schema(description="创建时间")
    private LocalDateTime createdAt;

	/**
	* 更新时间
	*/
    @Schema(description="更新时间")
    private LocalDateTime updatedAt;

	@Schema(description = "客户ID")
	private Integer customerId;

	@Schema(description = "合作伙伴ID")
	private Integer partnerId;

	@Schema(description = "抄送人")
	private String copy;

	@Schema(description = "工单分类")
	private String category;

	@Schema(description = "优先级")
	private String priority;

	@Schema(description = "截止日期")
	private String deadline;
}
