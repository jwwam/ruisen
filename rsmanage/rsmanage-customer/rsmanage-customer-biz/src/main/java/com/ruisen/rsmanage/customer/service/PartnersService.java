package com.ruisen.rsmanage.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruisen.rsmanage.customer.entity.PartnersEntity;
import com.ruisen.rsmanage.customer.entity.RevenueSharesEntity;
import java.util.List;

public interface PartnersService extends IService<PartnersEntity> {
    List<RevenueSharesEntity> getRevenueSharesByPartnerId(Integer partnerId);
}
