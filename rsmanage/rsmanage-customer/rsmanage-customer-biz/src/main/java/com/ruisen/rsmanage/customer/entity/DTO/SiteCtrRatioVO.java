package com.ruisen.rsmanage.customer.entity.DTO;

import java.util.List;

import lombok.Data;

@Data
public class SiteCtrRatioVO {
    private List<String> sites;      // 站点列表
    private List<Double> ctrs;       // 点击率列表
}
