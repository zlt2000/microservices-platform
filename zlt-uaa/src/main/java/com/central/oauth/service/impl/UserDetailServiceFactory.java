package com.central.oauth.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.central.common.constant.SecurityConstants;
import com.central.oauth.service.ZltUserDetailsService;
import com.central.oauth2.common.util.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户service工厂
 *
 * @author zlt
 * @version 1.0
 * @date 2021/7/24
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@Service
public class UserDetailServiceFactory {
    private static final String ERROR_MSG = "找不到账号类型为 {} 的实现类";

    @Resource
    private List<ZltUserDetailsService> userDetailsServices;

    public ZltUserDetailsService getService(Authentication authentication) {
        String accountType = AuthUtils.getAccountType(authentication);
        return this.getService(accountType);
    }

    public ZltUserDetailsService getService(String accountType) {
        if (StrUtil.isEmpty(accountType)) {
            accountType = SecurityConstants.DEF_ACCOUNT_TYPE;
        }
        log.info("UserDetailServiceFactory.getService:{}", accountType);
        if (CollUtil.isNotEmpty(userDetailsServices)) {
            for (ZltUserDetailsService userService : userDetailsServices) {
                if (userService.supports(accountType)) {
                    return userService;
                }
            }
        }
        throw new InternalAuthenticationServiceException(StrUtil.format(ERROR_MSG, accountType));
    }
}
