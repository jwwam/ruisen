package com.ruisen.rsmanage.customer.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruisen.rsmanage.customer.Po.ContractPo;
import com.ruisen.rsmanage.customer.Po.CustomerPartnerPo;
import com.ruisen.rsmanage.customer.entity.ContractEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContractMapper extends BaseMapper<ContractEntity> {
	List<ContractPo> qry(@Param("customerId") String customerId, @Param("ContractName") String ContractName, @Param("ContractNumber") String ContractNumber, @Param("IsActive") String IsActive, @Param("SignedDate") String SignedDate);

}
