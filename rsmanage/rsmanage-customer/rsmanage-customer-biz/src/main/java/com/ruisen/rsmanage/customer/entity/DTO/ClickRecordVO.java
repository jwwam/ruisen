package com.ruisen.rsmanage.customer.entity.DTO;

import lombok.Data;

@Data
public class ClickRecordVO {
    private String date;             // 日期
    private String site;             // 站点
    private String country;          // 国家/地区
    private Long clicks;             // 点击次数
    private Long impressions;        // 展示次数
    private Double ctr;              // 点击率
}
