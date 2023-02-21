## 代码说明
- [WebSocket接口鉴权](https://www.kancloud.cn/zlt2000/microservices-platform/2757595)

&nbsp;
## 启动以下服务

1. zlt-uaa：统一认证中心
2. user-center：用户服务
3. sc-gateway：api网关
4. websocket-demo

> 环境配置与启动参考文档：https://www.kancloud.cn/zlt2000/microservices-platform/919418

&nbsp;
## 获取access_token
可使用任意授权模式获取；

例如：密码模式授权
- 请求方式：POST
- 请求头：Authorization:Basic d2ViQXBwOndlYkFwcA==
- 请求地址：http://localhost:9900/api-uaa/oauth/token?grant_type=password&username=admin&password=admin

> 授权接口清单参考文档：https://www.kancloud.cn/zlt2000/microservices-platform/1158135

&nbsp;
## 测试步骤
使用 `Postman` 进行测试（最新版本支持 webSocket 接口）

点击 `New` 按钮，选择 `WebSocket Request` ；

在 `URL` 中输入 `ws://localhost:8092/websocket/test` 

在 `Headers` 中添加：`Authorization:Bearer xxx`

或者

在 `参数` 中添加：`ws://localhost:8092/websocket/test?access_token=xxx`

> xxx 需替换为正确的 access_token