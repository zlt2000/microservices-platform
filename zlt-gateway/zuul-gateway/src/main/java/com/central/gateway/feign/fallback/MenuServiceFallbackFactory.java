package com.central.gateway.feign.fallback;

import cn.hutool.core.collection.CollectionUtil;
import com.central.common.feign.UserService;
import com.central.common.model.LoginAppUser;
import com.central.common.model.SysMenu;
import com.central.common.model.SysUser;
import com.central.gateway.feign.MenuService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * menuService降级工场
 *
 * @author zlt
 * @date 2019/1/18
 */
@Slf4j
@Component
public class MenuServiceFallbackFactory implements FallbackFactory<MenuService> {
    @Override
    public MenuService create(Throwable throwable) {
        return roleIds -> {
            log.error("调用findByRoleCodes异常：{}", roleIds, throwable);
            return CollectionUtil.newArrayList();
        };
    }
}
