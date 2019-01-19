package com.central.common.utils;

import cn.hutool.core.bean.BeanUtil;
import com.central.common.model.SysRole;
import com.central.common.model.SysUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.central.common.model.LoginAppUser;

import java.util.*;

/**
 * 获取用户信息
 *
 * @author zlt
 */
public class SysUserUtil {
    public static LoginAppUser getLoginAppUser(SysUser user) {
        LoginAppUser login = new LoginAppUser();
        BeanUtil.copyProperties(user, login);
        return login;
    }

    /**
     * 获取登陆的用户名
     */
    public static String getUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username = null;
        if (principal instanceof SysUser) {
            username = ((SysUser) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }
        return username;
    }
}
