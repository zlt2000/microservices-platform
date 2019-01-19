package com.central;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author zlt
 * 文件中心
 */
@EnableDiscoveryClient
@SpringBootApplication
public class FileCenterApp {
    public static void main(String[] args) {
        // 固定端口
        SpringApplication.run(FileCenterApp.class, args);
    }

}