
use rsmanage;
-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name)
values (1730188458255, '-1', '/rs/customerPartner/index', '', '0', 'icon-bangzhushouji', '0', null , '8', null , '客户&合作伙伴关系管理');

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730188458256,1730188458255, 'rs_customerPartner_view', '1', null, '1',  '0', null, '0', null, '客户&合作伙伴关系查看');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730188458257,1730188458255, 'rs_customerPartner_add', '1', null, '1',  '0', null, '1', null, '客户&合作伙伴关系新增');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name)
values (1730188458258,1730188458255, 'rs_customerPartner_edit', '1', null, '1',  '0', null, '2', null, '客户&合作伙伴关表修改');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730188458259,1730188458255, 'rs_customerPartner_del', '1', null, '1',  '0', null, '3', null, '客户&合作伙伴关系删除');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1730188458260,1730188458255, 'rs_customerPartner_export', '1', null, '1',  '0', null, '3', null, '导入导出');