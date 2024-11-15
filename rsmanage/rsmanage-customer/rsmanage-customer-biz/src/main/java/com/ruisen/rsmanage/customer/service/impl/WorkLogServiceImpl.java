package com.ruisen.rsmanage.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisen.rsmanage.customer.Po.PageDto;
import com.ruisen.rsmanage.customer.Po.ResponseDto;
import com.ruisen.rsmanage.customer.Po.WorkLogPo;
import com.ruisen.rsmanage.customer.entity.WorkEntity;
import com.ruisen.rsmanage.customer.entity.WorkLogEntity;
import com.ruisen.rsmanage.customer.mapper.WorkLogMapper;
import com.ruisen.rsmanage.customer.mapper.WorkMapper;
import com.ruisen.rsmanage.customer.service.WorkLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class WorkLogServiceImpl extends ServiceImpl<WorkLogMapper, WorkLogEntity> implements WorkLogService {
    
    private final WorkLogMapper workLogMapper;
    
    @Override
    public ResponseDto qryWorkLogs(Map<String, Object> param) {
        log.info("查询工单日志开始，查询条件：【{}】", param);
        ResponseDto rspMsg = new ResponseDto();
        
        int curPage = (int) param.get("curPage");
        int pageSize = (int) param.get("pageSize");
        List<WorkLogPo> result;
        
        if (!StringUtils.isEmpty(curPage) && !StringUtils.isEmpty(pageSize)) {
            try {
                Page<WorkLogPo> page = PageHelper.startPage(curPage, pageSize);
                result = workLogMapper.qryWorkLogs(param);
                PageInfo<WorkLogPo> pageInfo = new PageInfo<>(result);
                int totalRow = (int) pageInfo.getTotal();
                rspMsg.setPage(new PageDto(pageSize, curPage, totalRow));
            } finally {
                PageHelper.clearPage();
            }
        } else {
            result = workLogMapper.qryWorkLogs(param);
        }
        
        if (result == null || result.isEmpty()) {
            return new ResponseDto("ok", "数据为空");
        }
        
        rspMsg.setRetCode("ok");
        rspMsg.setRetMsg("查询成功");
        rspMsg.setData(result);
        
        log.info("查询工单日志结束");
        return rspMsg;
    }
}
