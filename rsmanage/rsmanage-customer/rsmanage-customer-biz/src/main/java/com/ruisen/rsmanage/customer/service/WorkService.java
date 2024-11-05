package com.ruisen.rsmanage.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruisen.rsmanage.customer.Po.ResponseDto;
import com.ruisen.rsmanage.customer.entity.WorkEntity;

import java.util.Map;

public interface WorkService extends IService<WorkEntity> {
	ResponseDto qry(Map<String, Object> param);
}
