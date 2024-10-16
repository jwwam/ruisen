use rsmanage;
-- ----------------------------
-- 修改客户表约束条件和商务id类型
-- ----------------------------
ALTER TABLE rs_customers
    MODIFY COLUMN `linkman` varchar(50)  NULL COMMENT '联系人',
    MODIFY COLUMN `email` varchar(100)   NULL COMMENT '客户电子邮件'
    MODIFY COLUMN `sales_rep_id` varchar(64)  NOT NULL COMMENT '对应我方商务id';、

-- ----------------------------
-- 去除客户邮箱唯一索引（邮箱可能为空）
-- ----------------------------
ALTER TABLE rs_customers
DROP INDEX email;
