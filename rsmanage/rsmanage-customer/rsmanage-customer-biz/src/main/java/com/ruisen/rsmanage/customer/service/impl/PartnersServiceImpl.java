package com.ruisen.rsmanage.customer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruisen.rsmanage.customer.entity.PartnersEntity;
import com.ruisen.rsmanage.customer.mapper.PartnersMapper;
import com.ruisen.rsmanage.customer.service.PartnersService;
import com.ruisen.rsmanage.customer.entity.RevenueSharesEntity;
import com.ruisen.rsmanage.customer.mapper.RevenueSharesMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 合作伙伴信息表
 *
 * @author rsmanage
 * @date 2024-10-15 21:20:48
 */
@Service
public class PartnersServiceImpl extends ServiceImpl<PartnersMapper, PartnersEntity> implements PartnersService {
    @Autowired
    private RevenueSharesMapper revenueSharesMapper;

    @Override
    public List<RevenueSharesEntity> getRevenueSharesByPartnerId(Integer partnerId) {
        return revenueSharesMapper.selectList(Wrappers.<RevenueSharesEntity>lambdaQuery().eq(RevenueSharesEntity::getPartnerId, partnerId));
    }
}
