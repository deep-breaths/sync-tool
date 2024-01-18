/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.30.144
 Source Server Type    : MySQL
 Source Server Version : 80032
 Source Host           : 192.168.30.144:3306
 Source Schema         : sync-tool

 Target Server Type    : MySQL
 Target Server Version : 80032
 File Encoding         : 65001

 Date: 18/01/2024 15:33:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sync_col
-- ----------------------------
DROP TABLE IF EXISTS `sync_col`;
CREATE TABLE `sync_col`  (
  `id` bigint NOT NULL,
  `version_id` bigint NULL DEFAULT NULL COMMENT '版本id',
  `table_id` bigint NULL DEFAULT NULL COMMENT '表id',
  `db_id` bigint NULL DEFAULT NULL COMMENT '数据库id',
  `db_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库名称',
  `table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名称',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段名',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段类型',
  `size` bigint NULL DEFAULT NULL COMMENT '长度',
  `is_nullable` tinyint(1) NULL DEFAULT NULL COMMENT '是否为null',
  `default_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认值',
  `last_column` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上一个字段名称',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '表字段信息(来源于建表语句的拆解)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sync_conn
-- ----------------------------
DROP TABLE IF EXISTS `sync_conn`;
CREATE TABLE `sync_conn`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '连接id',
  `conn_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '连接名称，用于标识连接',
  `db_type_id` bigint NOT NULL COMMENT '数据库类型，如 MySQL、PostgreSQL 等',
  `host` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主机名或IP地址',
  `port` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库端口',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库用户名',
  `password` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库密码',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `db_type_id`(`db_type_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库连接信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sync_data
-- ----------------------------
DROP TABLE IF EXISTS `sync_data`;
CREATE TABLE `sync_data`  (
  `id` bigint NOT NULL,
  `version_id` bigint NULL DEFAULT NULL COMMENT '版本id',
  `table_id` bigint NULL DEFAULT NULL COMMENT '表id',
  `db_id` bigint NULL DEFAULT NULL COMMENT '数据库id',
  `db_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库名称',
  `table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名称',
  `row_num` bigint NULL DEFAULT NULL COMMENT '行号',
  `col_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '列名',
  `col_value` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '列值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sync_db
-- ----------------------------
DROP TABLE IF EXISTS `sync_db`;
CREATE TABLE `sync_db`  (
  `id` bigint NOT NULL,
  `version_id` bigint NULL DEFAULT NULL COMMENT '版本id',
  `schema_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库名',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sync_db_type
-- ----------------------------
DROP TABLE IF EXISTS `sync_db_type`;
CREATE TABLE `sync_db_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库类型，如mysql',
  `driver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '驱动名称，如com.mysql.cj.jdbc.Driver',
  `prefix` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '前缀，jdbc:mysql://',
  `suffix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '后缀，?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai',
  `structure` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '建库语句',
  `test` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试连接的sql语句',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `type`(`type` ASC, `driver_name` ASC, `prefix` ASC, `suffix` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库类型字典' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sync_fk
-- ----------------------------
DROP TABLE IF EXISTS `sync_fk`;
CREATE TABLE `sync_fk`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `version_id` bigint NULL DEFAULT NULL COMMENT '版本号',
  `db_id` bigint NULL DEFAULT NULL COMMENT '数据库id',
  `table_id` bigint NULL DEFAULT NULL COMMENT '表id',
  `db_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库名称',
  `table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名称',
  `fk_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '外键名称',
  `fk_table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '外键表名',
  `fk_col_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '外键列名',
  `pk_table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联的主键表名',
  `pk_col_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联的主键列名',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '存储外键信息的表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sync_index
-- ----------------------------
DROP TABLE IF EXISTS `sync_index`;
CREATE TABLE `sync_index`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `version_id` bigint NULL DEFAULT NULL COMMENT '版本号',
  `db_id` bigint NULL DEFAULT NULL COMMENT '数据库id',
  `table_id` bigint NULL DEFAULT NULL COMMENT '表id',
  `db_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库名',
  `table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名',
  `index_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '索引名称',
  `composite_col` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '复合列',
  `non_unique` tinyint NULL DEFAULT NULL COMMENT '是否不是唯一',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '索引类型',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '存储索引信息的表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sync_table
-- ----------------------------
DROP TABLE IF EXISTS `sync_table`;
CREATE TABLE `sync_table`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `version_id` bigint NULL DEFAULT NULL COMMENT '版本id',
  `db_id` bigint NULL DEFAULT NULL COMMENT '数据库id',
  `db_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库名',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表类型，table、view',
  `table_statement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '建表语句',
  `table_options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '编码等 ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `version_id`(`version_id` ASC) USING BTREE,
  INDEX `sync_table_ibfk_2`(`db_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sync_version
-- ----------------------------
DROP TABLE IF EXISTS `sync_version`;
CREATE TABLE `sync_version`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '版本ID',
  `conn_id` bigint NOT NULL COMMENT '外键，DatabaseConnections',
  `version_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '版本号',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sync_conn_vserion`(`conn_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库版本' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
