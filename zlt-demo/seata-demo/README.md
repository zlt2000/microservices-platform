[TOC]
## 一、说明
**包括以下5个模块，分别是**
* `business-service`：业务服务
* `storage-service`：库存服务
* `order-service`：订单服务
* `account-service`：账号服务
* `seata-common-starter`：公共工程

&nbsp;
**本demo主要是模拟用户下订单的场景，整个流程如下：**
用户下单(`business-service`) -> 扣库存(`storage-service`) -> 创建订单(`order-service`) -> 减少账户余额(`account-service`)

&nbsp;
**提供以下两个测试接口**

1. 事务成功：扣除库存成功 > 创建订单成功 > 扣减账户余额成功
http://localhost:9090/placeOrder 
1. 事务失败：扣除库存成功 > 创建订单成功 > 扣减账户余额失败，事务回滚
http://localhost:9090/placeOrderFallBack

&nbsp;
## 二、运行步骤
### 2.1. 部署运行seata-server
`seata`的注册中心和配置中心需改成`nacos`
**下载地址：**https://github.com/seata/seata/releases

### 2.2. 初始化demo的数据库脚本
[seata-demo.sql](seata-demo.sql)

### 2.3. 修改各个服务的配置
* 修改`bootstrap.yml`中的nacos地址
* 修改`application.yml`中的数据库配置
