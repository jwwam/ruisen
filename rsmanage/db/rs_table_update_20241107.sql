use rsmanage
ALTER TABLE rs_work_form MODIFY COLUMN status int(1) NOT NULL COMMENT '工单状态(0:待处理, 1:处理中, 2:已处理, 3:已终止)';

ALTER TABLE rs_work_form
ADD COLUMN handle_time datetime DEFAULT NULL COMMENT '处理时间';
ALTER TABLE rs_work_form
ADD COLUMN handle_opinion varchar(500) DEFAULT NULL COMMENT '处理意见';