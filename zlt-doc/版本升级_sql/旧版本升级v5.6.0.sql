------------更新语句
Use `user-center`;
alter table sys_role add data_scope varchar(32) DEFAULT 'ALL' comment '数据权限范围配置：ALL/全部权限，CREATOR/创建者权限';

alter table sys_user add `creator_id` int(11) COMMENT '创建人id';
update sys_user set creator_id = 1;

alter table sys_role add `creator_id` int(11) COMMENT '创建人id';
update sys_role set creator_id = 1;

alter table sys_menu add `creator_id` int(11) COMMENT '创建人id';
update sys_menu set creator_id = 1;

Use `oauth-center`;
alter table oauth_client_details add `creator_id` int(11) COMMENT '创建人id';
update oauth_client_details set creator_id = 1;