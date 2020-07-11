------------更新语句
Use `oauth-center`;
update oauth_client_details set authorized_grant_types = 'authorization_code,password,refresh_token,client_credentials,implicit,password_code,openId,mobile_password' where client_id = 'webApp'