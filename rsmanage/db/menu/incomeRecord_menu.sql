-- 该脚本不要直接执行， 注意维护菜单的父节点ID 默认 父节点-1 , 

-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name)
values (1731788715849, '-1', '/rs/incomeRecord/index', '', '0', 'ele-Handbag', '0', null , '8', null , '收入管理');

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1731788715850,1731788715849, 'rs_incomeRecord_view', '1', null, '1',  '0', null, '0', null, '收入查看');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1731788715851,1731788715849, 'rs_incomeRecord_add', '1', null, '1',  '0', null, '1', null, '收入新增');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name)
values (1731788715852,1731788715849, 'rs_incomeRecord_edit', '1', null, '1',  '0', null, '2', null, '收入修改');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1731788715853,1731788715849, 'rs_incomeRecord_del', '1', null, '1',  '0', null, '3', null, '收入删除');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1731788715854,1731788715849, 'rs_incomeRecord_export', '1', null, '1',  '0', null, '3', null, '导入导出');