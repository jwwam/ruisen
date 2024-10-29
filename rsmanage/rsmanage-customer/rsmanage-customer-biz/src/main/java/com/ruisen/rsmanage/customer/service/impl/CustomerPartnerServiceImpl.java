package com.ruisen.rsmanage.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruisen.rsmanage.customer.entity.CustomerPartnerEntity;
import com.ruisen.rsmanage.customer.mapper.CustomerPartnerMapper;
import com.ruisen.rsmanage.customer.service.CustomerPartnerService;
import org.springframework.stereotype.Service;

/**
 * 客户与合作伙伴的合作关系表
 *
 * @author rsmanage
 * @date 2024-10-29 15:54:18
 */
@Service
public class CustomerPartnerServiceImpl extends ServiceImpl<CustomerPartnerMapper, CustomerPartnerEntity> implements CustomerPartnerService {

}
