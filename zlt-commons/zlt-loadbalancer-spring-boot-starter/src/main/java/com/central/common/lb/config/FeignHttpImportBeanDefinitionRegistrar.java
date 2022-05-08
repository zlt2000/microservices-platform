package com.central.common.lb.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import javax.servlet.Filter;

/**
 * 示例
 *
 * @author jarvis create by 2022/5/8
 */
public class FeignHttpImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        try {
            Class.forName("javax.servlet.Filter", false, registry.getClass().getClassLoader());
            AbstractBeanDefinition feignHttpInterceptorConfig = BeanDefinitionBuilder.genericBeanDefinition(FeignHttpInterceptorConfig.class).getBeanDefinition();
            registry.registerBeanDefinition(importBeanNameGenerator.generateBeanName(feignHttpInterceptorConfig, registry), feignHttpInterceptorConfig);


            AbstractBeanDefinition feignInterceptorConfig = BeanDefinitionBuilder.genericBeanDefinition(FeignInterceptorConfig.class).getBeanDefinition();
            registry.registerBeanDefinition(importBeanNameGenerator.generateBeanName(feignInterceptorConfig, registry), feignInterceptorConfig);
        } catch (ClassNotFoundException e) {
        }
    }
}
