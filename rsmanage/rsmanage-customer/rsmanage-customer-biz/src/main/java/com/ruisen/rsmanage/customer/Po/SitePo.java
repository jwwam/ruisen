package com.ruisen.rsmanage.customer.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站点管理表
 *
 * @author rsmanage
 * @date 2024-12-12 00:49:22
 */
@Data
public class SitePo extends Model<SitePo> {


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
    private String partnerIds;

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

	/**
	 * 客户姓名
	 */
	@Schema(description="客户姓名")
	private String customerName;

	/**
	 * 合作伙伴标识
	 */
	@Schema(description="合作伙伴姓名")
	private String partnerCode;

	/**
	 * 客户GAM邮箱
	 */
	@Schema(description="客户GAM邮箱")
	private String email;

	/**
	 * 借用账号客户姓名
	 */
	@Schema(description="借用账号客户姓名")
	private String borrowedCustomerName;
}
