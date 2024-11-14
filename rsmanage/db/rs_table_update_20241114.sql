ALTER TABLE rs_contracts 
ADD COLUMN contract_name varchar(200) COMMENT '合同名称';

ALTER TABLE rs_contracts
ADD COLUMN create_user_id bigint(20) COMMENT '创建人ID';