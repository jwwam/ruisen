package com.ruisen.rsmanage.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruisen.rsmanage.customer.entity.CustomersEntity;
import com.ruisen.rsmanage.customer.mapper.CustomersMapper;
import com.ruisen.rsmanage.customer.service.CustomersService;
import org.springframework.stereotype.Service;

/**
 * 客户信息表
 *
 * @author rsmanage
 * @date 2024-10-16 01:26:37
 */
@Service
public class CustomersServiceImpl extends ServiceImpl<CustomersMapper, CustomersEntity> implements CustomersService {

}
