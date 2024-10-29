-- 该脚本不要直接执行， 注意维护菜单的父节点ID 默认 父节点-1 , 
use rsmanage;
-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name)
values (1730193995835, '-1', '/rs/work/index', '', '0', 'icon-bangzhushouji', '0', null , '8', null , '工单管理');

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730193995836,1730193995835, 'rs_work_view', '1', null, '1',  '0', null, '0', null, '工单查看');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730193995837,1730193995835, 'rs_work_add', '1', null, '1',  '0', null, '1', null, '工单新增');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name)
values (1730193995838,1730193995835, 'rs_work_edit', '1', null, '1',  '0', null, '2', null, '工单修改');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730193995839,1730193995835, 'rs_work_del', '1', null, '1',  '0', null, '3', null, '工单删除');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730193995840,1730193995835, 'rs_work_export', '1', null, '1',  '0', null, '3', null, '导入导出');