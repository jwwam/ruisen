package com.ruisen.rsmanage.customer.entity.DTO;

import java.util.List;

import lombok.Data;

@Data
public class CountryClicksTop10VO {
    private List<String> countries;  // 国家列表
    private List<Long> clicks;       // 点击量列表
}