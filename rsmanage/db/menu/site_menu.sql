-- 该脚本不要直接执行， 注意维护菜单的父节点ID 默认 父节点-1 , 

-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name)
values (1733935762326, '-1', '/rs/site/index', '', '0', 'icon-bangzhushouji', '0', null , '8', null , '站点管理');

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1733935762327,1733935762326, 'rs_site_view', '1', null, '1',  '0', null, '0', null, '站点查看');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1733935762328,1733935762326, 'rs_site_add', '1', null, '1',  '0', null, '1', null, '站点新增');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name)
values (1733935762329,1733935762326, 'rs_site_edit', '1', null, '1',  '0', null, '2', null, '站点修改');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1733935762330,1733935762326, 'rs_site_del', '1', null, '1',  '0', null, '3', null, '站点删除');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1733935762331,1733935762326, 'rs_site_export', '1', null, '1',  '0', null, '3', null, '导入导出');