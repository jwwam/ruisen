-- 该脚本不要直接执行， 注意维护菜单的父节点ID 默认 父节点-1 , 
use rsmanage;
-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name)
values (1728998448158, '-1', '/rs/partners/index', '', '0', 'icon-bangzhushouji', '0', null , '8', null , '合作伙伴信息表管理');

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1728998448159,1728998448158, 'rs_partners_view', '1', null, '1',  '0', null, '0', null, '合作伙伴信息表查看');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1728998448160,1728998448158, 'rs_partners_add', '1', null, '1',  '0', null, '1', null, '合作伙伴信息表新增');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name)
values (1728998448161,1728998448158, 'rs_partners_edit', '1', null, '1',  '0', null, '2', null, '合作伙伴信息表修改');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1728998448162,1728998448158, 'rs_partners_del', '1', null, '1',  '0', null, '3', null, '合作伙伴信息表删除');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1728998448163,1728998448158, 'rs_partners_export', '1', null, '1',  '0', null, '3', null, '导入导出');