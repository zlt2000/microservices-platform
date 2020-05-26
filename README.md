#  zlt-microservices-platform

<p align="center">
  <img src='https://img.shields.io/badge/license-Apache%202-4EB1BA.svg' alt='License'/>
  <img src="https://img.shields.io/badge/Spring%20Boot-2.1.12.RELEASE-blue" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Spring%20Cloud-Greenwich.SR5-blue" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2.1.2.RELEASE-blue" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Elasticsearch-6.x-brightgreen" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Layui-EasyWeb-yellowgreen" alt="Downloads"/>
</p>

## 如果您觉得有帮助，请点右上角 "Star" 支持一下谢谢
&nbsp;
## 1. 总体架构图
![mark](https://gitee.com/zlt2000/images/raw/master/springcloud%E5%BE%AE%E6%9C%8D%E5%8A%A1%E6%9E%B6%E6%9E%84%E5%9B%BE.jpg)

&nbsp;
## 2. 功能介绍
![mark](https://gitee.com/zlt2000/images/raw/master/ZLT-MP%E5%BE%AE%E6%9C%8D%E5%8A%A1%E5%B9%B3%E5%8F%B0%E5%8A%9F%E8%83%BD%E5%9B%BE.jpg)

&nbsp;
## 3. 项目介绍
* **技术交流群** 
<table>
    <tr>
        <td><center><strong><a href="http://shang.qq.com/wpa/qunwpa?idkey=17544199255998bda0d938fb72b08d076c40c52c9904520b76eb5eb0585da71e" target="_blank">交流一群(已满)</a></strong></center></td>
        <td><center><strong><a href="https://shang.qq.com/wpa/qunwpa?idkey=41988facbc02f678942a7ee7ae03122f2ef0a10c948b3d07319f070bfb0d3a98" target="_blank">交流二群</a></strong></center></td>
	</tr>
    <tr>
        <td><a href="http://shang.qq.com/wpa/qunwpa?idkey=17544199255998bda0d938fb72b08d076c40c52c9904520b76eb5eb0585da71e" target="_blank"><img width=300px height=300px alt="交流一群(已满)" src="https://gitee.com/zlt2000/images/raw/master/%E4%BA%A4%E6%B5%81%E4%B8%80%E7%BE%A4.png"/></a></td>
        <td><a href="https://shang.qq.com/wpa/qunwpa?idkey=41988facbc02f678942a7ee7ae03122f2ef0a10c948b3d07319f070bfb0d3a98" target="_blank"><img width=300px height=300px alt="交流二群" src="https://gitee.com/zlt2000/images/raw/master/%E4%BA%A4%E6%B5%81%E4%BA%8C%E7%BE%A4.png"/></a></td>
    </tr>
</table>


* **详细在线文档** ：https://www.kancloud.cn/zlt2000/microservices-platform/919418
  * **[项目更新日志](https://www.kancloud.cn/zlt2000/microservices-platform/936235)**
  * **[文档更新日志](https://www.kancloud.cn/zlt2000/microservices-platform/936236)**
* **演示环境地址**： [http://zlt2000.cn](http://zlt2000.cn/)
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
>重构于开源项目OCP&cp：https://gitee.com/owenwangwen/open-capacity-platform

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
│  │  ├─log-center -- 日志中心[7200]
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
│  │  ├─sso-demo -- 单点登录demo
```

<table>
    <tr>
        <td><a target="_blank" href="https://www.aliyun.com/minisite/goods?userCode=dickv1kw&share_source=copy_link"><img width="460px" height="177px" alt="阿里云" src="https://gitee.com/zlt2000/images/raw/master/aly.jpg"/></a></td>
        <td><a target="_blank" href="https://url.cn/55zzbhR"><img width="460px" height="177px"  alt="腾讯云" src="https://gitee.com/zlt2000/images/raw/master/txy.jpg"/></a></td>
    </tr>
</table>

## 5. 交流反馈
* 有问题先看看 [F&Q](https://www.kancloud.cn/zlt2000/microservices-platform/981382) 中有没有相关的回答
* 欢迎提交`ISSUS`，请写清楚问题的具体原因，重现步骤和环境(上下文)
* 项目/微服务交流请进群：
  * 一群：[250883130(已满)](https://shang.qq.com/wpa/qunwpa?idkey=17544199255998bda0d938fb72b08d076c40c52c9904520b76eb5eb0585da71e)
  * 二群：[1041797659](https://shang.qq.com/wpa/qunwpa?idkey=41988facbc02f678942a7ee7ae03122f2ef0a10c948b3d07319f070bfb0d3a98)
* 个人博客：[https://zlt2000.gitee.io](https://zlt2000.gitee.io)
* 个人邮箱：zltdiablo@163.com
* 个人公众号：[陶陶技术笔记](http://qiniu.zlt2000.cn/blog/20190902/M56cWjw7uNsc.png?imageslim)
* GitChat：[https://gitbook.cn/gitchat/author/5b2362320398d50d7b7ab29e](https://gitbook.cn/gitchat/author/5b2362320398d50d7b7ab29e)

&nbsp;
## 6. 截图（点击可大图预览）
<table>
    <tr>
        <td><img alt="首页" src="https://gitee.com/zlt2000/images/raw/master/%E9%A6%96%E9%A1%B5.png"/></td>
        <td><img alt="用户搜索" src="https://gitee.com/zlt2000/images/raw/master/%E7%94%A8%E6%88%B7%E6%90%9C%E7%B4%A2.png"/></td>
    </tr>
	<tr>
        <td><img alt="server_metrics" src="https://gitee.com/zlt2000/images/raw/master/server_metrics.png"/></td>
        <td><img alt="application_metrics" src="https://gitee.com/zlt2000/images/raw/master/application_metrics.png"/></td>
    </tr>
	<tr>
        <td><img alt="持续集成2" src="https://gitee.com/zlt2000/images/raw/master/%E6%8C%81%E7%BB%AD%E9%9B%86%E6%88%902.png"/></td>
        <td><img alt="sonar结果" src="https://gitee.com/zlt2000/images/raw/master/sonar%E7%BB%93%E6%9E%9C.png"/></td>
    </tr>
    <tr>
        <td><img alt="skywalking首页.png" src="https://gitee.com/zlt2000/images/raw/master/skywalking%E9%A6%96%E9%A1%B5.png"/></td>
        <td><img alt="skywalking应用拓扑图" src="https://gitee.com/zlt2000/images/raw/master/skywalking%E5%BA%94%E7%94%A8%E6%8B%93%E6%89%91%E5%9B%BE.png"/></td>
    </tr>
    <tr>
        <td><img alt="elk" src="https://gitee.com/zlt2000/images/raw/master/elk.png"/></td>
        <td><img alt="任务中心" src="https://gitee.com/zlt2000/images/raw/master/%E4%BB%BB%E5%8A%A1%E4%B8%AD%E5%BF%83.png"/></td>
    </tr>
    <tr>
        <td><img alt="日志中心02" src="https://gitee.com/zlt2000/images/raw/master/%E6%97%A5%E5%BF%97%E4%B8%AD%E5%BF%8302.png"/></td>
        <td><img alt="慢查询sql" src="https://gitee.com/zlt2000/images/raw/master/%E6%85%A2%E6%9F%A5%E8%AF%A2sql.png"/></td>
    </tr>
    <tr>
        <td><img alt="nacos-discovery" src="https://gitee.com/zlt2000/images/raw/master/nacos-discovery.png"/></td>
        <td><img alt="应用吞吐量监控" src="https://gitee.com/zlt2000/images/raw/master/%E5%BA%94%E7%94%A8%E5%90%9E%E5%90%90%E9%87%8F%E7%9B%91%E6%8E%A7.png"/></td>
    </tr>
</table>