[TOC]

## 一、说明

本demo是通过tx-lcn测试lcn模式的分布式事务，主要是模拟以下两个场景

1. `service-a` 调用`service-b`调用`service-c` 都成功
2. `service-a` 调用`service-b`调用`service-c` 最后`service-a`抛出异常全部回滚



&nbsp;

## 二、运行步骤

### 1. 初始化TxManager的数据

执行`zlt-doc\sql\tx-manager.sql`和`zlt-doc\sql\tx_logger.sql`脚本

### 2. 修改TxManager的配置

工程目录：`zlt-transaction\txlcn-tm`，修改nacos、数据库和redis

### 2. 启动TxManager

工程目录：`zlt-transaction\txlcn-tm`

### 3. 初始化demo的数据

执行`zlt-demo\txlcn-demo\txlcn-demo.sql`脚本

### 4. 修改3个服务的配置

`nacos`地址和数据库地址



&nbsp;

## 三、启动模块与测试

### 1. 正常提交事务
访问 发起方提供的Rest接口 `/txlcn?value=the-value`。发现事务全部提交
![result](https://www.txlcn.org/img/docs/result.png)

### 2. 回滚事务
访问 发起方提供的Rest接口 `/txlcn?value=the-value&ex=throw`。发现发起方由本地事务回滚，而参与方ServiceB、ServiceC，由于TX-LCN的协调，数据也回滚了。
![error_result](https://www.txlcn.org/img/docs/error_result.png)