package com.ruisen.rsmanage.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import java.time.LocalDate;  // 改用LocalDate
import com.alibaba.excel.annotation.ExcelIgnore;
import lombok.Getter;
import lombok.Setter;
import utils.CustomLocalDateConverter;
import utils.PercentageConverter;
import utils.ConversionResult;
import utils.CustomBigDecimalConverter;
import utils.CustomIntegerConverter;

/**
 * 广告交易统计数据表
 *
 * @author rsmanage
 * @date 2024-11-17 04:25:15
 */
@Data
@TableName("rs_income_records")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "广告交易统计数据表")
public class IncomeRecordEntity extends Model<IncomeRecordEntity> {


	/**
	* 主键ID
	*/
    @ExcelIgnore
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键ID")
    private Long id;

	/**
    /**
    * 数据日期
    */
    @Getter
	@Setter
	@TableField(value = "date", exist = true)
    @Schema(description="数据日期")
    private LocalDate date;

    @ExcelProperty(value = "Date", index = 0, converter = CustomLocalDateConverter.class)
    // @DateTimeFormat("yyyy/MM/dd")
    @Schema(description="数据日期")
    @TableField(exist = false)
    private ConversionResult<LocalDate> dateResult;

	/**
	* 站点域名
	*/
	@ExcelProperty(value = "Site", index = 1)
    @Schema(description="站点域名")
    private String site;

	/**
	* 国家/地区
	*/
	@ExcelProperty(value = "Country", index = 2)
    @Schema(description="国家/地区")
    private String country;

	/**
	* 广告展示次数
	*/
	@ExcelProperty(value = "Ad Exchange impressions", index = 3, converter = CustomIntegerConverter.class)
    @Schema(description="广告展示次数")
    private Integer impressions;

	/**
	* 广告点击次数
	*/
	@ExcelProperty(value = "Ad Exchange clicks", index = 4, converter = CustomIntegerConverter.class)
    @Schema(description="广告点击次数")
    private Integer clicks;

	/**
	* 点击率(Click Through Rate)
	*/
	@ExcelProperty(value = "Ad Exchange CTR", index = 5, converter = PercentageConverter.class)
    @Schema(description="点击率(Click Through Rate)")
    private BigDecimal ctr;

	/**
	* 广告收入(美元)
	*/
	@ExcelProperty(value = "Ad Exchange revenue ($)", index = 6, converter = CustomBigDecimalConverter.class)
    @Schema(description="广告收入(美元)")
    private BigDecimal revenue;

	/**
	* 千次展示收益(Effective Cost Per Mille)
	*/
	@ExcelProperty(value = "Ad Exchange average eCPM ($)", index = 7, converter = CustomBigDecimalConverter.class)
    @Schema(description="千次展示收益(Effective Cost Per Mille)")
    private BigDecimal ecpm;

	/**
	* 广告请求数
	*/
	@ExcelProperty(value = "Ad Exchange ad requests", index = 8, converter = CustomIntegerConverter.class)
    @Schema(description="广告请求数")
    private Integer adRequests;

	/**
	* 广告匹配率(%)
	*/
	@ExcelProperty(value = "Ad Exchange match rate", index = 9, converter = PercentageConverter.class)
    @Schema(description="广告匹配率(%)")
    private BigDecimal matchRate;

	/**
	* 创建时间
	*/
    @ExcelIgnore
    @Schema(description="创建时间")
    private LocalDateTime createdAt;

	/**
	* 更新时间
	*/
    @ExcelIgnore
    @Schema(description="更新时间")
    private LocalDateTime updatedAt;


    @ExcelIgnore
    @TableField(exist = false)
    private String originalDate;

	@ExcelIgnore
	@TableField(exist = false)
	private String originalImpressions;
	
	@ExcelIgnore
	@TableField(exist = false)
	private String originalClicks;
	
	@ExcelIgnore
	@TableField(exist = false)
	private String originalCtr;
	
	@ExcelIgnore
	@TableField(exist = false)
	private String originalRevenue;
	
	@ExcelIgnore
	@TableField(exist = false)
	private String originalEcpm;
	
	@ExcelIgnore
	@TableField(exist = false)
	private String originalAdRequests;
	
	@ExcelIgnore
	@TableField(exist = false)
	private String originalMatchRate;

	// 添加便捷方法获取日期值
	public LocalDate getDate() {
		return dateResult != null && dateResult.isSuccess() ? dateResult.getValue() : date;
	}

	// 添加 setDate 方法
	public void setDate(LocalDate date) {
		this.date = date;
		if (date != null) {
			this.dateResult = ConversionResult.success(date, date.toString());
		}
	}

	// 在数据转换时保存原始值
	public void setImpressions(Integer impressions) {
		this.originalImpressions = String.valueOf(impressions);
		this.impressions = impressions;
	}
	
	public void setClicks(Integer clicks) {
		this.originalClicks = String.valueOf(clicks);
		this.clicks = clicks;
	}
	
	public void setCtr(BigDecimal ctr) {
		this.originalCtr = String.valueOf(ctr);
		this.ctr = ctr;
	}
	
	public void setRevenue(BigDecimal revenue) {
		this.originalRevenue = String.valueOf(revenue);
		this.revenue = revenue;
	}
	
	public void setEcpm(BigDecimal ecpm) {
		this.originalEcpm = String.valueOf(ecpm);
		this.ecpm = ecpm;
	}
	
	public void setAdRequests(Integer adRequests) {
		this.originalAdRequests = String.valueOf(adRequests);
		this.adRequests = adRequests;
	}
	
	public void setMatchRate(BigDecimal matchRate) {
		this.originalMatchRate = String.valueOf(matchRate);
		this.matchRate = matchRate;
	}

}
