use rsmanage;
ALTER TABLE rs_revenue_shares ADD COLUMN higher_share DECIMAL(5, 2) COMMENT '上游分成比例';
use rsmanage;
ALTER TABLE sys_user DROP COLUMN is_admin;