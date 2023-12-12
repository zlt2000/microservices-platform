package com.central.oauth.service.impl;

import com.central.common.constant.SecurityConstants;
import com.central.common.feign.UserService;
import com.central.common.model.SysUser;
import com.central.common.utils.LoginUserUtils;
import com.central.oauth.service.ZltUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zlt
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@Service
public class UserDetailServiceImpl implements ZltUserDetailsService {
    private static final String ACCOUNT_TYPE = SecurityConstants.DEF_ACCOUNT_TYPE;

    @Resource
    private UserService userService;

    @Override
    public boolean supports(String accountType) {
        return ACCOUNT_TYPE.equals(accountType);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        SysUser sysUser = userService.findByUsername(username);
        if (sysUser == null) {
            throw new InternalAuthenticationServiceException("用户名或密码错误");
        }
        checkUser(sysUser);
        return LoginUserUtils.getLoginAppUser(sysUser);
    }

    @Override
    public UserDetails loadUserByUserId(String openId) {
        SysUser sysUser = userService.findByOpenId(openId);
        checkUser(sysUser);
        return LoginUserUtils.getLoginAppUser(sysUser);
    }

    @Override
    public UserDetails loadUserByMobile(String mobile) {
        SysUser sysUser = userService.findByMobile(mobile);
        checkUser(sysUser);
        return LoginUserUtils.getLoginAppUser(sysUser);
    }

    private void checkUser(SysUser sysUser) {
        if (sysUser != null && !sysUser.getEnabled()) {
            throw new DisabledException("用户已作废");
        }
    }
}
