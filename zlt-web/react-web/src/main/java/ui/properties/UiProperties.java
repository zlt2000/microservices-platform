package ui.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 前端服务配置
 *
 * @author zlt
 * @date 2020/2/3
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "zlt.ui")
@RefreshScope
public class UiProperties {

    private String pathContext = "/";
}
