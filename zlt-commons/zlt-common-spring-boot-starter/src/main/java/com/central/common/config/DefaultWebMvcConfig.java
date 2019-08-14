package com.central.common.config;

import com.central.common.feign.UserService;
import com.central.common.interceptor.TenantInterceptor;
import com.central.common.interceptor.TraceInterceptor;
import com.central.common.resolver.ClientArgumentResolver;
import com.central.common.resolver.TokenArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 默认SpringMVC拦截器
 *
 * @author zlt
 * @date 2019/8/5
 */
public class DefaultWebMvcConfig extends WebMvcConfigurationSupport {
	@Autowired
	private UserService userService;

    /**
     * 配置SpringMVC拦截器，添加租户拦截器
     */
	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		//租户拦截器
		registry.addInterceptor(new TenantInterceptor()).addPathPatterns("/**");

		//日志链路追踪拦截器
		registry.addInterceptor(new TraceInterceptor()).addPathPatterns("/**");

		super.addInterceptors(registry);
	}

	/**
	 * Token参数解析
	 *
	 * @param argumentResolvers 解析类
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		//注入用户信息
		argumentResolvers.add(new TokenArgumentResolver(userService));
		//注入应用信息
		argumentResolvers.add(new ClientArgumentResolver());
	}
}
