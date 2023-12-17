package com.central.gateway;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** 
* @author zlt
*/
@EnableAdminServer
@EnableDiscoveryClient
@EnableConfigurationProperties(AdminServerProperties.class)
@SpringBootApplication  
public class MonitorApp {
	public static void main(String[] args) {
		SpringApplication.run(MonitorApp.class, args);
	}
}
