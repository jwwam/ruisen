package com.ruisen.rsmanage.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("rs_contract_sequence")
public class ContractSequence {
	@TableId(type = IdType.AUTO)
	private Long id;

	private LocalDate sequenceDate;

	private Integer currentValue;

	private LocalDateTime createdTime;

	private LocalDateTime updatedTime;
}