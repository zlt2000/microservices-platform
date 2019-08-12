------------更新语句
Use `user-center`;
alter table sys_role add `tenant_id` varchar(32) DEFAULT '' COMMENT '租户字段';
update sys_role set tenant_id = 'webApp'
alter table sys_menu add `tenant_id` varchar(32) DEFAULT '' COMMENT '租户字段';
update sys_menu set tenant_id = 'webApp'


Use `file-center`;
alter table file_info add `tenant_id` varchar(32) DEFAULT '' COMMENT '租户字段';
update file_info set tenant_id = 'webApp'


Use `oauth-center`;
alter table oauth_client_details add `client_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '应用名称'
update oauth_client_details set client_name = 'pc端' where client_id = 'webApp'
update oauth_client_details set client_name = '移动端' where client_id = 'app'
update oauth_client_details set client_name = '第三方应用' where client_id = 'zlt'