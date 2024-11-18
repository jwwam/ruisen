/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : rsmanage

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 17/11/2024 03:59:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for rs_work_log
-- ----------------------------
DROP TABLE IF EXISTS `rs_work_log`;
CREATE TABLE `rs_work_log` (
  `log_id` int NOT NULL COMMENT '工单操作日志唯一标识',
  `work_id` int NOT NULL COMMENT '工单唯一标识',
  `operation` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作类型（如创建、更新、关闭等）',
  `performed_by` bigint DEFAULT NULL COMMENT '操作执行者的用户 ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `details` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作详情或备注',
  PRIMARY KEY (`log_id`,`work_id`),
  KEY `work_id` (`work_id`) USING BTREE,
  CONSTRAINT `rs_work_l` FOREIGN KEY (`work_id`) REFERENCES `rs_work_form` (`work_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='工单操作表';

SET FOREIGN_KEY_CHECKS = 1;
