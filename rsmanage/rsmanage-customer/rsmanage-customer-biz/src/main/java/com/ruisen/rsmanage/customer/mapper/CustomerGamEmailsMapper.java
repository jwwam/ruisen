package com.ruisen.rsmanage.customer.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruisen.rsmanage.customer.Po.CustomerGamEmailsPo;
import com.ruisen.rsmanage.customer.Po.CustomerPartnerPo;
import com.ruisen.rsmanage.customer.entity.CustomerGamEmailsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerGamEmailsMapper extends BaseMapper<CustomerGamEmailsEntity> {
	List<CustomerGamEmailsPo> qry(@Param("email") String email, @Param("networkCode") String networkCode, @Param("partnerCode") String partnerCode, @Param("customerName") String customerName);
}
