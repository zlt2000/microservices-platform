package com.sharding;

import com.central.common.utils.IdGenerator;

/**
 * sharding-jdbc demo
 *
 * @author zlt
 */
public class TestApplication {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            long userId = IdGenerator.getId();
            System.out.println(userId);
            userId = userId & 15;
            System.out.println(userId);
        }
        /*
        long orderId = IdGenerator.getId();
        System.out.println(orderId);
        orderId = (orderId << 4) | userId;
        System.out.println(orderId);*/
    }
}
