package com.central.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * @author zlt
 */
@Configuration
@SpringBootApplication
public class BackWebApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackWebApplication.class, args);
	}
}
