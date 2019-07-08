package com.central.oauth2.common.annotation;

import com.central.oauth2.common.config.TokenStoreConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启token存储认证配置化
 *
 * @author zlt
 * @date 2019/7/7
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(TokenStoreConfig.class)
public @interface EnableTokenStore {
}
