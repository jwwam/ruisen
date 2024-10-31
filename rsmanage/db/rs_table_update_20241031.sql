use rsmanage;
ALTER TABLE sys_user
ADD COLUMN is_admin CHAR(1) COMMENT '管理员标记，0管理员，1非管理员';


ALTER TABLE rs_customers
ADD COLUMN company_name VARCHAR(255) COMMENT '客户主题（公司名称）',
ADD COLUMN finance_contact VARCHAR(255) COMMENT '对方公司财务联系人',
ADD COLUMN finance_contact_user VARCHAR(255) COMMENT '对方公司财务人员',
ADD COLUMN finance_email VARCHAR(255) COMMENT '对应的财务人员邮箱',
ADD COLUMN finance_phone VARCHAR(20) COMMENT '财务人员电话';
