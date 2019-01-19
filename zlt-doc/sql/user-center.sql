CREATE DATABASE IF NOT EXISTS `user-center` DEFAULT CHARACTER SET = utf8;
Use `user-center`;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `head_img_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `mobile` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sex` tinyint(1) NULL DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT 1,
  `type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `company` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `open_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `is_del` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `username`(`username`) USING BTREE,
  INDEX `mobile`(`mobile`) USING BTREE,
  INDEX `open_id`(`open_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$Wtw81uu43fGKw9lkOr1RAOTNWxQIZBsB3YDwc/5yDnr/yeG5x92EG', '管理员', 'http://payo7kq4i.bkt.clouddn.com/QQ%E5%9B%BE%E7%89%8720180819191900.jpg', '13106975707', 0, 1, 'BACKEND', '2017-11-17 16:56:59', '2018-11-15 23:19:49', 'ENGJ', NULL, 0);
INSERT INTO `sys_user` VALUES (2, 'owen', '$2a$10$rgnDR/ozUKej0rYa4/GV/.Bx6U8ogmwpR8bzO12fIsj7AH2KaJKTS', '欧文', 'http://payo7kq4i.bkt.clouddn.com/QQ%E5%9B%BE%E7%89%8720180819191900.jpg', '18579068166', 1, 0, 'APP', '2017-11-17 16:56:59', '2018-11-15 02:03:34', 'ENGJ', NULL, 0);
INSERT INTO `sys_user` VALUES (3, 'user', '$2a$10$OhfZv4VQJiqMEukpf1qXA.V7UMiHjr86g6lJqPvKUoHwrPk35steG', '体验用户', 'http://payo7kq4i.bkt.clouddn.com/QQ%E5%9B%BE%E7%89%8720180819191900.jpg', '18888888888', 1, 1, 'APP', '2017-11-17 16:56:59', NULL, 'ENGJ', NULL, 0);
INSERT INTO `sys_user` VALUES (4, 'test', '$2a$10$RD18sHNphJMmcuLuUX/Np.IV/7Ngbjd3Jtj3maFLpwaA6KaHVqPtq', '测试账户', 'http://payo7kq4i.bkt.clouddn.com/QQ%E5%9B%BE%E7%89%8720180819191900.jpg', '13851539156', 0, 0, 'APP', '2017-11-17 16:56:59', '2018-09-07 03:27:40', 'ENGJ', NULL, 0);
INSERT INTO `sys_user` VALUES (7, 'useruser', '$2a$10$IcsVUMlsZtOljOSrCKhx5.JA5vj/x8XiN6XJDwj4al5NKFU/KwdeO', 'useruser', NULL, '18571111111', 1, 0, 'APP', '2018-09-03 09:57:12', '2018-09-05 12:57:14', 'ENGJ', NULL, 0);
INSERT INTO `sys_user` VALUES (8, 'abc', '$2a$10$RII9blAhenwoFLjL1Y7kNOgq8xqUR/.o6SCDmfPbb6IAnZng/HsKa', 'abc', NULL, '13322332123', 0, 0, 'APP', '2018-09-03 03:32:52', '2018-09-11 13:55:24', 'YCC', NULL, 0);
INSERT INTO `sys_user` VALUES (9, 'jay', '$2a$10$og3NMep2E4sJF90IzoyVre53A37APaNvbXXTJDhcjQkDuTHIe.wvO', 'jay', NULL, '15151515151', 0, 0, 'APP', '2018-09-06 02:30:51', '2018-09-18 06:22:08', 'YCC', NULL, 0);
INSERT INTO `sys_user` VALUES (10, 'testpre', '$2a$10$5fLnmUaeCiBnHMTpGwLRo.2kfVCu/rUVCZ/CNat4y55Zo.VXu.56O', 'testpre', NULL, '17791907897', 0, 1, 'APP', '2018-09-07 02:48:44', '2018-09-18 06:21:51', 'YCC', NULL, 0);
INSERT INTO `sys_user` VALUES (11, '1', '$2a$10$9vLdwXBZaAPy/hmzEDf.M.YbrsKWGG21nqWq17/EwWPBi65GDivLa', '11', NULL, '13530151800', 1, 1, 'APP', '2018-09-07 14:20:51', '2018-11-15 01:45:36', 'YCC', NULL, 0);
INSERT INTO `sys_user` VALUES (12, '12', '$2a$10$cgRGZ0uuIAoKuwBoTWmz7eJzP4RUEr688VlnpZ4BTCz2RZEt0jrIe', '12', NULL, '17587132062', 0, 1, 'APP', '2018-09-08 04:52:25', '2018-09-16 01:48:00', 'YCC', NULL, 0);
INSERT INTO `sys_user` VALUES (13, 'abc1', '$2a$10$pzvn4TfBh2oFZJbtagovFe56ZTUlTaawPnx0Yz2PeqGex0xbddAGu', 'abc', NULL, '12345678901', 0, 1, 'APP', '2018-09-11 08:02:25', '2018-09-14 06:49:54', 'YCC', NULL, 0);
INSERT INTO `sys_user` VALUES (14, 'ceshis', '$2a$10$bS/QnPkDUDYCAtCxDAF9DuGJV12kvQ1XRHM/wAmqAsnSQxSXd4vsW', 'ceshis', NULL, '12345643455', 0, 1, 'APP', '2018-09-12 13:50:57', '2018-12-24 00:06:34', 'YCC', NULL, 0);
INSERT INTO `sys_user` VALUES (15, 'w2121', '$2a$10$cXP.wRKERY8eCyhAglrYM.0.Z8FNMXcR1lY.ysgCZ0ZQVSwypkKnS', '1223', NULL, '15854556993', 0, 1, 'APP', '2018-09-13 09:35:15', '2018-12-17 17:45:46', 'JFSC', NULL, 1);
INSERT INTO `sys_user` VALUES (16, 'zhangsan', '$2a$10$hl.oqAsPK6H/gIKoYq.PXu358uAohcYVQ7TyzeGyXznZPOHjkO5lK', 'zhangsan', NULL, '15958278762', 0, 1, 'APP', '2018-09-17 10:18:04', '2018-09-18 15:14:00', 'JFSC', NULL, 1);
INSERT INTO `sys_user` VALUES (17, '234', '$2a$10$FxFvGGSi2RCe4lm5V.G0Feq6szh5ArMz.8Mzm08zQlkA.VgE9GFbm', 'ddd', NULL, '13245678906', 0, 1, 'APP', '2018-09-19 01:33:54', '2018-09-19 01:33:54', 'JFSC', NULL, 1);
INSERT INTO `sys_user` VALUES (18, 'tester', '$2a$10$VUfknatgKIoZJYDLIesrrO5Vg8Djw5ON2oDWeXyC24TZ6Ca/TWiye', 'tester', NULL, '12345678901', 0, 1, 'APP', '2018-09-19 04:52:01', '2018-11-16 22:12:04', 'JFSC', NULL, 1);
INSERT INTO `sys_user` VALUES (19, '11111111111111111111', '$2a$10$DNaUDpCHKZI0V9w.R3wBaeD/gGOQDYjgC5fhju7bQLfIkqsZV61pi', 'cute', 'http://payo7kq4i.bkt.clouddn.com/C:\\Users\\GAOY91\\Pictures\\79f0f736afc37931a921fd59e3c4b74543a91170.jpg', '15599999991', 1, 1, 'APP', '2018-09-19 04:57:39', NULL, 'JFSC', NULL, 1);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色code',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'ADMIN', '管理员', '2017-11-17 16:56:59', '2018-09-19 09:39:10');
INSERT INTO `sys_role` VALUES (3, 'test', '测试', '2018-09-17 10:15:51', '2018-11-15 01:49:14');
INSERT INTO `sys_role` VALUES (4, '11', '11', '2018-11-15 01:49:19', '2018-11-15 01:49:19');
INSERT INTO `sys_role` VALUES (5, '123', '1235', '2018-11-15 23:33:39', '2018-12-24 23:53:33');

-- ----------------------------
-- Table structure for sys_role_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_user`;
CREATE TABLE `sys_role_user`  (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_user
-- ----------------------------
INSERT INTO `sys_role_user` VALUES (1, 1);
INSERT INTO `sys_role_user` VALUES (2, 1);
INSERT INTO `sys_role_user` VALUES (3, 1);
INSERT INTO `sys_role_user` VALUES (4, 1);
INSERT INTO `sys_role_user` VALUES (7, 1);
INSERT INTO `sys_role_user` VALUES (8, 1);
INSERT INTO `sys_role_user` VALUES (9, 1);
INSERT INTO `sys_role_user` VALUES (10, 1);
INSERT INTO `sys_role_user` VALUES (11, 1);
INSERT INTO `sys_role_user` VALUES (13, 1);
INSERT INTO `sys_role_user` VALUES (14, 1);
INSERT INTO `sys_role_user` VALUES (15, 1);
INSERT INTO `sys_role_user` VALUES (16, 3);
INSERT INTO `sys_role_user` VALUES (17, 1);
INSERT INTO `sys_role_user` VALUES (18, 3);
INSERT INTO `sys_role_user` VALUES (19, 3);
INSERT INTO `sys_role_user` VALUES (20, 1);
INSERT INTO `sys_role_user` VALUES (25, 1);
INSERT INTO `sys_role_user` VALUES (26, 1);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) NOT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `path_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `css` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sort` int(11) NOT NULL,
  `create_time` datetime(0) NULL,
  `update_time` datetime(0) NULL,
  `type` tinyint(1) NOT NULL,
  `hidden` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (2, 12, '用户管理', '#!user', 'system/user.html', NULL, 'layui-icon-friends', 2, '2017-11-17 16:56:59', '2018-09-19 11:26:14', 1, 0);
INSERT INTO `sys_menu` VALUES (3, 12, '角色管理', '#!role', 'system/role.html', NULL, 'layui-icon-friends', 3, '2017-11-17 16:56:59', '2018-09-02 06:12:04', 1, 0);
INSERT INTO `sys_menu` VALUES (4, 12, '菜单管理', '#!menus', 'system/menus.html', NULL, 'layui-icon-menu-fill', 4, '2017-11-17 16:56:59', '2018-09-03 02:23:47', 1, 0);
INSERT INTO `sys_menu` VALUES (7, 37, '注册中心', '#!register', 'http://192.168.28.130:1111/', NULL, 'layui-icon-engine', 7, '2017-11-17 16:56:59', '2018-12-21 10:34:49', 1, 0);
INSERT INTO `sys_menu` VALUES (8, 37, '监控中心', '#!monitor', 'http://127.0.0.1:9999/#/wallboard', NULL, 'layui-icon-util', 8, '2017-11-17 16:56:59', '2018-08-25 10:43:24', 1, 0);
INSERT INTO `sys_menu` VALUES (9, 37, '文件中心', '#!files', 'files/files.html', NULL, 'layui-icon-file', 10, '2017-11-17 16:56:59', '2018-08-25 10:43:33', 1, 0);
INSERT INTO `sys_menu` VALUES (10, 37, '文档中心', '#!swagger', 'http://127.0.0.1:9200/swagger-ui.html', NULL, 'layui-icon-app', 9, '2017-11-17 16:56:59', '2018-08-30 06:22:02', 1, 0);
INSERT INTO `sys_menu` VALUES (11, 12, '我的信息', '#!myInfo', 'system/myInfo.html', NULL, '', 10, '2017-11-17 16:56:59', '2018-09-02 06:12:24', 1, 1);
INSERT INTO `sys_menu` VALUES (12, -1, '认证管理', 'javascript:;', '', NULL, 'layui-icon-set', 1, '2017-11-17 16:56:59', '2018-12-13 15:02:49', 1, 0);
INSERT INTO `sys_menu` VALUES (35, 12, '应用管理', '#!app', 'attestation/app.html', NULL, 'layui-icon-app', 9, '2017-11-17 16:56:59', '2018-08-25 10:57:42', 1, 0);
INSERT INTO `sys_menu` VALUES (37, -1, '系统管理', 'javascript:;', '', NULL, 'layui-icon-set', 3, '2018-08-25 10:41:58', '2018-12-13 15:03:09', 1, 0);
INSERT INTO `sys_menu` VALUES (38, 37, 'Zipkin监控', '#!zipkin', 'http://127.0.0.1:11008/zipkin/', NULL, 'layui-icon-util', 10, '2018-08-26 17:10:38', '2018-09-03 09:54:11', 1, 0);
INSERT INTO `sys_menu` VALUES (44, 37, '服务治理', '#!eureka', 'eureka/list.html', NULL, 'layui-icon-engine', 6, '2018-08-30 15:30:19', '2018-08-30 15:30:36', 1, 0);
INSERT INTO `sys_menu` VALUES (48, 47, 'ccc', 'ccc', 'ccc', NULL, 'ccc', 1, '2018-09-05 02:17:22', '2018-09-05 02:17:22', 1, 0);
INSERT INTO `sys_menu` VALUES (49, 48, 'ddd', 'ddd', 'ddd', NULL, '', 55, '2018-09-05 03:27:18', '2018-09-05 03:27:18', 1, 0);
INSERT INTO `sys_menu` VALUES (58, 56, 'test333', '', '', NULL, '', 1, '2018-11-17 02:44:39', '2018-11-17 02:44:39', 2, 0);
INSERT INTO `sys_menu` VALUES (59, 2, '用户查看', NULL, '/api-user/users', 'get', NULL, 1, '2018-12-15 08:11:33', '2018-12-15 08:11:33', 2, 0);
INSERT INTO `sys_menu` VALUES (60, 3, '角色查看', NULL, '/api-user/roles', 'get', NULL, 1, '2018-12-15 10:44:54', '2018-12-15 10:44:54', 2, 0);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` int(11) NOT NULL,
  `menu_id` int(11) NOT NULL,
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 2);
INSERT INTO `sys_role_menu` VALUES (1, 3);
INSERT INTO `sys_role_menu` VALUES (1, 4);
INSERT INTO `sys_role_menu` VALUES (1, 7);
INSERT INTO `sys_role_menu` VALUES (1, 8);
INSERT INTO `sys_role_menu` VALUES (1, 9);
INSERT INTO `sys_role_menu` VALUES (1, 10);
INSERT INTO `sys_role_menu` VALUES (1, 11);
INSERT INTO `sys_role_menu` VALUES (1, 12);
INSERT INTO `sys_role_menu` VALUES (1, 35);
INSERT INTO `sys_role_menu` VALUES (1, 37);
INSERT INTO `sys_role_menu` VALUES (1, 38);
INSERT INTO `sys_role_menu` VALUES (1, 44);
INSERT INTO `sys_role_menu` VALUES (1, 48);
INSERT INTO `sys_role_menu` VALUES (1, 49);
INSERT INTO `sys_role_menu` VALUES (1, 59);
INSERT INTO `sys_role_menu` VALUES (1, 60);
INSERT INTO `sys_role_menu` VALUES (1, 61);
INSERT INTO `sys_role_menu` VALUES (3, 2);
INSERT INTO `sys_role_menu` VALUES (3, 3);
INSERT INTO `sys_role_menu` VALUES (3, 4);
INSERT INTO `sys_role_menu` VALUES (3, 5);
INSERT INTO `sys_role_menu` VALUES (3, 11);
INSERT INTO `sys_role_menu` VALUES (3, 12);
INSERT INTO `sys_role_menu` VALUES (3, 35);
INSERT INTO `sys_role_menu` VALUES (3, 36);
INSERT INTO `sys_role_menu` VALUES (3, 50);
INSERT INTO `sys_role_menu` VALUES (3, 52);
INSERT INTO `sys_role_menu` VALUES (4, 2);
INSERT INTO `sys_role_menu` VALUES (4, 3);
INSERT INTO `sys_role_menu` VALUES (4, 4);
INSERT INTO `sys_role_menu` VALUES (4, 12);
