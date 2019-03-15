package org.txlcn.demo.common.spring;

import org.springframework.stereotype.Component;

/**
 * Description:
 * Date: 19-2-19 下午1:53
 *
 * @author ujued
 */
@Component
public class ServiceBFallback implements ServiceBClient {
    @Override
    public String rpc(String name) {
        return "fallback";
    }
}
