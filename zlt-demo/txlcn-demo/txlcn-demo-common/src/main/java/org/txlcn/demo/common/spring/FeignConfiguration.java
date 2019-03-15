package org.txlcn.demo.common.spring;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Date: 2018/12/25
 *
 * @author ujued
 */
@ComponentScan
@Configuration
@EnableFeignClients
public class FeignConfiguration {
}
