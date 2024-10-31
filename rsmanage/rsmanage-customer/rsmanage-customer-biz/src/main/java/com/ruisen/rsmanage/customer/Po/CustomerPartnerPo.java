package com.ruisen.rsmanage.customer.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerPartnerPo {
	/**
	 * 合作关系唯一标识
	 */
	@Schema(description="合作关系唯一标识")
	private Integer relationId;
	/**
	 * 客户ID
	 */
	@Schema(description="客户ID")
	private Integer customerId;

	/**
	 * 合作伙伴标识
	 */
	@Schema(description="合作伙伴标识")
	private String partnerCode;

	/**
	 * 客户分成比例（百分比）
	 */
	@Schema(description="客户分成比例（百分比）")
	private BigDecimal customerRevenueShare;

	/**
	 * 客户在合作伙伴平台的账号
	 */
	@Schema(description="客户在合作伙伴平台的账号")
	private String partnerAccount;

	/**
	 * 客户在合作伙伴平台的密码
	 */
	@Schema(description="客户在合作伙伴平台的密码")
	private String partnerPassword;

	/**
	 * 对应的ads.txt内容
	 */
	@Schema(description="对应的ads.txt内容")
	private String adsTxtContent;

	/**
	 * 记录创建时间
	 */
	@Schema(description="记录创建时间")
	private LocalDateTime createdAt;

	/**
	 * 客户在合作伙伴平台的地址
	 */
	@Schema(description="客户在合作伙伴平台的地址")
	private String url;

	/**
	 * 客户姓名
	 */
	@Schema(description="客户姓名")
	private String customerName;

}
