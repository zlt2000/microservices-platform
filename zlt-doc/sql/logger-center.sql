CREATE DATABASE IF NOT EXISTS `logger-center` DEFAULT CHARACTER SET = utf8;
Use `logger-center`;

-- ----------------------------
-- Table structure for t_logger
-- ----------------------------
DROP TABLE IF EXISTS `sys_logger`;
CREATE TABLE `sys_logger`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `application_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '应用名',
  `class_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类名',
  `method_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '方法名',
  `user_id` int(11) NULL COMMENT '用户id',
  `user_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '用户名',
  `client_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '租户id',
  `operation` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作信息',
  `timestamp` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;