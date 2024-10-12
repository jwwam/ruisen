-- 该脚本不要直接执行， 注意维护菜单的父节点ID 默认 父节点-1 , 

-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name)
values (1728732995167, '-1', '/rs/customer/index', '', '0', 'icon-bangzhushouji', '0', null , '8', null , '客户信息表管理');

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1728732995168,1728732995167, 'rs_customer_view', '1', null, '1',  '0', null, '0', null, '客户信息表查看');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1728732995169,1728732995167, 'rs_customer_add', '1', null, '1',  '0', null, '1', null, '客户信息表新增');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name)
values (1728732995170,1728732995167, 'rs_customer_edit', '1', null, '1',  '0', null, '2', null, '客户信息表修改');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1728732995171,1728732995167, 'rs_customer_del', '1', null, '1',  '0', null, '3', null, '客户信息表删除');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1728732995172,1728732995167, 'rs_customer_export', '1', null, '1',  '0', null, '3', null, '导入导出');