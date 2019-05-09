package com.sharding.demo.config;

import com.central.db.config.DefaultMybatisPlusConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zlt
 */
@Configuration
@MapperScan({"com.sharding.demo.mapper*"})
public class MybatisPlusConfig extends DefaultMybatisPlusConfig {
}
