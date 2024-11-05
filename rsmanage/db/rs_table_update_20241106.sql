use rsmanage;

ALTER TABLE rs_work_form
ADD COLUMN customer_id INT COMMENT '客户ID',
ADD COLUMN partner_id INT COMMENT '合作伙伴ID',
ADD COLUMN copy VARCHAR(255) COMMENT '抄送人',
ADD COLUMN category VARCHAR(255) COMMENT '工单分类',
ADD COLUMN priority VARCHAR(255) NOT NULL COMMENT '优先级',
ADD COLUMN deadline VARCHAR(10) NOT NULL COMMENT '截止日期';

ALTER TABLE rs_work_form MODIFY COLUMN attachments VARCHAR(1000) COMMENT '附件路径，多个用逗号分隔';
