package com.central.common.config;

import com.central.common.constant.CommonConstant;
import com.central.common.utils.CustomBanner;
import com.nepxion.banner.BannerConstant;
import com.nepxion.banner.Description;
import com.nepxion.banner.LogoBanner;
import com.taobao.text.Color;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Banner初始化
 *
 * @author zlt
 * @date 2019/8/28
 */
public class BannerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (!(applicationContext instanceof AnnotationConfigApplicationContext)) {
            LogoBanner logoBanner = new LogoBanner(BannerInitializer.class, "/zltmp/logo.txt", "Welcome to zlt", 5, 6, new Color[5], true);
            CustomBanner.show(logoBanner, new Description(BannerConstant.VERSION + ":", CommonConstant.PROJECT_VERSION, 0, 1)
                    , new Description("Github:", "https://github.com/zlt2000", 0, 1)
                    , new Description("Blog:", "https://zlt2000.gitee.io", 0, 1)
            );
        }
    }
}
