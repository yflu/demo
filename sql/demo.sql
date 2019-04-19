/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2019-04-19 08:38:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `log_name` varchar(255) DEFAULT NULL COMMENT '日志名称',
  `succeed` varchar(255) DEFAULT NULL COMMENT '是否执行成功',
  `message` text COMMENT '具体消息',
  `ip` varchar(255) DEFAULT NULL COMMENT '登录ip',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '登录人ID',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1115189682896019649 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='登录记录';

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `pid` bigint(20) NOT NULL COMMENT '父节点ID',
  `name` varchar(50) NOT NULL COMMENT '菜单名称',
  `code` varchar(100) NOT NULL COMMENT '菜单编号',
  `menu_flag` char(1) NOT NULL COMMENT '菜单标记:Y是 N否',
  `url` varchar(200) DEFAULT NULL COMMENT '链接',
  `level` int(1) NOT NULL COMMENT '层级',
  `sort` int(2) DEFAULT NULL COMMENT '排序',
  `del_flag` char(1) DEFAULT NULL COMMENT '删除标记：Y是 N否',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `version` int(11) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`menu_id`),
  KEY `idx_pid` (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '0', '系统设置', 'system:index', 'Y', null, '1', null, '1', '2017-01-23 10:09:38', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('2', '1', '用户管理', 'system:userManage', 'Y', null, '2', null, '1', '2017-01-23 10:36:15', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('3', '1', '权限管理', 'system:permissionManage', 'Y', null, '2', null, '1', '2017-01-23 10:37:03', '2017-01-23 10:38:38', null, null, null);
INSERT INTO `sys_menu` VALUES ('4', '1', '角色管理', 'system:roleManage', 'Y', null, '2', null, '1', '2017-01-23 10:38:03', '2017-01-23 10:38:27', null, null, null);
INSERT INTO `sys_menu` VALUES ('8', '0', '数据管理', 'data:index', 'Y', null, '1', null, '1', '2019-03-27 15:06:20', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('9', '8', '组织管理', 'organization:list', 'Y', null, '2', null, '1', '2019-03-27 15:25:00', '2019-03-27 15:36:51', null, null, null);
INSERT INTO `sys_menu` VALUES ('10', '8', '用户管理', 'user:list', 'Y', null, '2', null, '1', '2019-03-27 15:30:34', null, null, null, null);
INSERT INTO `sys_menu` VALUES ('12', '1', '日志管理', 'system:log', 'Y', null, '2', null, '1', '2019-03-27 16:58:39', null, null, null, null);

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `log_type` varchar(32) DEFAULT NULL COMMENT '日志类型',
  `log_name` varchar(255) DEFAULT NULL COMMENT '日志名称',
  `uri` varchar(200) DEFAULT NULL COMMENT '请求路径',
  `request_type` char(10) DEFAULT NULL COMMENT '请求类型',
  `class_name` varchar(255) DEFAULT NULL COMMENT '类名称',
  `method` text COMMENT '方法名称',
  `succeed` varchar(32) DEFAULT NULL COMMENT '是否成功',
  `params` text COMMENT '备注',
  `result` text COMMENT '返回值',
  `contrast` text COMMENT '对比',
  `ip` varchar(100) DEFAULT NULL COMMENT 'ip',
  `cost_time` bigint(20) DEFAULT NULL COMMENT '耗时',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1094865976764338319 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='操作日志';

-- ----------------------------
-- Records of sys_operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(32) NOT NULL COMMENT '角色名称',
  `del_flag` char(1) NOT NULL COMMENT '删除标记：Y是 N否',
  `remark` varchar(32) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `version` int(11) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', 'admin', '1', '超级管理员', '2016-09-13 14:42:34', null, null, null, null);
INSERT INTO `sys_role` VALUES ('2', '123123', '0', '123123', '2019-03-27 09:33:49', null, null, null, null);
INSERT INTO `sys_role` VALUES ('3', '12312312', '0', '123', '2019-03-27 09:52:27', null, null, null, null);
INSERT INTO `sys_role` VALUES ('4', '测试', '1', '123', '2019-03-27 16:29:18', null, null, null, null);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `relation_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`relation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COMMENT='角色-菜单关系表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('1', '1', '0');
INSERT INTO `sys_role_menu` VALUES ('2', '1', '1');
INSERT INTO `sys_role_menu` VALUES ('3', '1', '2');
INSERT INTO `sys_role_menu` VALUES ('4', '1', '3');
INSERT INTO `sys_role_menu` VALUES ('5', '1', '4');
INSERT INTO `sys_role_menu` VALUES ('6', '1', '8');
INSERT INTO `sys_role_menu` VALUES ('7', '1', '9');
INSERT INTO `sys_role_menu` VALUES ('8', '1', '10');
INSERT INTO `sys_role_menu` VALUES ('9', '1', '12');
INSERT INTO `sys_role_menu` VALUES ('10', '4', '0');
INSERT INTO `sys_role_menu` VALUES ('11', '4', '8');
INSERT INTO `sys_role_menu` VALUES ('12', '4', '9');
INSERT INTO `sys_role_menu` VALUES ('13', '4', '10');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `account` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(50) NOT NULL COMMENT '密码',
  `salt` varchar(10) DEFAULT NULL COMMENT '密码盐',
  `nickname` varchar(40) DEFAULT NULL COMMENT '昵称',
  `name` varchar(20) DEFAULT NULL COMMENT '用户姓名',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色id',
  `status` int(1) DEFAULT NULL COMMENT '状态：1正常 0禁用',
  `del_flag` char(1) DEFAULT NULL COMMENT '删除标记：Y是 N否',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `version` int(11) DEFAULT NULL COMMENT '乐观锁',
  PRIMARY KEY (`user_id`),
  KEY `idx_account_delflag` (`account`,`del_flag`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('3', null, '123', '7c4a8d09ca3762af61e59520943dc26494f8941b', '8qmbk', 'kimi', 'kimi', '3', '1', 'N', '2018-07-24 16:21:37', '2018-07-24 16:21:37', null, null, null);
INSERT INTO `sys_user` VALUES ('4', null, '222', '7c4a8d09ca3762af61e59520943dc26494f8941b', '8qmbk', 'kitty', 'kitty', '3', '1', 'N', '2018-07-24 16:21:53', '2018-07-24 16:21:53', null, null, null);
INSERT INTO `sys_user` VALUES ('7', null, '233', '7c4a8d09ca3762af61e59520943dc26494f8941b', '8qmbk', 'chloe', 'chloe', '4', '1', 'N', '2018-07-24 16:22:33', '2018-07-24 16:22:33', null, null, null);
INSERT INTO `sys_user` VALUES ('9', null, '555', '7c4a8d09ca3762af61e59520943dc26494f8941b', '8qmbk', 'anna1', 'anna', '1', '1', 'N', '2019-01-04 14:04:45', '2019-03-27 10:05:15', null, null, null);
INSERT INTO `sys_user` VALUES ('11', null, '66', '7c4a8d09ca3762af61e59520943dc26494f8941b', '8qmbk', '111', '1111', '1', '1', 'N', '2019-03-27 13:26:21', '2019-03-27 13:26:21', null, null, null);
INSERT INTO `sys_user` VALUES ('55', null, '777阿萨德', '7c4a8d09ca3762af61e59520943dc26494f8941b', '8qmbk', 'admin', '超级管理员', '1', '1', 'N', '2017-01-22 10:52:18', '2019-03-27 10:17:36', null, null, null);
INSERT INTO `sys_user` VALUES ('56', null, '1555306336118', '1495557bc7698ec12190907e4fc8caa7', 'szn8w', '1555306336118', null, null, '1', 'N', '2019-04-12 02:39:59', '2019-04-15 05:32:16', null, '57', null);