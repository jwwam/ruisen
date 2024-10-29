-- 该脚本不要直接执行， 注意维护菜单的父节点ID 默认 父节点-1 , 
use rsmanage;
-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name)
values (1730190806068, '-1', '/rs/customerGamEmails/index', '', '0', 'icon-bangzhushouji', '0', null , '8', null , '客户GAM邮箱管理');

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730190806069,1730190806068, 'rs_customerGamEmails_view', '1', null, '1',  '0', null, '0', null, '客户GAM邮箱查看');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730190806070,1730190806068, 'rs_customerGamEmails_add', '1', null, '1',  '0', null, '1', null, '客户GAM邮箱新增');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name)
values (1730190806071,1730190806068, 'rs_customerGamEmails_edit', '1', null, '1',  '0', null, '2', null, '客户GAM邮箱修改');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730190806072,1730190806068, 'rs_customerGamEmails_del', '1', null, '1',  '0', null, '3', null, '客户GAM邮箱删除');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730190806073,1730190806068, 'rs_customerGamEmails_export', '1', null, '1',  '0', null, '3', null, '导入导出');