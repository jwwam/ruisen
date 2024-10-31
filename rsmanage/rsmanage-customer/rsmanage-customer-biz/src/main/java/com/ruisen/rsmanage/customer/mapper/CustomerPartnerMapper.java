package com.ruisen.rsmanage.customer.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruisen.rsmanage.customer.Po.CustomerPartnerPo;
import com.ruisen.rsmanage.customer.entity.CustomerPartnerEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CustomerPartnerMapper extends BaseMapper<CustomerPartnerEntity> {

	List<CustomerPartnerPo>  qry(@Param("customerId") String customerId, @Param("customerName") String customerName,@Param("partnerCode") String partnerCode,@Param("partnerAccount") String partnerAccount);
}
