package com.ruisen.rsmanage.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisen.rsmanage.customer.Po.CustomerPartnerPo;
import com.ruisen.rsmanage.customer.Po.PageDto;
import com.ruisen.rsmanage.customer.Po.ResponseDto;
import com.ruisen.rsmanage.customer.entity.CustomerPartnerEntity;
import com.ruisen.rsmanage.customer.mapper.CustomerPartnerMapper;
import com.ruisen.rsmanage.customer.service.CustomerPartnerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 客户与合作伙伴的合作关系表
 *
 * @author rsmanage
 * @date 2024-10-29 15:54:18
 */
@Slf4j
@Service
@AllArgsConstructor
public class CustomerPartnerServiceImpl extends ServiceImpl<CustomerPartnerMapper, CustomerPartnerEntity> implements CustomerPartnerService {
	private final CustomerPartnerMapper customerPartnerMapper;
	@Override
	public ResponseDto qry(Map<String, Object> param) {
		log.info("查询客户与合作伙伴关系列表开始，查询条件：【{}】",param);
		// 需要分页查询
		ResponseDto rspMsg = new ResponseDto ();
		int curPage = (int) param.get("curPage");
		int pageSize = (int) param.get("pageSize");
		String customerId = (String) param.get("customerId");
		String customerName = (String) param.get("customerName");
		String PartnerCode = (String) param.get("PartnerCode");
		String PartnerAccount = (String) param.get("PartnerAccount");
		List<CustomerPartnerPo> result;
		if (!StringUtils.isEmpty(curPage) && !StringUtils.isEmpty(pageSize)){
			try {
				Page<CustomerPartnerPo> page = PageHelper.startPage(curPage, pageSize);
				result = customerPartnerMapper.qry(customerId,customerName,PartnerCode,PartnerAccount);
				PageInfo<CustomerPartnerPo> pageInfo = new PageInfo<CustomerPartnerPo>(result);
				log.info("分页参数:{}", pageInfo);
				int totalRow = (int) pageInfo.getTotal();
				int totalPage = (int) Math.ceil((double) totalRow / pageSize);
				rspMsg.setPage(new PageDto(pageSize, curPage, totalRow));
			} finally {
				PageHelper.clearPage();
			}
		}else {
			result = customerPartnerMapper.qry(customerId,customerName,PartnerCode,PartnerAccount);
		}
		if (result == null || result.size() == 0) {
			return new ResponseDto("ok", "数据为空");
		}

		rspMsg.setRetCode("ok");
		rspMsg.setRetMsg("查询成功");
		rspMsg.setData(result);
		System.out.println(rspMsg);
		log.info("查询客户与合作伙伴关系结束",rspMsg);
		return rspMsg;
	}
}
