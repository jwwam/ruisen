package com.ruisen.rsmanage.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 站点管理表
 *
 * @author rsmanage
 * @date 2024-12-12 00:49:22
 */
@Data
@TableName("rs_site")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "站点管理表")
public class SiteEntity extends Model<SiteEntity> {


	/**
	* 主键ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键ID")
    private Long id;

	/**
	* 域名
	*/
    @Schema(description="域名")
    private String domain;

	/**
	* 状态 1-正常 2-禁用
	*/
    @Schema(description="状态 1-正常 2-禁用")
    private Integer status;

	/**
	* 客户ID
	*/
    @Schema(description="客户ID")
    private Long customerId;

	/**
	* 上游合作伙伴ID列表，多个用逗号分隔
	*/
    @Schema(description="上游合作伙伴ID列表，多个用逗号分隔")
    private String[] partnerIds;

	/**
	* 是否借用账号 0-否 1-是
	*/
    @Schema(description="是否借用账号 0-否 1-是")
    private Integer isBorrowedAccount;

	/**
	* 借用账号客户ID
	*/
    @Schema(description="借用账号客户ID")
    private Long borrowedCustomerId;

	/**
	* 客户GAM邮箱
	*/
    @Schema(description="客户GAM邮箱")
    private Long gamEmailId;

	/**
	* 是否放链接 0-否 1-是
	*/
    @Schema(description="是否放链接 0-否 1-是")
    private Integer hasLink;

	/**
	* 站点提审账号
	*/
    @Schema(description="站点提审账号")
    private String reviewAccount;

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
}
