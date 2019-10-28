#  zlt-microservices-platform

<p align="center">
  <img src='https://img.shields.io/badge/license-Apache%202-4EB1BA.svg' alt='License'/>
  <img src="https://img.shields.io/badge/Spring%20Boot-2.1.8.RELEASE-blue" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Spring%20Cloud-Greenwich.SR3-blue" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2.1.0.RELEASE-blue" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Layui-EasyWeb-yellowgreen" alt="Downloads"/>
</p>



## 如果您觉得有帮助，请点右上角 "Star" 支持一下谢谢

[TOC]

## 1. 项目介绍

* **技术交流群** [250883130](http://shang.qq.com/wpa/qunwpa?idkey=17544199255998bda0d938fb72b08d076c40c52c9904520b76eb5eb0585da71e)
* **详细在线文档** [点击查看](https://www.kancloud.cn/zlt2000/microservices-platform/936236)
  * **[项目更新日志](https://www.kancloud.cn/zlt2000/microservices-platform/936235)**
  * **[文档更新日志](https://www.kancloud.cn/zlt2000/microservices-platform/936236)**
* **演示环境地址**： [http://mp.zlt2000.cn](http://mp.zlt2000.cn/)
  * 账号密码：admin/admin
  * APM监控账号密码：admin/admin
  * Grafana账号：zlt/zlt123
  * txlcn事务管理器密码：admin
  * 任务管理账号密码：admin/123456
  * Sentinel：sentinel/sentinel
* **演示环境有全方位的监控示例：日志系统 + APM系统 + GPE系统**
* Gitee地址：https://gitee.com/zlt2000/microservices-platform
* Github地址：https://github.com/zlt2000/microservices-platform
* 前后端分离的企业级微服务架构
* 主要针对解决微服务和业务开发时常见的**非功能性需求**
* 深度定制`Spring Security`真正实现了基于`RBAC`、`jwt`和`oauth2`的无状态统一权限认证的解决方案
* 提供应用管理，方便第三方系统接入，**支持多租户(应用隔离)**
* 引入组件化的思想实现高内聚低耦合并且高度可配置化
* 注重代码规范，严格控制包依赖，每个工程基本都是最小依赖
* 非常适合学习和企业中使用

&nbsp;

## 2. 项目总体架构图
![mark](http://qiniu.zlt2000.cn/blog/20191021/IyNU3skYNIMf.jpg?imageslim)

&nbsp;

## 3. 功能介绍
![mark](http://qiniu.zlt2000.cn/blog/20191026/JMENnTzOtvr3.jpg?imageslim)

&nbsp;

## 4. 模块说明

```lua
central-platform -- 父项目，公共依赖
│  ├─zlt-business -- 业务模块一级工程
│  │  ├─user-center -- 用户中心[7000]
│  │  ├─file-center -- 文件中心[5000]
│  │  ├─code-generator -- 代码生成器[7300]
│  │  ├─search-center -- 搜索中心
│  │  │  ├─search-client -- 搜索中心客户端
│  │  │  ├─search-server -- 搜索中心服务端[7100]
│  │─zlt-commons -- 通用工具一级工程
│  │  ├─zlt-auth-client-spring-boot-starter -- 封装spring security client端的通用操作逻辑
│  │  ├─zlt-common-core -- 封装通用操作逻辑
│  │  ├─zlt-common-spring-boot-starter -- 封装通用操作逻辑
│  │  ├─zlt-db-spring-boot-starter -- 封装数据库通用操作逻辑
│  │  ├─zlt-log-spring-boot-starter -- 封装log通用操作逻辑
│  │  ├─zlt-redis-spring-boot-starter -- 封装Redis通用操作逻辑
│  │  ├─zlt-ribbon-spring-boot-starter -- 封装Ribbon和Feign的通用操作逻辑
│  │  ├─zlt-sentinel-spring-boot-starter -- 封装Sentinel的通用操作逻辑
│  │  ├─zlt-swagger2-spring-boot-starter -- 封装Swagger通用操作逻辑
│  ├─zlt-config -- 配置中心
│  ├─zlt-doc -- 项目文档
│  ├─zlt-gateway -- api网关一级工程
│  │  ├─sc-gateway -- spring-cloud-gateway[9900]
│  │  ├─zuul-gateway -- netflix-zuul[9900]
│  ├─zlt-job -- 分布式任务调度一级工程
│  │  ├─job-admin -- 任务管理器[8081]
│  │  ├─job-core -- 任务调度核心代码
│  │  ├─job-executor-samples -- 任务执行者executor样例[8082]
│  ├─zlt-monitor -- 监控一级工程
│  │  ├─sc-admin -- 应用监控[6500]
│  │  ├─log-center -- 日志中心[6200]
│  ├─zlt-uaa -- spring-security认证中心[8000]
│  ├─zlt-register -- 注册中心Nacos[8848]
│  ├─zlt-web -- 前端一级工程
│  │  ├─back-web -- 后台前端[8066]
│  ├─zlt-transaction -- 事务一级工程
│  │  ├─txlcn-tm -- tx-lcn事务管理器[7970]
│  ├─zlt-demo -- demo一级工程
│  │  ├─txlcn-demo -- txlcn分布式事务demo
│  │  ├─seata-demo -- seata分布式事务demo
│  │  ├─sharding-jdbc-demo -- sharding-jdbc分库分表demo
│  │  ├─rocketmq-demo -- rocketmq和mq事务demo
```

&nbsp;
## 5. 交流反馈
* 有问题先看看 [F&Q](https://www.kancloud.cn/zlt2000/microservices-platform/981382) 中有没有相关的回答

* 欢迎提交`ISSUS`，请写清楚问题的具体原因，重现步骤和环境(上下文)

* 项目/微服务交流请进群：[250883130](http://shang.qq.com/wpa/qunwpa?idkey=17544199255998bda0d938fb72b08d076c40c52c9904520b76eb5eb0585da71e)

* 个人博客：https://blog.csdn.net/zlt2000

* 个人邮箱：zltdiablo@163.com

* 个人公众号：[陶陶技术笔记](http://qiniu.zlt2000.cn/blog/20190902/M56cWjw7uNsc.png?imageslim)


&nbsp;
## 6. 截图（点击可大图预览）
<table>
    <tr>
        <td><img alt="首页" src="http://qiniu.zlt2000.cn/blog/20191021/9wd5JIuXMtFW.png?imageslim"/></td>
        <td><img alt="用户搜索" src="http://qiniu.zlt2000.cn/blog/20191021/UaHM4w4kPVT9.png?imageslim"/></td>
    </tr>
	<tr>
        <td><img alt="server_metrics" src="http://qiniu.zlt2000.cn/blog/20191021/9KsjcCgYqgBB.png?imageslim"/></td>
        <td><img alt="application_metrics" src="http://qiniu.zlt2000.cn/blog/20191021/4VH2z1BW3sWN.png?imageslim"/></td>
    </tr>
	<tr>
        <td><img alt="持续集成2" src="http://qiniu.zlt2000.cn/blog/20191021/8Ew3z2kDe8bj.png?imageslim"/></td>
        <td><img alt="sonar结果" src="http://qiniu.zlt2000.cn/blog/20191021/AEEPnTLBz4P4.png?imageslim"/></td>
    </tr>
    <tr>
        <td><img alt="skywalking首页.png" src="http://qiniu.zlt2000.cn/blog/20191021/GhDqEUdDDfhV.png?imageslim"/></td>
        <td><img alt="skywalking应用拓扑图" src="http://qiniu.zlt2000.cn/blog/20191021/xLi5mvhzWfWt.png?imageslim"/></td>
    </tr>
    <tr>
        <td><img alt="elk" src="http://qiniu.zlt2000.cn/blog/20191021/5lrHQb2xNF4o.png?imageslim"/></td>
        <td><img alt="任务中心" src="http://qiniu.zlt2000.cn/blog/20191021/A0YLCT1BHCed.png?imageslim"/></td>
    </tr>
    <tr>
        <td><img alt="日志中心02" src="http://qiniu.zlt2000.cn/blog/20191021/f7bdpM1To8wT.png?imageslim"/></td>
        <td><img alt="慢查询sql" src="http://qiniu.zlt2000.cn/blog/20191021/Yqr4Dnb9BwSy.png?imageslim"/></td>
    </tr>
    <tr>
        <td><img alt="nacos-discovery" src="http://qiniu.zlt2000.cn/blog/20191021/spAnWiDcwch5.png?imageslim"/></td>
        <td><img alt="应用吞吐量监控" src="http://qiniu.zlt2000.cn/blog/20191021/vBhLkLma3dwC.png?imageslim"/></td>
    </tr>
</table>