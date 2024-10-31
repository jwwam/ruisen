package com.ruisen.rsmanage.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruisen.rsmanage.customer.Po.ResponseDto;
import com.ruisen.rsmanage.customer.entity.CustomerPartnerEntity;

import java.util.Map;

public interface CustomerPartnerService extends IService<CustomerPartnerEntity> {
	ResponseDto qry(Map<String, Object> param);
}
