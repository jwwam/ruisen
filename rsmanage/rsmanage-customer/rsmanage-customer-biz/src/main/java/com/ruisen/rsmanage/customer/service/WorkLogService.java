package com.ruisen.rsmanage.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruisen.rsmanage.customer.Po.ResponseDto;
import com.ruisen.rsmanage.customer.entity.WorkLogEntity;

import java.util.Map;

public interface WorkLogService extends IService<WorkLogEntity> {
    ResponseDto qryWorkLogs(Map<String, Object> param);
}
