------------更新语句
Use `oauth-center`;
alter table oauth_client_details add token_format varchar(20) not null DEFAULT 'reference' COMMENT 'token格式: reference 引用令牌(不透明), self-contained 自包含令牌(jwt))';
alter table oauth_client_details drop column support_id_token;
alter table oauth_client_details drop column id_token_validity;

UPDATE oauth_client_details set scope = 'app,openid,profile' where client_id = 'webApp';
UPDATE oauth_client_details set scope = 'all,openid,profile' where client_id = 'zlt';