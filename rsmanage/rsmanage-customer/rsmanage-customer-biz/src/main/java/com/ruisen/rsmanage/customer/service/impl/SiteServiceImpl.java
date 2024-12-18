package com.ruisen.rsmanage.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisen.rsmanage.customer.Po.PageDto;
import com.ruisen.rsmanage.customer.Po.ResponseDto;
import com.ruisen.rsmanage.customer.Po.SitePo;
import com.ruisen.rsmanage.customer.entity.SiteEntity;
import com.ruisen.rsmanage.customer.mapper.SiteMapper;
import com.ruisen.rsmanage.customer.service.SiteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 站点管理表
 *
 * @author rsmanage
 * @date 2024-12-12 00:49:22
 */
@Slf4j
@Service
@AllArgsConstructor
public class SiteServiceImpl extends ServiceImpl<SiteMapper, SiteEntity> implements SiteService {
	private final SiteMapper siteMapper ;
	@Override
	public ResponseDto qry(Map<String, Object> param) {
		log.info("查询站点表开始，查询条件：【{}】",param);
		// 需要分页查询
		ResponseDto rspMsg = new ResponseDto ();
		int curPage = (int) param.get("curPage");
		int pageSize = (int) param.get("pageSize");
		String doMain = (String) param.get("doMain");
		String customerId = (String) param.get("customerId");
		String status = (String) param.get("status");
		String hasLink = (String) param.get("hasLink");
		List<SitePo> result;
		if (!StringUtils.isEmpty(curPage) && !StringUtils.isEmpty(pageSize)){
			try {
				Page<SitePo> page = PageHelper.startPage(curPage, pageSize);
				result = siteMapper.qry(doMain,customerId,status,hasLink);
				for (SitePo sitePo : result) {
					//借用账号客户名称
					if(sitePo.getBorrowedCustomerId() != null && !sitePo.getBorrowedCustomerId().equals(null)){
						// 通过借用客户id获取借用客户姓名
						String BorrowedCustomerName = siteMapper.getCustomersId(Long.valueOf((sitePo.getBorrowedCustomerId())));
						sitePo.setBorrowedCustomerName(BorrowedCustomerName);
					}
					if(sitePo.getIsBorrowedAccount() == 0){
						sitePo.setBorrowedCustomerName(null);
					}
					//合作伙伴标识
					if (sitePo.getPartnerIds() != null && !sitePo.getPartnerIds().isEmpty()) {
						String ccStr = sitePo.getPartnerIds();
						String[] idArray = ccStr.split(",");
						// 存储合作伙伴标识列表
						List<String> nameList = new ArrayList<>();
						// 循环查询用户名称
						for (String idStr : idArray) {
							Long partnerId = Long.parseLong(idStr.trim());
							String partnerCode = siteMapper.getPartnersId(partnerId);
							if (partnerCode != null) {
								nameList.add(partnerCode);
							}
						}
						ccStr = String.join(",", nameList);
						sitePo.setPartnerIds(ccStr);
					}
				}
				PageInfo<SitePo> pageInfo = new PageInfo<SitePo>(result);
				log.info("分页参数:{}", pageInfo);
				int totalRow = (int) pageInfo.getTotal();
				int totalPage = (int) Math.ceil((double) totalRow / pageSize);
				rspMsg.setPage(new PageDto(pageSize, curPage, totalRow));
			} finally {
				PageHelper.clearPage();
			}
		}else {
			result = siteMapper.qry(doMain,customerId,status,hasLink);
		}
		if (result == null || result.size() == 0) {
			return new ResponseDto("ok", "数据为空");
		}

		rspMsg.setRetCode("ok");
		rspMsg.setRetMsg("查询成功");
		rspMsg.setData(result);
		System.out.println(rspMsg);
		log.info("查询站点列表数据结束",rspMsg);
		return rspMsg;
	}
}
