--菜单更新

INSERT INTO `rsmanage`.`sys_menu` (`menu_id`, `name`, `en_name`, `permission`, `path`, `parent_id`, `icon`, `visible`, `sort_order`, `keep_alive`, `embedded`, `menu_type`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`) VALUES (1874699506504585218, '告警任务管理', 'quartz', NULL, '/daemon/job-manage/index', -1, 'ele-BellFilled', '1', 14, '0', NULL, '0', 'sadmin', '2025-01-02 14:09:27', 'sadmin', '2025-01-02 14:18:24', '0');

INSERT INTO `rsmanage`.`sys_role_menu` (`role_id`, `menu_id`) VALUES (3, 1874699506504585218);

