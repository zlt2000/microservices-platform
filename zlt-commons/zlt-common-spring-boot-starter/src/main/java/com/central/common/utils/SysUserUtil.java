package com.central.common.utils;

import cn.hutool.core.bean.BeanUtil;
import com.central.common.model.SysUser;
import org.springframework.security.core.Authentication;

import com.central.common.model.LoginAppUser;

/**
 * 获取用户信息
 *
 * @author zlt
 */
public class SysUserUtil {
    private SysUserUtil() {
        throw new IllegalStateException("Utility class");
    }

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
