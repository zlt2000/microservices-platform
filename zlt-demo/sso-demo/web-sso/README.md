## **详细的原理和注意事项请查看**
[单点登录详解](https://www.kancloud.cn/zlt2000/microservices-platform/1515193)



## oauth-center数据库执行以下sql
```sql
update oauth_client_details set 
    authorized_grant_types = 'authorization_code,password,refresh_token', 
    web_server_redirect_uri = 'http://127.0.0.1:8081/callback.html'
where client_id = 'app'
```



## 启动以下服务

1. zlt-uaa：统一认证中心
2. user-center：用户服务
3. sc-gateway：api网关
4. back-web：webApp应用前端页面
5. web-sso：单点登录demo(app应用)



## 测试步骤

1. 登录webApp应用：
    通过地址 http://127.0.0.1:8066 先登录webApp应用
2. 访问app应用(单点成功)：
   在浏览器打开一个新的页签(共享session)，通过地址 http://127.0.0.1:8081/index.html 访问app应用静态页面，单点登录成功显示当前登录用户名、应用id、token信息