package com.ruisen.rsmanage.customer.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruisen.rsmanage.admin.api.entity.SysFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class WorkPo {
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
	 * 工单状态，0:待处理, 1:处理中, 2:已处理, 3:已终止
	 */
	@Schema(description="工单状态，0:待处理, 1:处理中, 2:已处理,3:已终止")
	private Integer status;

	/**
	 * 处理人ID
	 */
	@Schema(description="处理人ID")
	private String assignees;

	/**
	 * 附件路径列表，Sring类型
	 */
	@Schema(description="附件路径列表")
	private String attachments;
	/**
	 * 附件列表
	 */
	@Schema(description="附件路径列表")
	private List<SysFile> attachmentsList;
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
	private String partnerId;

	@Schema(description = "抄送人")
	private String copy;

	@Schema(description = "工单分类")
	private String category;

	@Schema(description = "优先级")
	private String priority;

	@Schema(description = "截止日期")
	private LocalDateTime deadline;

	@Schema(description="客户姓名")
	private String customerName;

	@Schema(description="合作伙伴标识")
	private String partnerCode;

	@Schema(description="提交人")
	private String submitterName;

	@Schema(description = "处理时间")
	private LocalDateTime handleTime;

	@Schema(description = "处理意见")
	private String handleOpinion;

}
