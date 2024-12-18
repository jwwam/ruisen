package com.ruisen.rsmanage.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 客户信息表
 *
 * @author rsmanage
 * @date 2024-10-16 01:26:37
 */
@Data
@TableName("rs_customers")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户信息表")
public class CustomersEntity extends Model<CustomersEntity> {


	/**
	* 客户唯一标识
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="客户唯一标识")
    private Integer customerId;

	/**
	* 客户名称
	*/
    @Schema(description="客户名称")
    private String name;

	/**
	* 联系人
	*/
    @Schema(description="联系人")
    private String linkman;

	/**
	* 客户电子邮件
	*/
    @Schema(description="客户电子邮件")
    private String email;

	/**
	* 客户电话号码
	*/
    @Schema(description="客户电话号码")
    private String phoneNumber;

	/**
	* 商务id
	*/
    @Schema(description="商务id")
    private String salesRepId;

	/**
	* 记录创建时间
	*/
    @Schema(description="记录创建时间")
    private LocalDateTime createdAt;

	@Schema(description="客户主题（公司名称）")
	private String companyName;

	@Schema(description="对方公司财务联系人")
	private String financeContact;

	@Schema(description="对方公司财务人员")
	private String financeContactUser;

	@Schema(description="对应的财务人员邮箱")
	private String financeEmail;

	@Schema(description="财务人员电话")
	private String financePhone;

	@Schema(description="我方客户标志")
	private Integer isOurCustomer;

}
