use rsmanage
CREATE TABLE rs_contract_sequence (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    sequence_date DATE NOT NULL COMMENT '序列号日期',
    current_value INT NOT NULL COMMENT '当前序列号值',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sequence_date (sequence_date) COMMENT '日期唯一索引'
) COMMENT '合同编号序列表';