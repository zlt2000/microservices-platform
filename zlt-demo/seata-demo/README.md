## **详细的原理和部署细节请查看**
[SEATA同步场景事务(v1.3)](https://www.kancloud.cn/zlt2000/microservices-platform/1280566)

&nbsp;
## 测试环境
* mysql 5.7
* seata 1.3
* nacos 1.3
> **注意**：如果nacos使用低于1.3的版本不需要配置username和password；如果使用1.3以上版本必需开启 `nacos.core.auth.enabled=true` 并且配置username和password，否则读取不到seata-server

&nbsp;
## 配置中心的配置如下
**config.txt**
```properties
service.vgroupMapping.test_tx_service_group=default
store.mode=db
store.db.datasource=druid
store.db.dbType=mysql
store.db.url=jdbc:mysql://192.168.28.130:3306/seata?useUnicode=true
store.db.driverClassName=com.mysql.jdbc.Driver
store.db.user=root
store.db.password=root
store.db.minConn=5
store.db.maxConn=30
store.db.globalTable=global_table
store.db.branchTable=branch_table
store.db.queryLimit=100
store.db.lockTable=lock_table
store.db.maxWait=5000
```
>根据自己的环境修改 url、user、password 配置值

&nbsp;
## seata的配置如下
**registry.conf**
```json
registry {
  # file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
  type = "nacos"

  nacos {
    application = "seata-server"
    serverAddr = "192.168.28.130:8848"
    group = "SEATA_GROUP"
    namespace = ""
    cluster = "default"
    username = "nacos"
    password = "nacos"
  }
}

config {
  # file、nacos 、apollo、zk、consul、etcd3
  type = "nacos"

  nacos {
    serverAddr = "192.168.28.130:8848"
    namespace = ""
    group = "SEATA_GROUP"
    username = "nacos"
    password = "nacos"
  }
}
```

&nbsp;
## 说明
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
