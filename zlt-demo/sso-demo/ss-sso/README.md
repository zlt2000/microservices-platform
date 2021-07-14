## **详细的原理和注意事项请查看**
[单点登录详解](https://www.kancloud.cn/zlt2000/microservices-platform/2278849)



## 启动以下服务

1. zlt-uaa：统一认证中心
2. user-center：用户服务
3. sc-gateway：api网关
4. ss-sso：单点登录demo(zlt应用)
5. web-sso：单点登录demo2(app应用)



## 测试步骤

1. 登录app应用：
    通过地址 http://127.0.0.1:8081/index.html 先登录app应用
2. 访问zlt应用(单点成功)：
   在浏览器打开一个新的页签(共享session)，通过地址 http://127.0.0.1:8080 访问zlt应用，单点登录成功显示当前登录用户名、权限、应用id信息