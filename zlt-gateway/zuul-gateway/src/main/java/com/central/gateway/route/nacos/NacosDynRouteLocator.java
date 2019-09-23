package com.central.gateway.route.nacos;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.central.gateway.config.ZuulRouteEntity;
import com.central.gateway.route.AbstractDynRouteLocator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Nacos动态路由实现类
 *
 * @author zlt
 * @date 2019/8/22
 */
@Slf4j
public class NacosDynRouteLocator extends AbstractDynRouteLocator {
    private static final String ZUUL_DATA_ID = "zuul-routes";
    private static final String ZUUL_GROUP_ID = "ZUUL_GATEWAY";

    private NacosConfigProperties nacosConfigProperties;

    private ApplicationEventPublisher publisher;

    private NacosDynRouteLocator locator;

    @Setter
    private List<ZuulRouteEntity> zuulRouteEntities;

    public NacosDynRouteLocator(NacosConfigProperties nacosConfigProperties, ApplicationEventPublisher publisher,
                                String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
        this.nacosConfigProperties = nacosConfigProperties;
        this.publisher = publisher;
        this.locator = this;
        addListener();
    }

    /**
     * 添加Nacos监听
     */
    private void addListener() {
        try {
            nacosConfigProperties.configServiceInstance().addListener(ZUUL_DATA_ID, ZUUL_GROUP_ID, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    //赋值路由信息
                    locator.setZuulRouteEntities(getListByStr(configInfo));
                    RoutesRefreshedEvent routesRefreshedEvent = new RoutesRefreshedEvent(locator);
                    publisher.publishEvent(routesRefreshedEvent);
                }
            });
        } catch (NacosException e) {
            log.error("nacos-addListener-error", e);
        }
    }

    @Override
    public Map<String, ZuulProperties.ZuulRoute> loadDynamicRoute() {
        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        if (zuulRouteEntities == null) {
            zuulRouteEntities = getNacosConfig();
        }
        for (ZuulRouteEntity result : zuulRouteEntities) {
            if (StrUtil.isBlank(result.getPath()) || !result.isEnabled()) {
                continue;
            }
            ZuulRoute zuulRoute = new ZuulRoute();
            BeanUtil.copyProperties(result, zuulRoute);
            routes.put(zuulRoute.getPath(), zuulRoute);
        }
        return routes;
    }

    /**
     * 查询zuul的路由配置
     */
    private List<ZuulRouteEntity> getNacosConfig() {
        try {
            String content = nacosConfigProperties.configServiceInstance().getConfig(ZUUL_DATA_ID, ZUUL_GROUP_ID,5000);
            return getListByStr(content);
        } catch (NacosException e) {
            log.error("listenerNacos-error", e);
        }
        return new ArrayList<>(0);
    }

    public List<ZuulRouteEntity> getListByStr(String content) {
        if (StrUtil.isNotEmpty(content)) {
            return JSONObject.parseArray(content, ZuulRouteEntity.class);
        }
        return new ArrayList<>(0);
    }
}
