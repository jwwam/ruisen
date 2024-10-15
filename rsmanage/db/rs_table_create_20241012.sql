use rsmanage;

CREATE TABLE rs_customers (
                                             customer_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '客户唯一标识',
                                             name VARCHAR(50) NOT NULL COMMENT '客户名称',
                                             linkman VARCHAR(50) NOT NULL COMMENT '联系人',
                                             email VARCHAR(100) NOT NULL UNIQUE COMMENT '客户电子邮件',
                                             phone_number VARCHAR(15) COMMENT '客户电话号码',
                                             sales_rep_id BIGINT(50) COMMENT '对应的我方商务人员ID',
                                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间'
          ) COMMENT='客户信息表';

CREATE TABLE rs_partners (
                                                   partner_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '合作伙伴唯一标识',
                                                   partner_name VARCHAR(100) NOT NULL COMMENT '合作伙伴名称',
                                                   partner_code VARCHAR(50) UNIQUE COMMENT '合作伙伴标识',
                                                   revenue_share DECIMAL(5, 2) COMMENT '在达成某个条件时我方的分成比例(百分比)',
                                                   start_date DATE COMMENT '合作开始时间',
                                                   end_date DATE COMMENT '合作结束时间',
                                                   valid_days INT COMMENT '分成有效天数',
                                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间'
          ) COMMENT='合作伙伴信息表';

CREATE TABLE rs_customer_partner_relations (
                                                                     relation_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '合作关系唯一标识',
                                                                     customer_id INT NOT NULL COMMENT '客户ID',
                                                                     partner_id INT NOT NULL COMMENT '合作伙伴ID',
                                                                     customer_revenue_share DECIMAL(5, 2) COMMENT '客户分成比例（百分比）',
                                                                     partner_account VARCHAR(100) COMMENT '客户在合作伙伴平台的账号',
                                                                     partner_password VARCHAR(100) COMMENT '客户在合作伙伴平台的密码',
                                                                     ads_txt_content TEXT COMMENT '对应的ads.txt内容',
                                                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                                                                     FOREIGN KEY (customer_id) REFERENCES rs_customers(customer_id),
                                                                     FOREIGN KEY (partner_id) REFERENCES rs_partners(partner_id)
          ) COMMENT='客户与合作伙伴的合作关系表';

CREATE TABLE rs_customer_gam_emails (
                                                              email_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '邮箱记录唯一标识',
                                                              customer_id INT NOT NULL COMMENT '客户ID',
                                                              email VARCHAR(100) NOT NULL COMMENT 'GAM邮箱',
                                                              network_code VARCHAR(50) COMMENT 'NetworkCode',
                                                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                                                              FOREIGN KEY (customer_id) REFERENCES rs_customers(customer_id)
          ) COMMENT='客户GAM邮箱管理表';

CREATE TABLE rs_contracts (
                                                    contract_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '合同记录唯一标识',
                                                    contract_number VARCHAR(50) NOT NULL UNIQUE COMMENT '合同编号',
                                                    customer_id INT NOT NULL COMMENT '客户ID',
                                                    is_active BOOLEAN NOT NULL DEFAULT FALSE COMMENT '合同是否生效',
                                                    signed_date DATE COMMENT '签署日期',
                                                    payee_name VARCHAR(100) COMMENT '收款人名称',
                                                    payee_account VARCHAR(100) COMMENT '收款人账号',
                                                    bank_name VARCHAR(100) COMMENT '银行名称',
                                                    bank_address VARCHAR(255) COMMENT '银行地址',
                                                    swift_code VARCHAR(20) COMMENT 'SwiftCode',
                                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                                                    FOREIGN KEY (customer_id) REFERENCES rs_customers(customer_id)
          ) COMMENT='合同管理表';