## **详细的原理和部署细节请查看**
[Spring Cloud同步场景分布式事务怎样做？试试Seata](https://mp.weixin.qq.com/s/0yCmHzlXDC9BkbUuEt0_fQ)



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
