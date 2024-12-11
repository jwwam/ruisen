package com.ruisen.rsmanage.customer.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 客户GAM邮箱管理表
 *
 * @author rsmanage
 * @date 2024-10-29 16:33:25
 */
@Data
public class CustomerGamEmailsPo {
	/**
	 * 合作关系唯一标识
	 */
	@Schema(description="合作关系唯一标识")
	private Integer emailId;
	/**
	* 客户ID
	*/
    @Schema(description="客户ID")
    private Integer customerId;

	/**
	* GAM邮箱
	*/
    @Schema(description="GAM邮箱")
    private String email;

	/**
	* NetworkCode
	*/
    @Schema(description="NetworkCode")
    private String networkCode;

	/**
	* 记录创建时间
	*/
    @Schema(description="记录创建时间")
    private LocalDateTime createdAt;

	/**
	* 合作伙伴标识
	*/
    @Schema(description="合作伙伴标识")
    private String partnerCode;

	/**
	* ads文件内容
	*/
    @Schema(description="ads文件内容")
    private String adsTxtContent;

	/**
	* 用户名
	*/
    @Schema(description="用户名")
    private String name;

	/**
	* 密码
	*/
    @Schema(description="密码")
    private String password;

	/**
	 * 客户姓名
	 */
	@Schema(description="客户姓名")
	private String customerName;


}
