package com.ruisen.rsmanage.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruisen.rsmanage.customer.Po.WorkLogPo;
import com.ruisen.rsmanage.customer.entity.WorkLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkLogMapper extends BaseMapper<WorkLogEntity> {
    List<WorkLogPo> qryWorkLogs(@Param("param") Map<String, Object> param);
}
