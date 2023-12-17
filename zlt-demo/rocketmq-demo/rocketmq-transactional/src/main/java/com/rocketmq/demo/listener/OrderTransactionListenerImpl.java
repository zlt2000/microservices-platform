package com.rocketmq.demo.listener;

import com.central.common.utils.JsonUtil;
import com.rocketmq.demo.model.Order;
import com.rocketmq.demo.service.IOrderService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

/**
 * @author zlt
 */
@Component("myTransactionListener")
public class OrderTransactionListenerImpl implements TransactionListener {
	@Resource
	private IOrderService orderService;

    /**
     * 提交本地事务
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object arg) {
        //插入订单数据
        String orderJson =  new String((message.getBody()));
        Order order = JsonUtil.toObject(orderJson, Order.class);
        orderService.save(order);

        String produceError = message.getProperty("produceError");
        if ("1".equals(produceError)) {
            System.err.println("============Exception：订单进程挂了，事务消息没提交");
            //模拟插入订单后服务器挂了，没有commit事务消息
            throw new RuntimeException("============订单服务器挂了");
        }

        //提交事务消息
        return LocalTransactionState.COMMIT_MESSAGE;
    }

    /**
     * 事务回查接口
     *
     * 如果事务消息一直没提交，则定时判断订单数据是否已经插入
     *     是：提交事务消息
     *     否：回滚事务消息
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt message) {
        String orderId = message.getProperty("orderId");
        System.out.println("============事务回查-orderId：" + orderId);
        //判断之前的事务是否已经提交：订单记录是否已经保存
        int count = 1;
        //select count(1) from t_order where order_id = ${orderId}
        System.out.println("============事务回查-订单已生成-提交事务消息");
        return count > 0 ? LocalTransactionState.COMMIT_MESSAGE : LocalTransactionState.ROLLBACK_MESSAGE;
    }
}