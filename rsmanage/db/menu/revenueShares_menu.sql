-- 该脚本不要直接执行， 注意维护菜单的父节点ID 默认 父节点-1 , 
use rsmanage;
-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name)
values (1730233236925, '-1', '/rs/revenueShares/index', '', '0', 'icon-bangzhushouji', '0', null , '8', null , '分成比例管理');

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730233236926,1730233236925, 'rs_revenueShares_view', '1', null, '1',  '0', null, '0', null, '分成比例查看');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730233236927,1730233236925, 'rs_revenueShares_add', '1', null, '1',  '0', null, '1', null, '分成比例新增');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name)
values (1730233236928,1730233236925, 'rs_revenueShares_edit', '1', null, '1',  '0', null, '2', null, '分成比例修改');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730233236929,1730233236925, 'rs_revenueShares_del', '1', null, '1',  '0', null, '3', null, '分成比例删除');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730233236930,1730233236925, 'rs_revenueShares_export', '1', null, '1',  '0', null, '3', null, '导入导出');