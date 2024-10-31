package com.ruisen.rsmanage.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruisen.rsmanage.customer.Po.ResponseDto;
import com.ruisen.rsmanage.customer.entity.CustomerGamEmailsEntity;

import java.util.Map;

public interface CustomerGamEmailsService extends IService<CustomerGamEmailsEntity> {
	ResponseDto qry(Map<String, Object> param);
}
