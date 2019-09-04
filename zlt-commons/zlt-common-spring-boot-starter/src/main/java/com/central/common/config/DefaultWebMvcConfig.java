package com.central.common.config;

import com.central.common.constant.ConfigConstants;
import com.central.common.feign.UserService;
import com.central.common.interceptor.LbIsolationInterceptor;
import com.central.common.interceptor.TenantInterceptor;
import com.central.common.interceptor.TraceInterceptor;
import com.central.common.resolver.ClientArgumentResolver;
import com.central.common.resolver.TokenArgumentResolver;
import com.central.log.properties.TraceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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

	@Autowired
    private TraceProperties traceProperties;

	@Value("${" + ConfigConstants.CONFIG_RIBBON_ISOLATION_ENABLED + ":false}")
	private boolean enableIsolation;

    /**
     * 配置SpringMVC拦截器，添加租户拦截器
     */
	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		//租户拦截器
		registry.addInterceptor(new TenantInterceptor()).addPathPatterns("/**");

		if (traceProperties.getEnable()) {
            //日志链路追踪拦截器
            registry.addInterceptor(new TraceInterceptor()).addPathPatterns("/**");
        }

		if (enableIsolation) {
			//负债均衡隔离规则拦截器
			registry.addInterceptor(new LbIsolationInterceptor()).addPathPatterns("/**");
		}

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

	/**
	 * 设置资源文件目录
	 * @param registry
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
				.addResourceLocations("classpath:/resources/")
				.addResourceLocations("classpath:/static/")
				.addResourceLocations("classpath:/public/");
		super.addResourceHandlers(registry);
	}
}
