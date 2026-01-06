-- 人脸识别人脸识别系统数据库脚本
-- 创建时间：2026-01-06

-- 创建数据库
CREATE DATABASE IF NOT EXISTS face_recognition CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE face_recognition;

-- 1. 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
  `role_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(30) NOT NULL COMMENT '角色名称',
  `role_key` VARCHAR(100) NOT NULL COMMENT '角色权限',
  `role_sort` INT NOT NULL COMMENT '角色排序',
  `status` VARCHAR(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 2. 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` BIGINT DEFAULT NULL COMMENT '部门ID',
  `user_name` VARCHAR(30) NOT NULL COMMENT '用户账号',
  `nick_name` VARCHAR(30) NOT NULL COMMENT '用户昵称',
  `user_type` VARCHAR(2) NOT NULL DEFAULT '01' COMMENT '用户类型（00系统用户 01普通用户 02企业用户）',
  `email` VARCHAR(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` VARCHAR(11) DEFAULT '' COMMENT '手机号码',
  `sex` CHAR(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatarPath` VARCHAR(100) DEFAULT '' COMMENT '用户头像路径',
  `password` VARCHAR(100) DEFAULT '' COMMENT '密码',
  `status` VARCHAR(1) NOT NULL DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` VARCHAR(128) DEFAULT '' COMMENT '最后登录IP',
  `login_location` VARCHAR(255) DEFAULT '' COMMENT '最后登录地址',
  `login_date` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` DATETIME DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_name` (`user_name`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 3. 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

-- 4. 权限表
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `permission_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` VARCHAR(30) NOT NULL COMMENT '权限名称',
  `permission_key` VARCHAR(100) NOT NULL COMMENT '权限标识',
  `permission_type` CHAR(1) NOT NULL COMMENT '权限类型（1菜单 2按钮）',
  `status` VARCHAR(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `uk_permission_key` (`permission_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 5. 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`, `permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`),
  CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`permission_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和权限关联表';

-- 6. 考勤记录表
CREATE TABLE IF NOT EXISTS `attendance_record` (
  `record_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `user_name` VARCHAR(30) NOT NULL COMMENT '用户名',
  `real_name` VARCHAR(30) NOT NULL COMMENT '用户真实姓名',
  `punch_time` DATETIME NOT NULL COMMENT '打卡时间',
  `punch_type` TINYINT NOT NULL COMMENT '打卡类型（1：上班，2：下班）',
  `status` TINYINT NOT NULL COMMENT '打卡状态（1：成功，0：失败）',
  `recognition_log_id` BIGINT DEFAULT NULL COMMENT '识别日志ID',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_punch_time` (`punch_time`),
  KEY `idx_recognition_log_id` (`recognition_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';

-- 7. 人脸识别日志表
CREATE TABLE IF NOT EXISTS `face_recognition_log` (
  `log_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `recognition_time` DATETIME NOT NULL COMMENT '识别时间',
  `status` VARCHAR(1) NOT NULL COMMENT '识别状态（0成功 1失败）',
  `recognized_name` VARCHAR(30) DEFAULT NULL COMMENT '识别到的姓名',
  `photo_path` VARCHAR(255) NOT NULL COMMENT '照片路径',
  `confidence` DOUBLE DEFAULT NULL COMMENT '置信度',
  `face_count` INT DEFAULT 0 COMMENT '人脸数量',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`log_id`),
  KEY `idx_recognition_time` (`recognition_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人脸识别日志表';

-- 初始化数据
-- 初始化角色
INSERT INTO `sys_role` (`role_name`, `role_key`, `role_sort`, `status`) VALUES 
('管理员', 'admin', 1, '0'),
('普通用户', 'user', 2, '0');

-- 初始化用户（密码：123456，加密后的密码）
INSERT INTO `sys_user` (`user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `password`, `status`, `create_time`) VALUES 
('admin', '管理员', '00', 'admin@example.com', '13800138000', '0', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', NOW()),
('user', '普通用户', '01', 'user@example.com', '13800138001', '0', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', NOW());

-- 初始化用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES 
(1, 1),
(2, 2);

-- 初始化权限
INSERT INTO `sys_permission` (`permission_name`, `permission_key`, `permission_type`, `status`) VALUES 
('系统管理', 'sys:manage', '1', '0'),
('用户管理', 'sys:user:manage', '1', '0'),
('角色管理', 'sys:role:manage', '1', '0'),
('权限管理', 'sys:permission:manage', '1', '0'),
('考勤管理', 'attendance:manage', '1', '0'),
('人脸识别管理', 'face:manage', '1', '0');

-- 初始化角色权限关联
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES 
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(2, 5),
(2, 6);
