use rsmanage;
-- ----------------------------
-- 新增客户表我方客户标识
-- ----------------------------
ALTER TABLE rs_customers
ADD COLUMN is_our_customer tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否我方客户 0-否 1-是' AFTER sales_rep_id;