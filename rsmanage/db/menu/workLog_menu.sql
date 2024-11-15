-- 该脚本不要直接执行， 注意维护菜单的父节点ID 默认 父节点-1 , 
use rsmanage;
-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name)
values (1731608915506, '-1', '/rs/workLog/index', '', '0', 'iconfont icon-fuwenbenkuang', '0', null , '8', null , '工单操作管理');

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1731608915507,1731608915506, 'rs_workLog_view', '1', null, '1',  '0', null, '0', null, '工单操作查看');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1731608915508,1731608915506, 'rs_workLog_add', '1', null, '1',  '0', null, '1', null, '工单操作新增');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name)
values (1731608915509,1731608915506, 'rs_workLog_edit', '1', null, '1',  '0', null, '2', null, '工单操作修改');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1731608915510,1731608915506, 'rs_workLog_del', '1', null, '1',  '0', null, '3', null, '工单操作删除');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1731608915511,1731608915506, 'rs_workLog_export', '1', null, '1',  '0', null, '3', null, '导入导出');