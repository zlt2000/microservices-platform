package com.central.oauth2.common.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 续签配置
 *
 * @author zlt
 * @version 1.0
 * @date 2019/7/9
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Setter
@Getter
public class RenewProperties {
    /**
     * 是否开启token自动续签（目前只有redis实现）
     */
    private Boolean enable = false;

    /**
     * 白名单，配置需要自动续签的应用id（与黑名单互斥，只能配置其中一个），不配置默认所有应用都生效
     * 配置enable为true时才生效
     */
    private List<String> includeClientIds = new ArrayList<>();

    /**
     * 黑名单，配置不需要自动续签的应用id（与白名单互斥，只能配置其中一个）
     * 配置enable为true时才生效
     */
    private List<String> exclusiveClientIds = new ArrayList<>();

    /**
     * 续签时间比例，当前剩余时间小于小于过期总时长的50%则续签
     */
    private Double timeRatio = 0.5;
}
