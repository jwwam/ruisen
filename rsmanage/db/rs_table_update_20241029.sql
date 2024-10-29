use rsmanage;

-- 删除外键关系
ALTER TABLE rs_customer_partner_relations
    DROP FOREIGN KEY rs_customer_partner_relations_ibfk_2;

-- 修改rs_customer_partner_relations表的partner_id为partner_code
ALTER TABLE rs_customer_partner_relations CHANGE COLUMN partner_id partner_code VARCHAR(255) NOT NULL COMMENT '合作伙伴标识';

-- 增加新的外键关系为partner_code
ALTER TABLE rs_customer_partner_relations
    ADD CONSTRAINT rs_customer_partner_relations_ibfk_2 FOREIGN KEY (partner_code) REFERENCES rs_partners(partner_code);

-- 增加rs_customer_partner_relations表url字段
ALTER TABLE rs_customer_partner_relations ADD COLUMN url VARCHAR(255) NULL COMMENT '客户在合作伙伴平台的地址';

-- 增加rs_customer_gam_emails表partner_code字段
ALTER TABLE rs_customer_gam_emails ADD COLUMN partner_code VARCHAR(50) COMMENT '合作伙伴标识';

-- 增加rs_customer_gam_emails表ads_txt_content字段
ALTER TABLE rs_customer_gam_emails ADD COLUMN ads_txt_content TEXT COMMENT 'ads文件内容';

-- 增加rs_customer_gam_emails表name字段
ALTER TABLE rs_customer_gam_emails ADD COLUMN name VARCHAR(100) COMMENT '用户名';

-- 增加rs_customer_gam_emails表password字段
ALTER TABLE rs_customer_gam_emails ADD COLUMN password VARCHAR(100) COMMENT '密码';

-- 删除旧表
DROP TABLE IF EXISTS work_orders;
-- 增加新工单表
CREATE TABLE rs_work_form (
                              work_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '工单唯一标识',
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

-- 增加分成比例表
CREATE TABLE rs_revenue_shares (
                                share_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分成比例唯一标识',
                                partner_id INT COMMENT '合作伙伴ID',
                                name VARCHAR(255) COMMENT '分成比例名称',
                                share DECIMAL(5, 2) COMMENT '分成比例',
                                description TEXT COMMENT '分成比例描述',
                                valid_days INT COMMENT '有效天数',
                                is_active BOOLEAN COMMENT '是否启用',
                                sort_order INT COMMENT '排序顺序',
                                FOREIGN KEY (partner_id) REFERENCES partners(partner_id)
) COMMENT='分成比例表';