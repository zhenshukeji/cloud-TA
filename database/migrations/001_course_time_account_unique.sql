-- =====================================================================
-- 云朵教务社区版 · 已有库升级脚本 001
-- 任务：OS022（课时账户并发正确性）
-- 适用：从「未包含 active_account 唯一键」的旧库升级（如 2026-09-24 前后建立的
--       yunduo_community_dev / 更早候选库）。全新安装不需要本脚本——
--       001_schema.sql 已直接包含该列与唯一键。
-- 幂等性：MySQL 8 不支持 ADD COLUMN IF NOT EXISTS，重复执行会报
--       1060 (Duplicate column) / 1061 (Duplicate key name)——这两种错误
--       等价于「已应用过」，升级校验脚本应放行；其余错误一律视为失败。
-- 前置校验：若旧库存在同一学员同一课程的多份未删除账户（历史并发脏数据），
--       唯一键会创建失败（报 1062）。请先执行下面的自查 SQL 并合并/软删多余行：
--         SELECT student_id, course_id, COUNT(*) c FROM bd_course_time
--         WHERE is_delete = 0 GROUP BY student_id, course_id HAVING c > 1;
-- =====================================================================

ALTER TABLE `bd_course_time`
    ADD COLUMN `active_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci
        GENERATED ALWAYS AS (if(`is_delete` = 0, concat(`student_id`, '|', `course_id`), NULL)) STORED
        COMMENT '未删除课时账户的唯一键：学员|课程（OS022）';

ALTER TABLE `bd_course_time`
    ADD UNIQUE KEY `uk_bd_course_time_active_account` (`active_account`) USING BTREE;
