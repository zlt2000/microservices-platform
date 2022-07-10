## 一、说明
资源服务器 demo 样例，以最简化的代码演示如何快速集成一个带鉴权功能的服务，适用于 `无网络隔离` 架构。 

> 关于无网络隔离架构的设计可参考文档：[无网络隔离架构](https://www.kancloud.cn/zlt2000/microservices-platform/1153640)

&nbsp;
## 二、启动以下服务
1. zlt-uaa：统一认证中心
2. user-center：用户服务
3. sc-gateway：api网关
4. resources-server-demo

> 环境配置与启动参考文档：https://www.kancloud.cn/zlt2000/microservices-platform/919418

&nbsp;
## 三、测试
### 3.1. 测试接口一
http://localhost:8093/test/notAuth
> 无需token访问

&nbsp;
### 3.2. 测试接口二
通过 @LoginUser 获取当前登录人

http://localhost:8093/test/auth?access_token=xxx
> - xxx 需替换为正确的 access_token
> - 可以通过修改 `bootstrap.yml` 文件中的 `zlt.security.ignore.httpUrls` 参数添加排除校验的url。

&nbsp;
### 3.3. 测试接口三
通过 LoginUserContextHolder 获取当前登录人

http://localhost:8093/test/auth2?access_token=xxx
> - xxx 需替换为正确的 access_token

&nbsp;
## 四、获取access_token
可使用任意授权模式获取；

例如：密码模式授权
- 请求方式：POST
- 请求头：Authorization:Basic d2ViQXBwOndlYkFwcA==
- 请求地址：http://localhost:9900/api-uaa/oauth/token?grant_type=password&username=admin&password=admin

> 授权接口清单参考文档：https://www.kancloud.cn/zlt2000/microservices-platform/1158135
