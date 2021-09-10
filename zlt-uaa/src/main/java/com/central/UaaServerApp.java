package com.central;

import com.central.common.lb.annotation.EnableFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/** 
* @author zlt
*/
@EnableFeignClients
@EnableFeignInterceptor
@EnableDiscoveryClient
//@EnableRedisHttpSession
@SpringBootApplication
public class UaaServerApp {
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(UaaServerApp.class);
		application.setEnvironmentPrefix("zlt-uaa");
		application.run(args);
	}
}
