package com.ruisen.rsmanage.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruisen.rsmanage.customer.entity.CustomerEntity;
import com.ruisen.rsmanage.customer.mapper.CustomerMapper;
import com.ruisen.rsmanage.customer.service.CustomerService;
import org.springframework.stereotype.Service;

/**
 * 客户信息表
 *
 * @author rsmanage
 * @date 2024-10-12 19:36:35
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, CustomerEntity> implements CustomerService {

}
