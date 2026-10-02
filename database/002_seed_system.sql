-- =====================================================================
-- 云朵教务社区版 · 系统种子（单机构 / 单校区 / 三类角色 / 最小菜单）
-- 文件：002_seed_system.sql        任务：OS006
-- 生成时间：2026-09-23 20:27
-- ---------------------------------------------------------------------
-- 执行前提：已按 001_schema.sql 建好空库结构。
-- 重复执行：幂等。所有 INSERT 均带显式主键并附 ON DUPLICATE KEY UPDATE
--   自赋值子句（`col` = `col` 为无操作），重复执行结果为 no-op 成功，
--   不产生半套数据，可直接用于容器启动时的重复初始化。
-- 管理员口令：不在本文件内。sys_user.password 初始为空串，由后端启动时的
--   一次性初始化流程读取环境变量 INITIAL_ADMIN_PASSWORD 后写入 BCrypt 哈希；
--   未设置该变量时后端必须启动失败（见 OS009 验收）。
-- 数据虚构：机构/校区/人员名称均为示例值，不含真实手机号。
-- =====================================================================

START TRANSACTION;

INSERT INTO `community_seed_marker` (`version`, `applied_at`, `note`)
  VALUES ('community/v0.1.0', NOW(), '单机构单校区系统种子')
ON DUPLICATE KEY UPDATE `version` = `version`;

-- 机构 / 校区 / 部门
INSERT INTO `bd_mechanism` (`id`, `name`, `charge_name`, `phone_num`, `license_code`, `license`, `permit_code`, `permit`, `logo`, `main_img`, `refuse_reason`, `invite_code`, `status`, `audited_time`, `create_time`, `create_by`, `update_time`, `update_by`, `is_delete`)
  VALUES (1, '示例教育培训机构', '示例负责人', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'COMMUNITY', 2, NOW(), NOW(), 1, NULL, NULL, 0)
ON DUPLICATE KEY UPDATE `id` = `id`;
INSERT INTO `bd_campus` (`id`, `name`, `address`, `mec_id`, `province`, `city`, `mgr_name`, `mgr_phone`, `mgr_contact_number`, `latitude`, `longitude`, `is_member`, `member_end_time`, `is_delete`, `create_time`, `create_by`, `update_time`, `update_by`)
  VALUES (1, '示例校区', '', 1, '', '', NULL, NULL, NULL, NULL, NULL, 0, NULL, 0, NOW(), 1, NULL, NULL)
ON DUPLICATE KEY UPDATE `id` = `id`;
INSERT INTO `sys_dept` (`dept_id`, `mec_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `status`, `del_flag`, `create_by`, `create_time`)
  VALUES (1, 1, 0, '', '示例教育培训机构', 0, NULL, '0', '0', 'system', NOW())
ON DUPLICATE KEY UPDATE `dept_id` = `dept_id`;

-- 人员：机构管理员(1) / 教务(2,3) —— 密码列留空，由后端初始化流程写入
INSERT INTO `bd_mec_personnel` (`id`, `mec_id`, `name`, `contact_number`, `phone`, `type`, `create_by`, `create_time`, `is_delete`)
  VALUES (1, 1, '示例机构管理员', NULL, 'admin', 0, 1, NOW(), 0)
ON DUPLICATE KEY UPDATE `id` = `id`;
INSERT INTO `bd_campus_personnel` (`id`, `mec_id`, `campus_id`, `campus_name`, `type`, `name`, `phone`, `contact_number`, `join_time`, `is_delete`, `create_by`, `create_time`)
  VALUES (2, 1, 1, '示例校区', 1, '示例教务主管', 'academic', NULL, NOW(), 0, 1, NOW()),
         (3, 1, 1, '示例校区', 1, '示例授课教师', 'teacher', NULL, NOW(), 0, 1, NOW())
ON DUPLICATE KEY UPDATE `id` = `id`;
INSERT INTO `bd_campus_connection` (`id`, `mec_id`, `mec_personnel_id`, `campus_id`, `campus_name`, `create_by`, `create_time`, `is_delete`)
  VALUES (1, 1, 1, 1, '示例校区', 1, NOW(), 0)
ON DUPLICATE KEY UPDATE `id` = `id`;
INSERT INTO `bd_teacher` (`id`, `mec_id`, `campus_id`, `name`, `phone`, `sex`, `induction_time`, `taught_num`, `is_departure`, `version`, `is_delete`, `create_time`, `create_by`, `person_type`, `association_person`)
  VALUES (1, 1, 1, '示例授课教师', '', NULL, NOW(), 0, 0, 0, 0, NOW(), 1, 1, 3)
ON DUPLICATE KEY UPDATE `id` = `id`;

-- 账号：type 0=机构人员 1=校区人员（UserConstants / setUserEntity 口径）
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `type`, `association_id`, `status`, `del_flag`, `create_by`, `create_time`, `remark`)
  VALUES (1, 1, 'admin', '示例机构管理员', '00', '', '', '0', '', '', '0', 1, '0', '0', 'system', NOW(), '社区版初始管理员，首次启动时由 INITIAL_ADMIN_PASSWORD 设置口令'),
         (2, 1, 'academic', '示例教务主管', '00', '', '', '1', '', '', '1', 2, '0', '0', 'system', NOW(), '教务角色示例账号'),
         (3, 1, 'teacher', '示例授课教师', '00', '', '', '1', '', '', '1', 3, '0', '0', 'system', NOW(), '教师角色示例账号')
ON DUPLICATE KEY UPDATE `user_id` = `user_id`;

-- 角色（role_type 0=机构/校区角色）
INSERT INTO `sys_role` (`role_id`, `role_type`, `mec_id`, `campus_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `remark`)
  VALUES (1, 0, 1, NULL, '机构管理员', 'MEC_ADMIN_1', 1, '1', '0', '0', 'system', NOW(), '机构管理员：机构端全部权限'),
         (2, 0, 1, 1, '教务主管', 'CAMPUS_ACADEMIC_1', 2, '1', '0', '0', 'system', NOW(), '教务：课程、班级、排课、点名、统计'),
         (3, 0, 1, 1, '授课教师', 'CAMPUS_LECTURER_1', 3, '1', '0', '0', 'system', NOW(), '教师：课表与上课记录')
ON DUPLICATE KEY UPDATE `role_id` = `role_id`;
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `user_type`) VALUES (1, 1, 0), (2, 2, 0), (3, 3, 0)
ON DUPLICATE KEY UPDATE `user_id` = `user_id`;

-- 菜单（mec_type: 0=机构级 1=校区级；F=按钮权限）
INSERT INTO `sys_menu` (`menu_id`, `mec_type`, `menu_perm_type`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`)
  VALUES
  (1000, 1, NULL, '首页', 0, 0, '/', 'index.vue', 1, 'C', '0', '0', 'home:index:list', 'bells'),
  (1001, 1, NULL, '首页', 1000, 1, 'home', 'index.vue', 1, 'C', '0', '0', 'home:index:list', '404'),
  (1100, 0, 0, '机构信息', 0, 1, 'mechanism', NULL, 1, 'M', '0', '0', NULL, 'system'),
  (1101, 0, 0, '机构信息', 1100, 1, 'info', 'mechanism/info/index', 1, 'C', '0', '0', 'base:mechanism:list', 'clipboard'),
  (1102, 0, NULL, '机构校区', 1100, 2, 'region', 'mechanism/region/index', 1, 'C', '0', '0', 'base:campus:list', 'mechanism'),
  (1103, 0, NULL, '机构人员 ', 1100, 3, 'people', 'mechanism/people/index', 1, 'C', '0', '0', 'base:mec_personnel:list', 'peoples'),
  (1200, 1, NULL, '教务中心', 0, 4, 'educenter', NULL, 1, 'M', '0', '0', NULL, 'education'),
  (1201, 1, NULL, '课程管理', 1200, 1, 'course', 'educenter/course/index', 1, 'C', '0', '0', 'educenter:course:list', 'book'),
  (1202, 1, NULL, '学员管理', 1200, 2, 'student', 'educenter/student/index', 1, 'C', '0', '0', 'educenter:student:list', 'people'),
  (1203, 1, NULL, '班级信息', 1200, 3, 'class', 'educenter/class/index', 1, 'C', '0', '0', 'educenter:class:list', 'peoples'),
  (1204, 1, NULL, '报读情况', 1200, 4, 'enrollment', 'educenter/enrollment/index', 1, 'C', '0', '0', 'educenter:enrollment:list', 'dict'),
  (1205, 1, NULL, '课表管理', 1200, 5, 'curriculum', 'curriculum.vue', 1, 'C', '0', '0', 'educenter:curriculum:list', 'build'),
  (1206, 1, NULL, '上课记录', 1200, 6, 'record', 'educenter/record/index', 1, 'C', '0', '0', 'educenter:record:list', 'checkbox'),
  (1207, 1, NULL, '教师管理', 1200, 7, 'teacher', 'educenter/teacher/index', 1, 'C', '0', '0', 'educenter:teacher:list', 'info'),
  (1300, 1, NULL, '统计数据', 0, 9, 'statistics', NULL, 1, 'M', '0', '0', NULL, 'chart'),
  (1301, 1, NULL, '班级明细报表', 1300, 1, 'class', 'statistics/class/index', 1, 'C', '0', '0', 'statistics:class:list', 'peoples'),
  (1302, 1, NULL, '学生考勤统计', 1300, 2, 'attendance', 'statistics/attendance/index', 1, 'C', '0', '0', 'statistics:attendance:list', 'date'),
  (1303, 1, NULL, '学生课次统计', 1300, 3, 'classTime', 'statistics/classTime/index', 1, 'C', '0', '0', 'statistics:classTime:list', 'monitor'),
  (1400, 1, NULL, '基础信息', 0, 1, 'baseInfo', NULL, 1, 'M', '0', '0', NULL, 'logininfor'),
  (1401, NULL, NULL, '用户管理', 1400, 1, 'user', 'system/user/index', 1, 'C', '0', '0', 'system:user:list', 'user'),
  (1402, 1, NULL, '角色管理', 1400, 3, 'role', 'system/role/index', 1, 'C', '0', '0', 'system:role:list', 'tree'),
  (1403, 1, NULL, '菜单管理', 1400, 4, 'menu', 'system/menu/index', 1, 'C', '0', '0', 'system:menu:list', 'tree-table'),
  (1404, NULL, NULL, '字典管理', 1400, 5, 'dict', 'system/dict/index', 1, 'C', '0', '0', 'system:dict:list', 'dict'),
  (1406, 1, NULL, '校区人员', 1400, 2, 'campusPeople', 'baseInfo/campusPeople/index', 1, 'C', '0', '0', 'base:campus_personnel:list', 'peoples'),
  (1407, NULL, NULL, '日志管理', 1400, 6, 'log', 'system/log/index', 1, 'M', '0', '0', NULL, 'log'),
  (1408, NULL, NULL, '操作日志', 1407, 1, 'operlog', 'monitor/operlog/index', 1, 'C', '0', '0', 'monitor:operlog:list', 'form'),
  (1409, NULL, NULL, '登录日志', 1407, 2, 'logininfor', 'monitor/logininfor/index', 1, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor'),
  (2000, NULL, NULL, '用户查询', 1401, 1, NULL, NULL, 1, 'F', '0', '0', 'system:user:query', '#'),
  (2001, NULL, NULL, '用户新增', 1401, 2, NULL, NULL, 1, 'F', '0', '0', 'system:user:add', '#'),
  (2002, NULL, NULL, '用户修改', 1401, 3, NULL, NULL, 1, 'F', '0', '0', 'system:user:edit', '#'),
  (2003, NULL, NULL, '用户删除', 1401, 4, NULL, NULL, 1, 'F', '0', '0', 'system:user:remove', '#'),
  (2004, NULL, NULL, '用户导出', 1401, 5, NULL, NULL, 1, 'F', '0', '0', 'system:user:export', '#'),
  (2005, NULL, NULL, '用户导入', 1401, 6, NULL, NULL, 1, 'F', '0', '0', 'system:user:import', '#'),
  (2006, NULL, NULL, '重置密码', 1401, 7, NULL, NULL, 1, 'F', '0', '0', 'system:user:resetPwd', '#'),
  (2007, NULL, NULL, '菜单查询', 1403, 1, NULL, NULL, 1, 'F', '0', '0', 'system:menu:query', '#'),
  (2008, NULL, NULL, '菜单新增', 1403, 2, NULL, NULL, 1, 'F', '0', '0', 'system:menu:add', '#'),
  (2009, NULL, NULL, '菜单修改', 1403, 3, NULL, NULL, 1, 'F', '0', '0', 'system:menu:edit', '#'),
  (2010, NULL, NULL, '菜单删除', 1403, 4, NULL, NULL, 1, 'F', '0', '0', 'system:menu:remove', '#'),
  (2011, NULL, NULL, '字典查询', 1404, 1, '#', NULL, 1, 'F', '0', '0', 'system:dict:query', '#'),
  (2012, NULL, NULL, '字典新增', 1404, 2, '#', NULL, 1, 'F', '0', '0', 'system:dict:add', '#'),
  (2013, NULL, NULL, '字典修改', 1404, 3, '#', NULL, 1, 'F', '0', '0', 'system:dict:edit', '#'),
  (2014, NULL, NULL, '字典删除', 1404, 4, '#', NULL, 1, 'F', '0', '0', 'system:dict:remove', '#'),
  (2015, NULL, NULL, '字典导出', 1404, 5, '#', NULL, 1, 'F', '0', '0', 'system:dict:export', '#'),
  (2020, NULL, NULL, '操作查询', 1408, 1, '#', NULL, 1, 'F', '0', '0', 'monitor:operlog:query', '#'),
  (2021, NULL, NULL, '操作删除', 1408, 2, '#', NULL, 1, 'F', '0', '0', 'monitor:operlog:remove', '#'),
  (2022, NULL, NULL, '日志导出', 1408, 4, '#', NULL, 1, 'F', '0', '0', 'monitor:operlog:export', '#'),
  (2023, NULL, NULL, '登录查询', 1409, 1, '#', NULL, 1, 'F', '0', '0', 'monitor:logininfor:query', '#'),
  (2024, NULL, NULL, '登录删除', 1409, 2, '#', NULL, 1, 'F', '0', '0', 'monitor:logininfor:remove', '#'),
  (2025, NULL, NULL, '日志导出', 1409, 3, '#', NULL, 1, 'F', '0', '0', 'monitor:logininfor:export', '#')
ON DUPLICATE KEY UPDATE `menu_id` = `menu_id`;

-- 角色-菜单授权
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
  (1, 1000),
  (1, 1001),
  (1, 1100),
  (1, 1101),
  (1, 1102),
  (1, 1103),
  (1, 1200),
  (1, 1201),
  (1, 1202),
  (1, 1203),
  (1, 1204),
  (1, 1205),
  (1, 1206),
  (1, 1207),
  (1, 1300),
  (1, 1301),
  (1, 1302),
  (1, 1303),
  (1, 1400),
  (1, 1401),
  (1, 1402),
  (1, 1403),
  (1, 1404),
  (1, 1406),
  (1, 1407),
  (1, 1408),
  (1, 1409),
  (1, 2000),
  (1, 2001),
  (1, 2002),
  (1, 2003),
  (1, 2004),
  (1, 2005),
  (1, 2006),
  (1, 2007),
  (1, 2008),
  (1, 2009),
  (1, 2010),
  (1, 2011),
  (1, 2012),
  (1, 2013),
  (1, 2014),
  (1, 2015),
  (1, 2020),
  (1, 2021),
  (1, 2022),
  (1, 2023),
  (1, 2024),
  (1, 2025),
  (2, 1000),
  (2, 1001),
  (2, 1200),
  (2, 1201),
  (2, 1202),
  (2, 1203),
  (2, 1204),
  (2, 1205),
  (2, 1206),
  (2, 1207),
  (2, 1300),
  (2, 1301),
  (2, 1302),
  (2, 1303),
  (3, 1000),
  (3, 1001),
  (3, 1200),
  (3, 1201),
  (3, 1202),
  (3, 1203),
  (3, 1204),
  (3, 1205),
  (3, 1206),
  (3, 1207)
ON DUPLICATE KEY UPDATE `role_id` = `role_id`;

COMMIT;

