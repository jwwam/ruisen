package com.ruisen.rsmanage.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisen.rsmanage.customer.Po.CustomerGamEmailsPo;
import com.ruisen.rsmanage.customer.Po.CustomerPartnerPo;
import com.ruisen.rsmanage.customer.Po.PageDto;
import com.ruisen.rsmanage.customer.Po.ResponseDto;
import com.ruisen.rsmanage.customer.entity.CustomerGamEmailsEntity;
import com.ruisen.rsmanage.customer.mapper.CustomerGamEmailsMapper;
import com.ruisen.rsmanage.customer.mapper.CustomerPartnerMapper;
import com.ruisen.rsmanage.customer.service.CustomerGamEmailsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 客户GAM邮箱管理表
 *
 * @author rsmanage
 * @date 2024-10-29 16:33:25
 */
@Slf4j
@Service
@AllArgsConstructor
public class CustomerGamEmailsServiceImpl extends ServiceImpl<CustomerGamEmailsMapper, CustomerGamEmailsEntity> implements CustomerGamEmailsService {
	private final CustomerGamEmailsMapper customerGamEmailsMapper ;
	@Override
	public ResponseDto qry(Map<String, Object> param) {
		log.info("查询客户GEM邮箱表开始，查询条件：【{}】",param);
		// 需要分页查询
		ResponseDto rspMsg = new ResponseDto ();
		int curPage = (int) param.get("curPage");
		int pageSize = (int) param.get("pageSize");
		String email = (String) param.get("email");
		String customerName = (String) param.get("customerName");
		String PartnerCode = (String) param.get("PartnerCode");
		String networkCode = (String) param.get("networkCode");
		List<CustomerGamEmailsPo> result;
		if (!StringUtils.isEmpty(curPage) && !StringUtils.isEmpty(pageSize)){
			try {
				Page<CustomerGamEmailsPo> page = PageHelper.startPage(curPage, pageSize);
				result = customerGamEmailsMapper.qry(email,networkCode,PartnerCode,customerName);
				PageInfo<CustomerGamEmailsPo> pageInfo = new PageInfo<CustomerGamEmailsPo>(result);
				log.info("分页参数:{}", pageInfo);
				int totalRow = (int) pageInfo.getTotal();
				int totalPage = (int) Math.ceil((double) totalRow / pageSize);
				rspMsg.setPage(new PageDto(pageSize, curPage, totalRow));
			} finally {
				PageHelper.clearPage();
			}
		}else {
			result = customerGamEmailsMapper.qry(email,networkCode,PartnerCode,customerName);
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
