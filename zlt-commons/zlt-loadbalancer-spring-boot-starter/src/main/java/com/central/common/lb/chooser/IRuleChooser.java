package com.central.common.lb.chooser;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * service选择器类
 *
 * @author jarvis create by 2022/3/13
 */
public interface IRuleChooser {
    ServiceInstance choose(List<ServiceInstance> instances);
}
