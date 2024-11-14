package com.ruisen.rsmanage.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruisen.rsmanage.customer.Po.ResponseDto;
import com.ruisen.rsmanage.customer.entity.ContractEntity;

import java.util.Map;


public interface ContractService extends IService<ContractEntity> {
	String getNextSequence(String signedDate);

	ResponseDto qry(Map<String, Object> param);

}
