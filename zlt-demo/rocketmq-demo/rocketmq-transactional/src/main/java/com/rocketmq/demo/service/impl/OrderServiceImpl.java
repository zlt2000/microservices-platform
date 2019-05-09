package com.rocketmq.demo.service.impl;

import com.rocketmq.demo.model.Order;
import com.rocketmq.demo.service.IOrderService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zlt
 */
@Slf4j
@Service
public class OrderServiceImpl implements IOrderService {
    @Override
    public void save(Order order) {
        System.out.println("============保存订单成功：" + order.getOrderId());
    }
}