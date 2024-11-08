USE rsmanage;
ALTER TABLE rs_work_form MODIFY COLUMN deadline TIMESTAMP not NULL COMMENT '截止日期';