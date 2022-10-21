package ui.config;

import ui.properties.UiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(UiProperties.class)
public class StaticResourceConfiguration implements WebMvcConfigurer {

    private final UiProperties uiProperties;

    public StaticResourceConfiguration(UiProperties uiProperties) {
        this.uiProperties = uiProperties;
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String pathContext = uiProperties.getPathContext();
        registry.addResourceHandler(pathContext+"/**").addResourceLocations("classpath:/META-INF/central-platform-ui/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        String pathContext = uiProperties.getPathContext();
        registry.addViewController(pathContext+"/").setViewName("index.html");
    }
}
