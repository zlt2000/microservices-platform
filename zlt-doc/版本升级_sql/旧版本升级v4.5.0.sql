------------更新语句
Use `oauth-center`;
alter table oauth_client_details add support_id_token tinyint(1) DEFAULT 1 COMMENT '是否支持id_token';
alter table oauth_client_details add id_token_validity int(11) DEFAULT 60 COMMENT 'id_token有效期';

Use `user-center`;
alter table `sys_user` MODIFY COLUMN `password` varchar(100) NOT NULL COMMENT '登录密码';
