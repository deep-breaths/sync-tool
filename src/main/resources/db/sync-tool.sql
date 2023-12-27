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

 Date: 27/12/2023 16:13:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for column_structures
-- ----------------------------
DROP TABLE IF EXISTS `column_structures`;
CREATE TABLE `column_structures`  (
  `id` int NOT NULL,
  `version_id` int NULL DEFAULT NULL COMMENT '版本id',
  `table_id` int NULL DEFAULT NULL COMMENT '表id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段名',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字段类型',
  `size` double NULL DEFAULT NULL COMMENT '长度',
  `is_nullable` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否为null',
  `default` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '表字段信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for database_connections
-- ----------------------------
DROP TABLE IF EXISTS `database_connections`;
CREATE TABLE `database_connections`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '连接id',
  `connection_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '连接名称，用于标识连接',
  `db_type_id` int NOT NULL COMMENT '数据库类型，如 MySQL、PostgreSQL 等',
  `host` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主机名或IP地址',
  `port` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库端口',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库用户名',
  `password` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库密码',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `db_type_id`(`db_type_id` ASC) USING BTREE,
  CONSTRAINT `database_connections_ibfk_1` FOREIGN KEY (`db_type_id`) REFERENCES `db_type_dict` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库连接信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for database_structures
-- ----------------------------
DROP TABLE IF EXISTS `database_structures`;
CREATE TABLE `database_structures`  (
  `id` int NOT NULL,
  `version_id` int NULL DEFAULT NULL COMMENT '版本id',
  `schema_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库名',
  `structure` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '建库语句',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for database_versions
-- ----------------------------
DROP TABLE IF EXISTS `database_versions`;
CREATE TABLE `database_versions`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '版本ID',
  `connection_id` int NOT NULL COMMENT '外键，DatabaseConnections',
  `version_number` int NOT NULL COMMENT '版本号',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本描述',
  `applied_on` datetime NOT NULL COMMENT '应用的时间戳',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库版本' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for db_type_dict
-- ----------------------------
DROP TABLE IF EXISTS `db_type_dict`;
CREATE TABLE `db_type_dict`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '数据库类型，如mysql',
  `driver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '驱动名称，如com.mysql.cj.jdbc.Driver',
  `prefix` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '前缀，jdbc:mysql://',
  `suffix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '后缀，?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai',
  `test` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '测试连接的sql语句',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `type`(`type` ASC, `driver_name` ASC, `prefix` ASC, `suffix` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据库类型字典' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for foreign_keys
-- ----------------------------
DROP TABLE IF EXISTS `foreign_keys`;
CREATE TABLE `foreign_keys`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `version_id` int NULL DEFAULT NULL COMMENT '版本号',
  `table_id` int NULL DEFAULT NULL COMMENT '表id',
  `fk_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '外键名称',
  `fk_table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '外键表名',
  `fk_column_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '外键列名',
  `pk_table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联的主键表名',
  `pk_column_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联的主键列名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '存储外键信息的表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for indexes
-- ----------------------------
DROP TABLE IF EXISTS `indexes`;
CREATE TABLE `indexes`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `version_id` int NULL DEFAULT NULL COMMENT '版本号',
  `table_id` int NULL DEFAULT NULL COMMENT '表id',
  `table_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名',
  `index_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '索引名称',
  `composite_columns` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '复合列',
  `non_unique` tinyint NULL DEFAULT NULL COMMENT '是否唯一',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '存储索引信息的表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for table_structures
-- ----------------------------
DROP TABLE IF EXISTS `table_structures`;
CREATE TABLE `table_structures`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `version_id` int NULL DEFAULT NULL COMMENT '版本id',
  `database_id` int NULL DEFAULT NULL COMMENT '数据库id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名',
  `structure` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '建表语句',
  `indexes_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '索引',
  `foreign_keys_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '外键',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表类型，table、view',
  `primary_keys` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主键',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
