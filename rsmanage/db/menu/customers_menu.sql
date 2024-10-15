-- 该脚本不要直接执行， 注意维护菜单的父节点ID 默认 父节点-1 , 

-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name)
values (1729013197092, '-1', '/rs/customers/index', '', '0', 'icon-bangzhushouji', '0', null , '8', null , '客户信息管理');

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1729013197093,1729013197092, 'rs_customers_view', '1', null, '1',  '0', null, '0', null, '客户信息表查看');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1729013197094,1729013197092, 'rs_customers_add', '1', null, '1',  '0', null, '1', null, '客户信息表新增');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name)
values (1729013197095,1729013197092, 'rs_customers_edit', '1', null, '1',  '0', null, '2', null, '客户信息表修改');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1729013197096,1729013197092, 'rs_customers_del', '1', null, '1',  '0', null, '3', null, '客户信息表删除');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1729013197097,1729013197092, 'rs_customers_export', '1', null, '1',  '0', null, '3', null, '导入导出');