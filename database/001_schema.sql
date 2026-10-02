-- =====================================================================
-- 云朵教务社区版 · 数据库结构（纯结构，无任何业务数据）
-- 文件：001_schema.sql        任务：OS006
-- 生成时间：2026-09-23 20:27
-- 版本：community/v0.1.0      许可证：AGPL-3.0（见 ../LICENSE）
-- ---------------------------------------------------------------------
-- 结构口径（真值与合并规则，见 outputs/open-source-review/OS006/review.md）：
--   1. 基线取自生产库 dump 的 CREATE TABLE（发布基线），非开发库；
--   2. 生产与开发列宽不一致时取更宽松定义，逐条登记于 COLUMN_PATCH；
--   3. 金额单位按执行指引 §7.2 方案 B：
--        price / paid_price / purchase_price 保留原定义（与商业版 schema 兼容），
--        社区版不写入、不读取、不展示，保持 NULL；
--        新增 amount_cny / paid_amount_cny 承载「人民币元·线下收款登记」。
--        注（OS012A）：price / purchase_price 原为 NOT NULL 且无默认值，商业版靠代码显式赋值，
--        而社区版不再写这两列、MyBatis-Plus 又会跳过 null 字段，导致「新增课程」「新增购买记录（报读）」
--        在 STRICT_TRANS_TABLES 下报 1364 直接失败。故在本社区版 schema 内给这两列补 DEFAULT 0：
--        类型与可空性不变（仍与商业版兼容），仅让省略该列成为合法写法。
--   4. 防重复点名（§7.3）：bd_class_record 新增 STORED 生成列 active_slot + 唯一键，
--      对 is_delete=0 的记录强制「同班级同时段唯一」；软删除记录为 NULL 不受约束，
--      因此「纠错删除后重录」不会被唯一键阻塞。
--   5. 本文件不含任何 INSERT，可对空库重复执行（IF NOT EXISTS）。
--   6. 课时账户唯一性（OS022）：bd_course_time 新增 STORED 生成列 active_account + 唯一键，
--      对 is_delete=0 的账户强制「同学员同课程只有一份账户」；并发首次报读在 DB 层兜底，
--      与 bd_class_record.active_slot 同一手法。已有库升级见 migrations/001_course_time_account_unique.sql。
-- =====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- ---------------------------------------------------------------------
-- 初始化标记表：记录系统种子版本，供运维判断「已初始化到哪一版」
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `community_seed_marker` (
  `version` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '初始化脚本版本',
  `applied_at` datetime NOT NULL COMMENT '执行时间',
  `note` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`version`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='社区版初始化标记（每个版本只能执行一次）';

-- ==== bd_mechanism ====
CREATE TABLE IF NOT EXISTS `bd_mechanism`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '机构名称',
  `charge_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '机构负责人名称',
  `phone_num` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '常用联系电话',
  `license_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照编号',
  `license` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照图片',
  `permit_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '办学许可证编号',
  `permit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '办学许可证',
  `logo` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'logo，存储的为图片地址',
  `main_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片',
  `refuse_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `denial_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `invite_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邀请码',
  `status` int NULL DEFAULT NULL COMMENT '状态 0待提交 1待审核 2审核通过 3审核未通过',
  `audited_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `create_by` int NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `update_by` int NULL DEFAULT NULL COMMENT '修改人',
  `is_delete` int NULL DEFAULT 0 COMMENT '是否删除 0=未删除 1=删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_mechan_inv`(`invite_code`) USING BTREE,
  KEY `bd_mechan_inv_cre`(`invite_code`, `create_time`) USING BTREE,
  KEY `bd_mechan_inv_sta`(`invite_code`, `status`) USING BTREE,
  KEY `bd_mechan_sta_cre`(`status`, `create_time`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '机构信息' ROW_FORMAT=Dynamic;

-- ==== bd_campus ====
CREATE TABLE IF NOT EXISTS `bd_campus`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分校区名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '所属机构',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '城市',
  `mgr_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '校区负责人名称',
  `mgr_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '校区负责人登录账号',
  `mgr_contact_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '校区负责人联系电话',
  `latitude` decimal(20,15) NULL DEFAULT NULL COMMENT '维度',
  `longitude` decimal(20,15) NULL DEFAULT NULL COMMENT '经度',
  `is_purchase_live` int NULL DEFAULT NULL COMMENT '保留字段（社区版不使用）',
  `live_end_time` datetime NULL DEFAULT NULL COMMENT '保留字段（社区版不使用）',
  `is_member` int NULL DEFAULT NULL COMMENT '是否会员 0=不是会员，1=会员',
  `member_end_time` datetime NULL DEFAULT NULL COMMENT '会员到期时间',
  `is_delete` int NULL DEFAULT NULL COMMENT '是否删除 0=未删除 1=已删除',
  `create_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_campus_cre_mec`(`create_by`, `mec_id`) USING BTREE,
  KEY `bd_campus_is_`(`is_purchase_live`) USING BTREE,
  KEY `bd_campus_mec`(`mec_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '校区表' ROW_FORMAT=Dynamic;

-- ==== bd_campus_connection ====
CREATE TABLE IF NOT EXISTS `bd_campus_connection`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `mec_personnel_id` bigint NULL DEFAULT NULL COMMENT '机构人员id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `campus_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '校区名称',
  `create_by` int NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` int NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_delete` int NULL DEFAULT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_campus_mec_cam`(`mec_personnel_id`, `campus_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '机构人员和校区的关联表 ' ROW_FORMAT=Dynamic;

-- ==== bd_campus_personnel ====
CREATE TABLE IF NOT EXISTS `bd_campus_personnel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `campus_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '校区名称',
  `type` int NULL DEFAULT NULL COMMENT '类型  0校区负责人 1校区人员',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录账号',
  `contact_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话号码',
  `join_time` datetime NULL DEFAULT NULL COMMENT '入职时间',
  `is_delete` int NULL DEFAULT NULL COMMENT '是否删除',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_campus_mec_cam_typ`(`campus_id`, `type`, `mec_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '校区人员表' ROW_FORMAT=Dynamic;

-- ==== bd_mec_personnel ====
CREATE TABLE IF NOT EXISTS `bd_mec_personnel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `contact_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话号码',
  `type` int NULL DEFAULT NULL COMMENT '类型 0机构管理员 1机构人员',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_delete` int NULL DEFAULT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_mec_pe_typ_mec`(`type`, `mec_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '机构人员 ' ROW_FORMAT=Dynamic;

-- ==== bd_teacher ====
CREATE TABLE IF NOT EXISTS `bd_teacher`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '老师名称',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号码 ',
  `sex` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '0:男  1：女',
  `induction_time` datetime NOT NULL COMMENT '入职时间',
  `departure_time` datetime NULL DEFAULT NULL COMMENT '离职时间',
  `taught_num` int NULL DEFAULT NULL COMMENT '已授班级数',
  `is_departure` int NULL DEFAULT 0 COMMENT '0:未离职  1：已离职',
  `version` int NULL DEFAULT NULL COMMENT '版本号',
  `is_delete` int NULL DEFAULT 0 COMMENT '0:未删除1：已删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  `person_type` int NULL DEFAULT NULL COMMENT '0=机构人员 1=校区人员',
  `association_person` bigint NULL DEFAULT NULL COMMENT '关联的人员id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_teacher_is_dep_ind_mec_cam`(`is_departure`, `departure_time`, `induction_time`, `mec_id`, `campus_id`) USING BTREE,
  KEY `bd_teacher_mec_cam_per`(`mec_id`, `campus_id`, `person_type`) USING BTREE,
  KEY `bd_teacher_per_ass`(`person_type`, `association_person`) USING BTREE,
  KEY `bd_teacher_pho_cam_mec`(`phone`, `campus_id`, `mec_id`) USING BTREE,
  KEY `bd_teacher_mec_cam`(`mec_id`, `campus_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '老师表' ROW_FORMAT=Dynamic;

-- ==== bd_course ====
CREATE TABLE IF NOT EXISTS `bd_course`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程封面图片',
  `price` decimal(13,4) NOT NULL DEFAULT 0 COMMENT '课程价格（商业版兼容占位列，社区版不写不读不展示）',
  `paid_price` decimal(13,4) NULL DEFAULT NULL COMMENT '实收价格',
  `amount_cny` decimal(13,2) NULL DEFAULT NULL COMMENT '人民币元·线下收款登记',
  `paid_amount_cny` decimal(13,2) NULL DEFAULT NULL COMMENT '人民币元·线下收款登记',
  `time_num` int NOT NULL COMMENT '课时数量',
  `gift` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '礼品赠送',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程名称',
  `min_cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程小封面',
  `subject` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属科目',
  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课表颜色',
  `is_shelf` int NOT NULL COMMENT '课程状态 0=下架 1=上架',
  `is_experience` int NOT NULL COMMENT '课程类型 0=体验  1=常规',
  `describe` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程简介',
  `introduction` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '课程介绍',
  `is_delete` int NULL DEFAULT 0 COMMENT '是否删除 0=未删除 1=已删除',
  `create_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_course_cam_mec`(`campus_id`, `mec_id`) USING BTREE,
  KEY `bd_course_is__mec_cam_is_`(`is_shelf`, `mec_id`, `campus_id`, `is_experience`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '课程表' ROW_FORMAT=Dynamic;

-- ==== bd_class ====
CREATE TABLE IF NOT EXISTS `bd_class`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `has_class` int NULL DEFAULT NULL COMMENT '已授课时',
  `have_class` int NULL DEFAULT NULL COMMENT '已排课次',
  `bishop_teacher_id` bigint NOT NULL COMMENT '主教老师',
  `help_teacher_id` bigint NULL DEFAULT NULL COMMENT '辅助老师',
  `sum_students` int NOT NULL COMMENT '容量',
  `exist_students` int NULL DEFAULT NULL COMMENT '已有学员',
  `class_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级名称',
  `class_course_id` bigint NOT NULL COMMENT '关联课程',
  `version` int NULL DEFAULT NULL COMMENT '版本号',
  `is_delete` int NULL DEFAULT 0 COMMENT '是否删除 0=未删除 1=删除',
  `class_type` int NOT NULL COMMENT '班级类型 0：体验  1：常规',
  `class_state` int NULL DEFAULT 0 COMMENT '班级状态 0：正常 1：已废弃',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_class_cla`(`class_state`) USING BTREE,
  KEY `bd_class_clas_id`(`class_course_id`) USING BTREE,
  KEY `bd_class_hel_bis_cla_mec_cam_cla_cla`(`mec_id`, `campus_id`, `class_state`, `class_type`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '班级表' ROW_FORMAT=Dynamic;

-- ==== bd_class_time ====
CREATE TABLE IF NOT EXISTS `bd_class_time`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `class_id` bigint NOT NULL COMMENT '班级id',
  `week_day` int NOT NULL COMMENT '周几',
  `cls_start_time` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '上课开始时间',
  `cls_end_time` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '上课结束时间',
  `is_delete` int NOT NULL DEFAULT 0 COMMENT '0:未删除1：已删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_class__cla`(`class_id`) USING BTREE,
  KEY `bd_class__mec_cam_cla`(`mec_id`, `campus_id`, `class_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '班级上课时间表 ' ROW_FORMAT=Dynamic;

-- ==== bd_class_change ====
CREATE TABLE IF NOT EXISTS `bd_class_change`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `class_id` bigint NULL DEFAULT NULL COMMENT '班级id',
  `type` int NULL DEFAULT NULL COMMENT '更改类型0取消1新增2调课',
  `class_time_id` bigint NULL DEFAULT NULL COMMENT '班级上课时间id（调课）',
  `cls_start_time` datetime NULL DEFAULT NULL COMMENT '上课开始时间',
  `cls_end_time` datetime NULL DEFAULT NULL COMMENT '上课结束时间',
  `original_start_time` datetime NULL DEFAULT NULL COMMENT '原来上课开始时间',
  `original_end_time` datetime NULL DEFAULT NULL COMMENT '原来上课结束时间',
  `num` int NULL DEFAULT NULL COMMENT '那一周',
  `is_delete` int NULL DEFAULT 0 COMMENT '是否删除',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `version` int NULL DEFAULT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_class__cla`(`class_time_id`) USING BTREE,
  KEY `bd_class__cla_time_id`(`class_time_id`) USING BTREE,
  KEY `bd_class__cla_typ`(`class_id`, `type`) USING BTREE,
  KEY `bd_class__cla_typ_mec_cam`(`class_id`, `type`, `mec_id`, `campus_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '上课临时更改记录 ' ROW_FORMAT=Dynamic;

-- ==== bd_class_record ====
CREATE TABLE IF NOT EXISTS `bd_class_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `class_id` bigint NOT NULL COMMENT '对应班级',
  `course_id` bigint NULL DEFAULT NULL COMMENT '对应课程',
  `real_number` int NULL DEFAULT NULL COMMENT '实到人数',
  `should_number` int NULL DEFAULT NULL COMMENT '应到人数',
  `class_start_time` datetime NULL DEFAULT NULL COMMENT '上课开始时间',
  `class_end_time` datetime NULL DEFAULT NULL COMMENT '上课结束时间',
  `bishop_teacher_id` bigint NULL DEFAULT NULL COMMENT '主教老师id',
  `bishop_teacher_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主教老师',
  `help_teacher_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '辅助老师',
  `is_delete` int NULL DEFAULT 0 COMMENT '0:未删除1：已删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  `version` int NULL DEFAULT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_class__cla_cla_cla`(`class_id`, `class_start_time`, `class_end_time`) USING BTREE,
  KEY `bd_class__cla_cre`(`class_id`, `create_by`) USING BTREE,
  KEY `bd_class__cou_cla`(`course_id`, `class_id`) USING BTREE,
  KEY `bd_class__cou_cla_mec_cam`(`course_id`, `class_id`, `mec_id`, `campus_id`) USING BTREE,
  KEY `bd_class_cou_cla_bis_hel_mec_cam`(`course_id`, `class_id`, `bishop_teacher_id`, `mec_id`, `campus_id`) USING BTREE,
  `active_slot` varchar(96) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci GENERATED ALWAYS AS (if(`is_delete` = 0, concat(`class_id`, '|', `class_start_time`, '|', `class_end_time`), NULL)) STORED COMMENT '未删除上课记录的唯一时段键（见本文件头部说明）',
  UNIQUE KEY `uk_bd_class_record_active_slot` (`active_slot`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '上课记录表' ROW_FORMAT=Dynamic;

-- ==== bd_call_record ====
CREATE TABLE IF NOT EXISTS `bd_call_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `class_id` bigint NOT NULL COMMENT '班级id',
  `student_id` bigint NOT NULL COMMENT '学生id',
  `class_record_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上课记录id(对应 bd_class_record.id)',
  `class_time_num` int NULL DEFAULT NULL COMMENT '消课课时',
  `type` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '0:到课 1:迟到 2：请假 3:未到4提前请假',
  `is_delete` int NOT NULL COMMENT '是否删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_call_r_cla`(`class_record_id`) USING BTREE,
  KEY `bd_call_r_cla_typ`(`class_record_id`, `type`) USING BTREE,
  KEY `bd_call_r_stu_cla_cla`(`class_record_id`, `student_id`, `type`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '课消记录表' ROW_FORMAT=Dynamic;

-- ==== bd_course_time ====
CREATE TABLE IF NOT EXISTS `bd_course_time`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `student_id` bigint NOT NULL COMMENT '学生id',
  `course_id` bigint NOT NULL COMMENT '课程id',
  `total_number` int NULL DEFAULT NULL COMMENT '总课时',
  `remaining_number` int NULL DEFAULT NULL COMMENT '剩余课时',
  `is_delete` int NULL DEFAULT 0 COMMENT '0:未删除1：已删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  `version` int NULL DEFAULT NULL COMMENT '版本号',
  `active_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci GENERATED ALWAYS AS (if(`is_delete` = 0, concat(`student_id`, '|', `course_id`), NULL)) STORED COMMENT '未删除课时账户的唯一键：学员|课程（OS022，见本文件头部说明）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_bd_course_time_active_account` (`active_account`) USING BTREE,
  KEY `bd_course_cou`(`course_id`) USING BTREE,
  KEY `bd_course_stu_cou_rem`(`student_id`, `course_id`, `remaining_number`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '课时表 ' ROW_FORMAT=Dynamic;

-- ==== bd_course_time_update ====
CREATE TABLE IF NOT EXISTS `bd_course_time_update`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `student_id` bigint NULL DEFAULT NULL COMMENT '学生',
  `course_id` bigint NULL DEFAULT NULL COMMENT '课程id',
  `course_time_id` bigint NULL DEFAULT NULL COMMENT '课程课时id',
  `change_type` int NULL DEFAULT NULL COMMENT '修改类型0新增1删除',
  `num` int NULL DEFAULT NULL COMMENT '数量',
  `reason` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作原因',
  `type` int NULL DEFAULT NULL COMMENT '操作类型1工作人员手动增加课时2课时购买记录3线下报读登记4消课5手动修改总课次6消课记录的到课状态修改为请假7消课记录的到课状态修改为除请假以外的状态',
  `is_delete` int NULL DEFAULT 0 COMMENT '0:未删除1：已删除',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_course_cou_cre_stu_typ`(`course_id`, `create_by`, `student_id`, `type`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '课时修改记录 ' ROW_FORMAT=Dynamic;

-- ==== bd_student ====
CREATE TABLE IF NOT EXISTS `bd_student`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `student_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生姓名',
  `students_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学生手机号码',
  `school_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学校/幼儿园名称',
  `grade` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级',
  `age` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年龄',
  `sex` int NULL DEFAULT NULL COMMENT '性别 0=男 1=女 2=未知',
  `address` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `associated` int NULL DEFAULT NULL COMMENT '关系 0=父亲 1=母亲 2=爷爷 3=奶奶 4=外公 5=外婆 6=其他',
  `parents_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '家长姓名',
  `parents_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '家长手机号码',
  `id_card` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `register_time` datetime NULL DEFAULT NULL COMMENT '报名时间',
  `is_delete` int NULL DEFAULT 0 COMMENT '是否删除 0=未删除 1=删除',
  `is_ordinary` int NULL DEFAULT NULL COMMENT '0：正常学员1：回收站学员',
  `create_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_student_is_stu_cam`(`is_ordinary`, `students_phone`, `campus_id`) USING BTREE,
  KEY `bd_student_is_mec_cam`(`is_ordinary`, `mec_id`, `campus_id`) USING BTREE,
  KEY `bd_student_cam_mec_stu`(`campus_id`, `mec_id`, `students_phone`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '学生表' ROW_FORMAT=Dynamic;

-- ==== bd_student_class ====
CREATE TABLE IF NOT EXISTS `bd_student_class`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `student_id` bigint NOT NULL COMMENT '学生id',
  `class_id` bigint NOT NULL COMMENT '班级id',
  `have_time` datetime NULL DEFAULT NULL COMMENT '添加时间',
  `is_delete` int NULL DEFAULT NULL COMMENT '0：没有删除1：已删除',
  `create_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_studen_cla_mec_cam`(`class_id`, `mec_id`, `campus_id`) USING BTREE,
  KEY `bd_studen_stu_cla_cam_mec`(`student_id`, `class_id`, `campus_id`, `mec_id`) USING BTREE,
  KEY `bd_studen_stu_cam`(`student_id`, `campus_id`) USING BTREE,
  KEY `bd_studen_cla_stu`(`class_id`, `student_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '学生班级中间表' ROW_FORMAT=Dynamic;

-- ==== bd_course_purchase ====
CREATE TABLE IF NOT EXISTS `bd_course_purchase`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `student_id` bigint NULL DEFAULT NULL COMMENT '学生id',
  `course_id` bigint NOT NULL COMMENT '课程id',
  `time_num` int NOT NULL COMMENT '课时数量',
  `purchase_price` decimal(13,4) NOT NULL DEFAULT 0 COMMENT '购买价格（商业版兼容占位列，社区版不写不读不展示）',
  `amount_cny` decimal(13,2) NULL DEFAULT NULL COMMENT '人民币元·线下收款登记',
  `main_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程封面图片',
  `course_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '课程名称',
  `status` int NULL DEFAULT NULL COMMENT '状态 0=无效，1=正常',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` int NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` int NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_delete` int NULL DEFAULT NULL COMMENT '是否删除 0=未删除 1=删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_course_cam_sta_mec`(`campus_id`, `status`, `mec_id`) USING BTREE,
  KEY `bd_course_cou_cam_mec`(`course_id`, `campus_id`, `mec_id`) USING BTREE,
  KEY `bd_course_cou_cre_stu_sta`(`course_id`, `create_by`, `student_id`, `status`) USING BTREE,
  KEY `bd_course_mec_cam_sta`(`mec_id`, `campus_id`, `status`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '课程购买记录 ' ROW_FORMAT=Dynamic;

-- ==== bd_ask_leave ====
CREATE TABLE IF NOT EXISTS `bd_ask_leave`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `student_id` bigint NOT NULL COMMENT '学生id',
  `start_time` datetime NOT NULL COMMENT '请假开始时间',
  `off_time` datetime NOT NULL COMMENT '请假结束时间',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `dispose_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `leave_reason` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请假原因',
  `refuse_reason` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `type` int NULL DEFAULT NULL COMMENT '审核状态 0:未审核  1：已通过  2：未通过',
  `is_delete` int NULL DEFAULT 0 COMMENT '是否删除 0:未删除1：已删除',
  `call_record_id` bigint NULL DEFAULT NULL COMMENT '课消表id',
  `class_record_id` bigint NULL DEFAULT NULL COMMENT '上课记录id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_ask_le_cam_mec`(`campus_id`, `mec_id`) USING BTREE,
  KEY `bd_ask_le_stu_mec_typ_cam`(`student_id`, `mec_id`, `type`, `campus_id`) USING BTREE,
  KEY `bd_ask_start_off_stu`(`start_time`, `off_time`, `student_id`) USING BTREE,
  KEY `bd_ask_le_cam_stu`(`campus_id`, `student_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '学生请假表' ROW_FORMAT=Dynamic;

-- ==== bd_evaluation_teacher ====
CREATE TABLE IF NOT EXISTS `bd_evaluation_teacher`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `type` int NULL DEFAULT NULL COMMENT '类型0匿名1实名',
  `student_id` bigint NULL DEFAULT NULL COMMENT '学生id',
  `teacher_id` bigint NULL DEFAULT NULL COMMENT '老师id',
  `class_record_id` bigint NULL DEFAULT NULL COMMENT '上课记录id',
  `evaluate_score` decimal(32,10) NULL DEFAULT NULL COMMENT '评分',
  `evaluate_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `evaluate_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价时间',
  `is_delete` int NULL DEFAULT 0 COMMENT '0:未删除1：已删除',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `bd_evalua_cla_stu`(`class_record_id`, `student_id`) USING BTREE,
  KEY `bd_evalua_cla_stu_tea`(`class_record_id`, `student_id`, `teacher_id`) USING BTREE,
  KEY `bd_evalua_cla_stu_tea_mec_cam`(`class_record_id`, `student_id`, `teacher_id`, `mec_id`, `campus_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '老师评价表' ROW_FORMAT=Dynamic;

-- ==== sys_config ====
CREATE TABLE IF NOT EXISTS `sys_config`  (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '参数配置表' ROW_FORMAT=Dynamic;

-- ==== sys_dept ====
CREATE TABLE IF NOT EXISTS `sys_dept`  (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `mec_id` int NULL DEFAULT NULL COMMENT '机构id',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '部门表' ROW_FORMAT=Dynamic;

-- ==== sys_dict_data ====
CREATE TABLE IF NOT EXISTS `sys_dict_data`  (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '字典数据表' ROW_FORMAT=Dynamic;

-- ==== sys_dict_type ====
CREATE TABLE IF NOT EXISTS `sys_dict_type`  (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '字典类型表' ROW_FORMAT=Dynamic;

-- ==== sys_job ====
CREATE TABLE IF NOT EXISTS `sys_job`  (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '定时任务调度表' ROW_FORMAT=Dynamic;

-- ==== sys_job_log ====
CREATE TABLE IF NOT EXISTS `sys_job_log`  (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '日志信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '异常信息',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '定时任务调度日志表' ROW_FORMAT=Dynamic;

-- ==== sys_logininfor ====
CREATE TABLE IF NOT EXISTS `sys_logininfor`  (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '系统访问记录' ROW_FORMAT=Dynamic;

-- ==== sys_menu ====
CREATE TABLE IF NOT EXISTS `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `mec_type` int NULL DEFAULT NULL COMMENT '类型 0 机构菜单 1校区菜单 2平台菜单',
  `menu_perm_type` int NULL DEFAULT NULL COMMENT '达到某个权限解锁的类型（社区版不使用权限解锁，仅保留字段兼容）',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE,
  KEY `sys_menu_per_men_men_sta`(`perms`, `menu_id`, `menu_type`, `status`) USING BTREE,
  KEY `sys_menu_men_sta`(`menu_type`, `status`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '菜单权限表' ROW_FORMAT=Dynamic;

-- ==== sys_notice ====
CREATE TABLE IF NOT EXISTS `sys_notice`  (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `mec_id` int NULL DEFAULT NULL COMMENT '机构id',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '通知公告表' ROW_FORMAT=Dynamic;

-- ==== sys_oper_log ====
CREATE TABLE IF NOT EXISTS `sys_oper_log`  (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '返回参数',
  `status` int NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`oper_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '操作日志记录' ROW_FORMAT=Dynamic;

-- ==== sys_post ====
CREATE TABLE IF NOT EXISTS `sys_post`  (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `mec_id` int NULL DEFAULT NULL COMMENT '机构id',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '岗位信息表' ROW_FORMAT=Dynamic;

-- ==== sys_role ====
CREATE TABLE IF NOT EXISTS `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_type` int NULL DEFAULT NULL COMMENT '类型 0校区角色 1平台角色',
  `mec_id` bigint NULL DEFAULT NULL COMMENT '机构id',
  `campus_id` bigint NULL DEFAULT NULL COMMENT '校区id',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE,
  KEY `sys_role_del`(`del_flag`) USING BTREE,
  KEY `sys_role_rol`(`role_id`) USING BTREE,
  KEY `sys_role_rol_cre_del`(`role_id`, `create_by`, `del_flag`) USING BTREE,
  KEY `sys_role_del_rol`(`del_flag`, `role_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '角色信息表' ROW_FORMAT=Dynamic;

-- ==== sys_role_dept ====
CREATE TABLE IF NOT EXISTS `sys_role_dept`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '角色和部门关联表' ROW_FORMAT=Dynamic;

-- ==== sys_role_menu ====
CREATE TABLE IF NOT EXISTS `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '角色和菜单关联表' ROW_FORMAT=Dynamic;

-- ==== sys_user ====
CREATE TABLE IF NOT EXISTS `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `type` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型 0机构人员 1校区人员 2平台人员',
  `association_id` bigint NULL DEFAULT NULL COMMENT '当类型为0时关联的是机构ID，1是校区ID ，2是平台登录人员id',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登陆IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登陆时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE KEY `sys_user_user_name`(`user_name`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '用户信息表' ROW_FORMAT=Dynamic;

-- ==== sys_user_post ====
CREATE TABLE IF NOT EXISTS `sys_user_post`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '用户与岗位关联表' ROW_FORMAT=Dynamic;

-- ==== sys_user_role ====
CREATE TABLE IF NOT EXISTS `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `user_type` int NULL DEFAULT NULL COMMENT '用户类型 0是校区人员id  1是机构和校区的关联表id 2平台人员',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '用户和角色关联表' ROW_FORMAT=Dynamic;

-- ==== sequence ====
CREATE TABLE IF NOT EXISTS `sequence`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `current_value` bigint NULL DEFAULT NULL,
  `gmt_modified` datetime NULL DEFAULT NULL,
  `gmt_create` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ROW_FORMAT=Compact;

-- ==== QRTZ_BLOB_TRIGGERS ====
CREATE TABLE IF NOT EXISTS `QRTZ_BLOB_TRIGGERS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `blob_data` blob NULL,
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ROW_FORMAT=Dynamic;

-- ==== QRTZ_CALENDARS ====
CREATE TABLE IF NOT EXISTS `QRTZ_CALENDARS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `calendar` blob NOT NULL,
  PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ROW_FORMAT=Dynamic;

-- ==== QRTZ_CRON_TRIGGERS ====
CREATE TABLE IF NOT EXISTS `QRTZ_CRON_TRIGGERS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `cron_expression` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `time_zone_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ROW_FORMAT=Dynamic;

-- ==== QRTZ_FIRED_TRIGGERS ====
CREATE TABLE IF NOT EXISTS `QRTZ_FIRED_TRIGGERS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `entry_id` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `fired_time` bigint NOT NULL,
  `sched_time` bigint NOT NULL,
  `priority` int NOT NULL,
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`sched_name`, `entry_id`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ROW_FORMAT=Dynamic;

-- ==== QRTZ_JOB_DETAILS ====
CREATE TABLE IF NOT EXISTS `QRTZ_JOB_DETAILS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `job_class_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `is_durable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `is_update_data` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `job_data` blob NULL,
  PRIMARY KEY (`sched_name`, `job_name`, `job_group`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ROW_FORMAT=Dynamic;

-- ==== QRTZ_LOCKS ====
CREATE TABLE IF NOT EXISTS `QRTZ_LOCKS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `lock_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`sched_name`, `lock_name`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ROW_FORMAT=Dynamic;

-- ==== QRTZ_PAUSED_TRIGGER_GRPS ====
CREATE TABLE IF NOT EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`sched_name`, `trigger_group`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ROW_FORMAT=Dynamic;

-- ==== QRTZ_SCHEDULER_STATE ====
CREATE TABLE IF NOT EXISTS `QRTZ_SCHEDULER_STATE`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `last_checkin_time` bigint NOT NULL,
  `checkin_interval` bigint NOT NULL,
  PRIMARY KEY (`sched_name`, `instance_name`) USING BTREE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ROW_FORMAT=Dynamic;

-- ==== QRTZ_SIMPLE_TRIGGERS ====
CREATE TABLE IF NOT EXISTS `QRTZ_SIMPLE_TRIGGERS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `repeat_count` bigint NOT NULL,
  `repeat_interval` bigint NOT NULL,
  `times_triggered` bigint NOT NULL,
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ROW_FORMAT=Dynamic;

-- ==== QRTZ_SIMPROP_TRIGGERS ====
CREATE TABLE IF NOT EXISTS `QRTZ_SIMPROP_TRIGGERS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `str_prop_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `str_prop_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `str_prop_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `int_prop_1` int NULL DEFAULT NULL,
  `int_prop_2` int NULL DEFAULT NULL,
  `long_prop_1` bigint NULL DEFAULT NULL,
  `long_prop_2` bigint NULL DEFAULT NULL,
  `dec_prop_1` decimal(13,4) NULL DEFAULT NULL,
  `dec_prop_2` decimal(13,4) NULL DEFAULT NULL,
  `bool_prop_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `bool_prop_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `QRTZ_TRIGGERS` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ROW_FORMAT=Dynamic;

-- ==== QRTZ_TRIGGERS ====
CREATE TABLE IF NOT EXISTS `QRTZ_TRIGGERS`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `next_fire_time` bigint NULL DEFAULT NULL,
  `prev_fire_time` bigint NULL DEFAULT NULL,
  `priority` int NULL DEFAULT NULL,
  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `start_time` bigint NOT NULL,
  `end_time` bigint NULL DEFAULT NULL,
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `misfire_instr` smallint NULL DEFAULT NULL,
  `job_data` blob NULL,
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  KEY `sched_name`(`sched_name`, `job_name`, `job_group`) USING BTREE,
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `QRTZ_JOB_DETAILS` (`sched_name`, `job_name`, `job_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci ROW_FORMAT=Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
