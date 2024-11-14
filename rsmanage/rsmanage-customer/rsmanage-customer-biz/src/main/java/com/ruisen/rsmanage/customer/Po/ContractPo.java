package com.ruisen.rsmanage.customer.Po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.ruisen.rsmanage.admin.api.entity.SysFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 合同管理表
 *
 * @author rsmanage
 * @date 2024-11-13 13:31:03
 */
@Data
public class ContractPo extends Model<ContractPo> {

	/**
	* 合同记录唯一标识
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="合同记录唯一标识")
    private Integer contractId;

	/**
	* 合同编号
	*/
    @Schema(description="合同编号")
    private String contractNumber;

	/**
	 * 合同名称
	 */
	@Schema(description="合同编号")
	private String contractName;

	/**
	* 客户ID
	*/
    @Schema(description="客户ID")
    private Integer customerId;

	/**
	* 合同是否生效
	*/
    @Schema(description="合同是否生效")
    private Integer isActive;

	/**
	* 签署日期
	*/
    @Schema(description="签署日期")
    private LocalDate signedDate;

	/**
	* 收款人名称
	*/
    @Schema(description="收款人名称")
    private String payeeName;

	/**
	* 收款人账号
	*/
    @Schema(description="收款人账号")
    private String payeeAccount;

	/**
	* 银行名称
	*/
    @Schema(description="银行名称")
    private String bankName;

	/**
	* 银行地址
	*/
    @Schema(description="银行地址")
    private String bankAddress;

	/**
	* SwiftCode
	*/
    @Schema(description="SwiftCode")
    private String swiftCode;

	/**
	* 记录创建时间
	*/
    @Schema(description="记录创建时间")
    private LocalDateTime createdAt;

	/**
	* 合同文件
	*/
    @Schema(description="合同文件")
    private String fileUrl;
	/**
	 * 附件列表
	 */
	@Schema(description="附件路径列表")
	private List<SysFile> attachmentsList;
	/**
	 * 客户姓名
	 */
	@Schema(description="客户姓名")
	private String customerName;

	/**
	 * 合同创建人
	 */
	@Schema(description="创建人")
	private String createUserId;
}
