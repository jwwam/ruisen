-- ----------------------------
-- Table structure for rs_income_records
-- ----------------------------
CREATE TABLE `rs_income_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `date` date NOT NULL COMMENT '数据日期',
  `site` varchar(100) NOT NULL COMMENT '站点域名',
  `country` varchar(50) DEFAULT NULL COMMENT '国家/地区',
  `impressions` int NOT NULL DEFAULT 0 COMMENT '广告展示次数',
  `clicks` int NOT NULL DEFAULT 0 COMMENT '广告点击次数',
  `ctr` decimal(10,8) NOT NULL DEFAULT 0.00 COMMENT '点击率(Click Through Rate)',
  `revenue` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '广告收入(美元)',
  `ecpm` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '千次展示收益(Effective Cost Per Mille)',
  `ad_requests` int NOT NULL DEFAULT 0 COMMENT '广告请求数',
  `match_rate` decimal(10,8) NOT NULL DEFAULT 0.00 COMMENT '广告匹配率(%)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_date` (`date`),
  KEY `idx_site` (`site`),
  KEY `idx_country` (`country`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='广告交易统计数据表';