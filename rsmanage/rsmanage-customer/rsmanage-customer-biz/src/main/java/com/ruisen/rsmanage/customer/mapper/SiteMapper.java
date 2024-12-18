package com.ruisen.rsmanage.customer.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruisen.rsmanage.customer.Po.SitePo;
import com.ruisen.rsmanage.customer.entity.SiteEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SiteMapper extends BaseMapper<SiteEntity> {
	List<SitePo> qry(@Param("doMain") String doMain, @Param("customerId") String customerId, @Param("status") String status, @Param("hasLink") String hasLink);

	String getCustomersId(Long customerId);

	String getPartnersId(Long partnerdId);
}
