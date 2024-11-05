package com.ruisen.rsmanage.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 合作伙伴信息表
 *
 * @author rsmanage
 * @date 2024-10-15 21:20:48
 */
@Data
@TableName("rs_partners")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "合作伙伴信息表")
public class PartnersEntity extends Model<PartnersEntity> {


	/**
	* 合作伙伴唯一标识
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="合作伙伴唯一标识")
    private Integer partnerId;

	/**
	* 合作伙伴名称
	*/
    @Schema(description="合作伙伴名称")
    private String partnerName;

	/**
	* 合作伙伴标识
	*/
    @Schema(description="合作伙伴标识")
    private String partnerCode;

	/**
	* 在达成某个条件时我方的分成比例(百分比)
	*/
    @Schema(description="在达成某个条件时我方的分成比例(百分比)")
    private BigDecimal revenueShare;

	/**
	* 合作开始时间
	*/
    @Schema(description="合作开始时间")
    private LocalDate startDate;

	/**
	* 合作结束时间
	*/
    @Schema(description="合作结束时间")
    private LocalDate endDate;

	/**
	* 分成有效天数
	*/
    @Schema(description="分成有效天数")
    private Integer validDays;

	/**
	* 分成有效月数
	*/
    @Schema(description="分成有效月数")
    private Integer validMonths;

	/**
	* 记录创建时间
	*/
    @Schema(description="记录创建时间")
    private LocalDateTime createdAt;

	@TableField(exist = false)
    private List<RevenueSharesEntity> revenueShares;
}
