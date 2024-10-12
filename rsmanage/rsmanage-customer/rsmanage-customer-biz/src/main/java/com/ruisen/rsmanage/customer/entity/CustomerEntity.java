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
 * @date 2024-10-12 19:36:35
 */
@Data
@TableName("rs_customers")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户信息表")
public class CustomerEntity extends Model<CustomerEntity> {


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
	* 对应的我方商务人员ID
	*/
    @Schema(description="对应的我方商务人员ID")
    private Integer salesRepId;

	/**
	* 记录创建时间
	*/
    @Schema(description="记录创建时间")
    private LocalDateTime createdAt;
}
