package com.ruisen.rsmanage.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * 分成比例表
 *
 * @author rsmanage
 * @date 2024-10-30 04:20:36
 */
@Data
@TableName("rs_revenue_shares")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "分成比例表")
public class RevenueSharesEntity extends Model<RevenueSharesEntity> {


	/**
	* 分成比例唯一标识
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="分成比例唯一标识")
    private Integer shareId;

	/**
	* 合作伙伴ID
	*/
    @Schema(description="合作伙伴ID")
    private Integer partnerId;

	/**
	* 分成比例名称
	*/
    @Schema(description="分成比例名称")
    private String name;

	/**
	* 分成比例
	*/
    @Schema(description="分成比例")
    private BigDecimal share;

	/**
	* 分成比例描述
	*/
    @Schema(description="分成比例描述")
    private String description;

	/**
	* 有效天数
	*/
    @Schema(description="有效天数")
    private Integer validDays;

	/**
	* 是否启用
	*/
    @Schema(description="是否启用")
    private Boolean isActive;

	/**
	* 排序顺序
	*/
    @Schema(description="排序顺序")
    private Integer sortOrder;
}
