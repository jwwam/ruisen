package com.ruisen.rsmanage.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruisen.rsmanage.customer.entity.ContractSequence;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;

@Mapper
public interface ContractSequenceMapper extends BaseMapper<ContractSequence> {
	@Select("SELECT * FROM rs_contract_sequence WHERE sequence_date = #{date}")
	ContractSequence selectByDate(@Param("date") LocalDate date);

	@Insert("INSERT INTO rs_contract_sequence (sequence_date, current_value) VALUES (#{sequenceDate}, #{currentValue})")
	int insert(ContractSequence sequence);

	@Update("UPDATE rs_contract_sequence SET current_value = current_value + 1 WHERE sequence_date = #{date}")
	int incrementSequence(@Param("date") LocalDate date);
}