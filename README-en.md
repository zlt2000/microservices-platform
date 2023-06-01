#  zlt-microservices-platform

<p align="center">
  <img src="https://img.shields.io/badge/Version-5.5.0-critical" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-2.5.14-blue" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Spring%20Cloud-2020.0.6-blue" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2021.1-blue" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Elasticsearch-7.x-brightgreen" alt="Downloads"/>
  <img src="https://img.shields.io/badge/Ant%20Design-pro-9cf" alt="Downloads"/>
  <a target="_blank" href='https://gitee.com/zlt2000/microservices-platform'>
    <img src='https://gitee.com/zlt2000/microservices-platform/badge/star.svg' alt='star'/>
  </a>
  <a target="_blank" href='https://github.com/zlt2000/microservices-platform'>
    <img src="https://img.shields.io/github/stars/zlt2000/microservices-platform.svg?style=social" alt="github star"/>
  </a>
</p>





## If you find it helpful, please click "Star" in the upper right corner to support it. Thank you
&nbsp;
## 1. Overall Architecture Diagram
![mark](https://gitee.com/zlt2000/images/raw/master/springcloud%E5%BE%AE%E6%9C%8D%E5%8A%A1%E6%9E%B6%E6%9E%84%E5%9B%BE.jpg)

&nbsp;
## 2. Features
![mark](https://gitee.com/zlt2000/images/raw/master/ZLT-MP%E5%BE%AE%E6%9C%8D%E5%8A%A1%E5%B9%B3%E5%8F%B0%E5%8A%9F%E8%83%BD%E5%9B%BE.jpg)

&nbsp;
## 3. Project Introduction
* **Technical exchange group** 
<table>
    <tr>
        <td><center><strong><a href="https://qm.qq.com/cgi-bin/qm/qr?k=HntAHTirZwCEjF8PQpjDYkw37Zx5rJg8&jump_from=webapi" target="_blank">Exchange three groups</a></strong></center></td>
	</tr>
	<tr>
        <td><a href="https://qm.qq.com/cgi-bin/qm/qr?k=HntAHTirZwCEjF8PQpjDYkw37Zx5rJg8&jump_from=webapi" target="_blank"><img width=250px height=300px alt="Exchange three groups" src="http://qiniu.zlt2000.cn/blog/20210616/htVdgkFMohAm.png?imageslim"/></a></td>
    </tr>
</table>


* **Detailed online documentation** ：https://www.kancloud.cn/zlt2000/microservices-platform/919418
  * **[Project update log](https://www.kancloud.cn/zlt2000/microservices-platform/936235)**
  * **[Documentation Update Log](https://www.kancloud.cn/zlt2000/microservices-platform/936236)**
* **Demo**： [http://zlt2000.cn](http://zlt2000.cn/)
  * Account：admin/admin
  * APM monitoring: admin/admin
  * Grafana：zlt/zlt123
  * Task managment：admin/123456
* **The demonstration environment has a full range of monitoring examples: log system + APM system + GPE system**
* Gitee address：https://gitee.com/zlt2000/microservices-platform
* Github address：https://github.com/zlt2000/microservices-platform
* Enterprise-level microservice architecture with front-end and back-end separation
* It is mainly aimed at solving common problems in microservice and business development **non-functional requirements**
* Deep customization of `Spring Security` truly implements a solution for stateless unified authority authentication based on `RBAC`, `jwt` and `oauth2`
* Provide application management to facilitate third-party system access, **Support multi-tenancy (application isolation)**
* Introduce the idea of componentization to achieve high cohesion, low coupling and high configurability
* Pay attention to code specifications, strictly control package dependencies, and each project basically has minimum dependencies
* Ideal for learning and enterprise use
>Refactored from the open source project OCP&cp：https://gitee.com/owenwangwen/open-capacity-platform

&nbsp;
## 4. 模块说明
```lua
central-platform -- parent project, public dependencies
│  ├─zlt-business -- Business module first-level engineering
│  │  ├─user-center -- User Center[7000]
│  │  ├─file-center -- document center[5000]
│  │  ├─code-generator -- Code generator[7300]
│  │  ├─search-center -- search Center
│  │  │  ├─search-client -- Search Center client
│  │  │  ├─search-server -- Search Center Server[7100]
│  │─zlt-commons -- General tool first-level engineering
│  │  ├─zlt-auth-client-spring-boot-starter -- Encapsulate the general operation logic of spring security client
│  │  ├─zlt-common-core -- Encapsulate common operation logic
│  │  ├─zlt-common-spring-boot-starter -- Encapsulate common operation logic
│  │  ├─zlt-db-spring-boot-starter -- Encapsulate the general operation logic of the database
│  │  ├─zlt-log-spring-boot-starter -- Encapsulate log general operation logic
│  │  ├─zlt-redis-spring-boot-starter -- Encapsulate Redis general operation logic
│  │  ├─zlt-loadbalancer-spring-boot-starter -- Encapsulate the general operation logic of Loadbalancer and Feign
│  │  ├─zlt-sentinel-spring-boot-starter -- Encapsulate Sentinel's general operation logic
│  │  ├─zlt-swagger2-spring-boot-starter -- Encapsulate Swagger general operation logic
│  │  ├─zlt-elasticsearch-spring-boot-starter -- Encapsulate Elasticsearch general operation logic
│  │  ├─zlt-oss-spring-boot-starter -- Encapsulate object storage general operation logic
│  │  ├─zlt-zookeeper-spring-boot-starter -- Encapsulate Zookeeper general operation logic
│  ├─zlt-config -- configuration center
│  ├─zlt-doc -- project documentation
│  ├─zlt-gateway -- api gateway first level engineering
│  │  ├─sc-gateway -- spring-cloud-gateway[9900]
│  ├─zlt-monitor -- Monitor first-class engineering
│  │  ├─sc-admin -- application monitoring[6500]
│  │  ├─log-center -- log center[7200]
│  ├─zlt-uaa -- spring-security authentication center[8000]
│  ├─zlt-register -- Registration Center Nacos[8848]
│  ├─zlt-web -- Front-end first-level engineering
│  │  ├─layui-web -- layui frontend[8066]
│  │  ├─react-web -- react front end[8066]
│  ├─zlt-demo -- demo level one project
│  │  ├─txlcn-demo -- txlcn distributed transaction demo
│  │  ├─seata-demo -- seata distributed transaction demo
│  │  ├─sharding-jdbc-demo -- sharding-jdbc database sub-table demo
│  │  ├─rocketmq-demo -- rocketmq and mq transaction demo
│  │  ├─sso-demo -- single sign-on demo
```

<table>
    <tr>
        <td><a target="_blank" href="https://www.aliyun.com/minisite/goods?userCode=dickv1kw&share_source=copy_link"><img width="460px" height="177px" alt="Ali Cloud" src="https://gitee.com/zlt2000/images/raw/master/aly.jpg"/></a></td>
        <td><a target="_blank" href="https://cloud.tencent.com/act/cps/redirect?redirect=1074&cps_key=5516bbd5876cd224d90bd41d53d3f7fe&from=console"><img width="460px" height="177px"  alt="Tencent Cloud" src="https://gitee.com/zlt2000/images/raw/master/txy.jpg"/></a></td>
    </tr>
</table>


## 5. Exchange feedback
* Have a look first [F&Q](https://www.kancloud.cn/zlt2000/microservices-platform/981382) Is there any relevant answer in
* Welcome to submit `ISSUS`, please write clearly the specific reason of the problem, reproduction steps and environment (context)
* Please enter the group for project/microservice communication:
  * group: [250883130(已满)](https://shang.qq.com/wpa/qunwpa?idkey=17544199255998bda0d938fb72b08d076c40c52c9904520b76eb5eb0585da71e)
  * Second group:[1041797659(已满)](https://shang.qq.com/wpa/qunwpa?idkey=41988facbc02f678942a7ee7ae03122f2ef0a10c948b3d07319f070bfb0d3a98)
  * Three groups:[512637767](https://qm.qq.com/cgi-bin/qm/qr?k=HntAHTirZwCEjF8PQpjDYkw37Zx5rJg8&jump_from=webapi)
* personal blog:[https://zlt2000.gitee.io](https://zlt2000.gitee.io)
* Personal email：zltdiablo@163.com
* Personal public number: [陶陶技术笔记](http://qiniu.zlt2000.cn/blog/20190902/M56cWjw7uNsc.png?imageslim)
* GitChat：[https://gitbook.cn/gitchat/author/5b2362320398d50d7b7ab29e](https://gitbook.cn/gitchat/author/5b2362320398d50d7b7ab29e)

&nbsp;
## 6. Screenshot (click to enlarge the preview)
<table>
    <tr>
        <td><img alt="front page" src="https://gitee.com/zlt2000/images/raw/master/%E9%A6%96%E9%A1%B5.png"/></td>
        <td><img alt="user search" src="https://gitee.com/zlt2000/images/raw/master/%E7%94%A8%E6%88%B7%E6%90%9C%E7%B4%A2.png"/></td>
    </tr>
    <tr>
        <td><img alt="log system" src="https://gitee.com/zlt2000/images/raw/master/%E6%97%A5%E5%BF%97%E7%B3%BB%E7%BB%9F.png"/></td>
        <td><img alt="log link" src="https://gitee.com/zlt2000/images/raw/master/%E6%97%A5%E5%BF%97%E9%93%BE%E8%B7%AF.png"/></td>
    </tr>
	<tr>
        <td><img alt="server_metrics" src="https://gitee.com/zlt2000/images/raw/master/server_metrics.png"/></td>
        <td><img alt="application_metrics" src="https://gitee.com/zlt2000/images/raw/master/application_metrics.png"/></td>
    </tr>
    <tr>
        <td><img alt="skywalking Home.png" src="https://gitee.com/zlt2000/images/raw/master/skywalking%E9%A6%96%E9%A1%B5.png"/></td>
        <td><img alt="skywalking application topology diagram" src="https://gitee.com/zlt2000/images/raw/master/skywalking%E5%BA%94%E7%94%A8%E6%8B%93%E6%89%91%E5%9B%BE.png"/></td>
    </tr>
    <tr>
        <td><img alt="elk" src="https://gitee.com/zlt2000/images/raw/master/elk.png"/></td>
        <td><img alt="task center" src="https://gitee.com/zlt2000/images/raw/master/%E4%BB%BB%E5%8A%A1%E4%B8%AD%E5%BF%83.png"/></td>
    </tr>
    <tr>
        <td><img alt="Log Center 02" src="https://gitee.com/zlt2000/images/raw/master/%E6%97%A5%E5%BF%97%E4%B8%AD%E5%BF%8302.png"/></td>
        <td><img alt="slow query sql" src="https://gitee.com/zlt2000/images/raw/master/%E6%85%A2%E6%9F%A5%E8%AF%A2sql.png"/></td>
    </tr>
    <tr>
        <td><img alt="nacos-discovery" src="https://gitee.com/zlt2000/images/raw/master/nacos-discovery.png"/></td>
        <td><img alt="Application Throughput Monitoring" src="https://gitee.com/zlt2000/images/raw/master/%E5%BA%94%E7%94%A8%E5%90%9E%E5%90%90%E9%87%8F%E7%9B%91%E6%8E%A7.png"/></td>
    </tr>
</table>