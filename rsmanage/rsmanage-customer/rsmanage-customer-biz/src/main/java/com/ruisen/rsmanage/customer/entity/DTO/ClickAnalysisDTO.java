package com.ruisen.rsmanage.customer.entity.DTO;

import java.util.List;

import lombok.Data;

@Data
public class ClickAnalysisDTO {
    private Long totalClicks;        // 总点击量
    private Double averageCtr;       // 平均点击率
    private Double maxCtr;           // 最高点击率
    private List<ClickRecordVO> records;  // 点击记录列表
}
