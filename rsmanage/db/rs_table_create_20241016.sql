use rsmanage;

CREATE TABLE work_orders (
                             order_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '工单唯一标识',
                             submitter_id BIGINT(20) NOT NULL COMMENT '提交人ID',
                             title VARCHAR(255) NOT NULL COMMENT '工单标题',
                             content LONGTEXT COMMENT '工单内容',
                             status INT DEFAULT 0 COMMENT '工单状态，0:待处理, 1:处理中, 2:已处理',
                             assignees  VARCHAR(255)  COMMENT '处理人ID',
                             attachments JSON COMMENT '附件路径列表，JSON格式',
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             FOREIGN KEY (submitter_id) REFERENCES sys_user(user_id)
) COMMENT='工单表';