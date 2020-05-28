package com.central.gateway.swagger;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.HashSet;
import java.util.Set;

/**
 * swagger聚合配置
 *
 * @author zlt
 * @date 2019/10/5
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Setter
@Getter
@ConfigurationProperties("zlt.swagger-agg")
@RefreshScope
public class SwaggerAggProperties {
    /**
     * Swagger返回JSON文档的接口路径（全局配置）
     */
    private String apiDocsPath = "/v2/api-docs";

    /**
     * Swagger文档版本（全局配置）
     */
    private String swaggerVersion = "2.0";

    /**
     * 自动生成文档的路由名称，设置了generateRoutes之后，ignoreRoutes不生效
     */
    private Set<String> generateRoutes = new HashSet<>();

    /**
     * 不自动生成文档的路由名称，设置了generateRoutes之后，本配置不生效
     */
    private Set<String> ignoreRoutes = new HashSet<>();

    /**
     * 是否显示该路由
     */
    public boolean isShow(String route) {
        int generateRoutesSize = generateRoutes.size();
        int ignoreRoutesSize = ignoreRoutes.size();

        if(generateRoutesSize > 0 && !generateRoutes.contains(route)) {
            return false;
        }

        if(ignoreRoutesSize > 0 && ignoreRoutes.contains(route)) {
            return false;
        }

        return true;
    }
}
