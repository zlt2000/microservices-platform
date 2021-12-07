## **详细的原理和注意事项请查看**
- [OIDC协议单点登录](https://www.kancloud.cn/zlt2000/microservices-platform/2278851)

- [单点登出详解](https://www.kancloud.cn/zlt2000/microservices-platform/2539642)



## oauth-center数据库执行以下sql
```sql
alter table oauth_client_details add support_id_token tinyint(1) DEFAULT 1 COMMENT '是否支持id_token';
alter table oauth_client_details add id_token_validity int(11) DEFAULT 60 COMMENT 'id_token有效期';

update oauth_client_details set additional_information = '{"LOGOUT_NOTIFY_URL_LIST":"http://127.0.0.1:8082/logoutNotify"}'
where client_id = 'webApp';
```

## 启动以下服务

1. zlt-uaa：统一认证中心
2. user-center：用户服务
3. sc-gateway：api网关
4. oidc-sso：单点登录demo(webApp应用)
5. web-sso：单点登录demo(app应用)



## 测试步骤

1. 登录app应用：
    通过地址 http://127.0.0.1:8081 先登录app应用
2. 访问app应用(单点成功)：
   在浏览器打开一个新的页签(共享session)，通过地址 http://127.0.0.1:8082/ 访问webApp应用，单点登录成功显示当前登录用户名、应用id、token信息
3. 再次切换回app应用 http://127.0.0.1:8081 页面点击登出按钮，登出app应用
4. 回来webApp应用 http://127.0.0.1:8082 刷新页面，返回登录页面（单点登出成功）