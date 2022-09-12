------------更新语句
Use `oauth-center`;
update oauth_client_details set additional_information = '{"LOGOUT_NOTIFY_URL_LIST":"http://127.0.0.1:8082/logoutNotify"}'
where client_id = 'webApp';

update oauth_client_details set additional_information = '{"LOGOUT_NOTIFY_URL_LIST":"http://127.0.0.1:8081/logoutNotify"}'
where client_id = 'app';