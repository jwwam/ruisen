use rsmanage;

CREATE TABLE `rs_site_management` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `domain` varchar(255) NOT NULL COMMENT '域名',
    `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 1-正常 2-禁用',
    `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
    `partner_ids` varchar(500) NOT NULL COMMENT '上游合作伙伴ID列表，多个用逗号分隔',
    `is_borrowed_account` tinyint(1) DEFAULT '0' COMMENT '是否借用账号 0-否 1-是',
    `borrowed_customer_id` bigint(20) DEFAULT NULL COMMENT '借用账号客户ID',
    `gam_email_id` bigint(20) NOT NULL COMMENT '客户GAM邮箱',
    `has_link` tinyint(1) DEFAULT '0' COMMENT '是否放链接 0-否 1-是',
    `review_account` varchar(255) DEFAULT NULL COMMENT '站点提审账号',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站点管理表';