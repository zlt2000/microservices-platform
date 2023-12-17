package com.central.oauth.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.central.common.constant.SecurityConstants;
import com.central.oauth.service.ZltUserDetailsService;
import com.central.oauth.exception.CustomOAuth2AuthenticationException;
import com.central.oauth2.common.util.AuthUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户service工厂
 *
 * @author zlt
 * @version 1.0
 * @date 2021/7/24
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@Service
public class UserDetailServiceFactory {
    private static final String ERROR_MSG = "Cannot find the implementation class for the account type {}";

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
        throw new CustomOAuth2AuthenticationException(StrUtil.format(ERROR_MSG, accountType));
    }
}
