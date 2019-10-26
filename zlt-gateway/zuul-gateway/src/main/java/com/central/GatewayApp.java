package com.central;

import com.central.common.ribbon.annotation.EnableBaseFeignInterceptor;
import com.central.common.ribbon.annotation.EnableFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zlt
 */
@EnableZuulProxy
@EnableFeignClients
@EnableBaseFeignInterceptor
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApp {
	public static void main(String[] args) {
		SpringApplication.run(GatewayApp.class, args);
	}
}
