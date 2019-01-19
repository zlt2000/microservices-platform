package com.central.gateway;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** 
* @author zlt
*/
@EnableAdminServer
@EnableDiscoveryClient 
@SpringBootApplication  
public class MonitorApp {
	public static void main(String[] args) {
		SpringApplication.run(MonitorApp.class, args);
	}
}
