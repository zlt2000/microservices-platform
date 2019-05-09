[TOC]

## 一、说明

rocketMQ的demo主要模拟两个场景：

1. 集成Spring-Cloud-Stream消息框架的消息生产和消费
   * `rocketmq-produce`：消息生产者
   * `rocketmq-consume`：消息消费者

2. rocketMQ的事务消息，模拟场景：生成订单记录 -> MQ -> 增加积分
   * `rocketmq-transactional`



## 二、环境准备

安装`RocketMQ`



## 三、`produce`和`consume` demo

### 1. 修改application.yml配置

修改`rocketmq-produce`和`rocketmq-consume`里`namesrv-addr`的值为`RocketMQ`的服务地址

### 2. 启动consume

运行`rocketmq-consume`的`RocketMqConsumeApplication`

> 消费者有3个组
>
> input：为字符串消息，消费所有消息
>
> input2：为对象消息，只消费tag为`tagObj`的消息
>
> input3：为spring.messaging对象消息，消费所有消息

### 3. 启动produce

运行`rocketmq-produce`的`RocketMqProduceApplication`

> 运行后会发送5条消息：2条字符串消息，3条对象消息(带tag)



&nbsp;

## 四、事务消息demo

### 1. 修改application.yml配置

修改`rocketmq-transactional`里`namesrv-addr`的值为`RocketMQ`的服务地址

### 2. 启动

运行`rocketmq-transactional`的`RocketMqTxApplication`

### 3. 测试

测试的场景主要有3个：

#### 3.1. 正常情况

流程如下：

1. 订单创建
2. 发送mq消息
3. 消费消息增加积分

http://localhost:11002/success

#### 3.2. 发送消息失败

流程如下：

1. 订单创建
2. 发送mq消息 -> 失败
3. 事务回查(等待1分钟左右)
4. 发送mq消息
5. 消费消息增加积分

http://localhost:11002/produceError

#### 3.3. 消费消息失败

流程如下：

1. 订单创建
2. 发送mq消息
3. 消费消息增加积分 -> 失败
4. 重试消费消息 -> 失败
5. 进入死信队列
6. 消费死信队列的消息
7. 记录日志并发出预警

http://localhost:11002/consumeError

> 注意：消费死信队列topic，必需把topic的perm改成6才能消费，默认是2