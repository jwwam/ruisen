package com.ruisen.rsmanage.customer.entity.DTO;

import java.util.List;

import lombok.Data;

@Data
public class CtrTrendVO {
    private List<String> dates;      // 日期列表
    private List<Double> ctrs;       // 点击率列表
}
