package com.ruisen.rsmanage.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruisen.rsmanage.customer.Po.ResponseDto;
import com.ruisen.rsmanage.customer.entity.SiteEntity;

import java.util.Map;

public interface SiteService extends IService<SiteEntity> {
	ResponseDto qry(Map<String, Object> param);
}
