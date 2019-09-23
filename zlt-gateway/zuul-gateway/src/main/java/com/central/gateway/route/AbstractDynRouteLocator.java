package com.central.gateway.route;

import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 扩展 {@link SimpleRouteLocator} 实现动态路由
 *
 * @author zlt
 * @date 2019/8/21
 */
public abstract class AbstractDynRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {
    private ZuulProperties properties;

    public AbstractDynRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
        this.properties = properties;
    }

    /**
     * 刷新路由
     */
    @Override
    public void refresh() {
        doRefresh();
    }

    @Override
    protected Map<String, ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulRoute> routesMap = new LinkedHashMap<>();
        // 从application.properties中加载静态路由信息
        routesMap.putAll(super.locateRoutes());
        // 从数据源中加载动态路由信息
        routesMap.putAll(loadDynamicRoute());
        // 优化一下配置
        LinkedHashMap<String, ZuulRoute> values = new LinkedHashMap<>();
        for (Map.Entry<String, ZuulRoute> entry : routesMap.entrySet()) {
            String path = entry.getKey();
            // Prepend with slash if not already present.
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (StringUtils.hasText(this.properties.getPrefix())) {
                path = this.properties.getPrefix() + path;
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
            }
            values.put(path, entry.getValue());
        }
        return values;
    }

    /**
     * 加载路由配置，由子类去实现
     */
    public abstract Map<String, ZuulRoute> loadDynamicRoute();
}
