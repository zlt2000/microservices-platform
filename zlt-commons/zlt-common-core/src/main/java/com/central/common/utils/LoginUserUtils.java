package com.central.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.central.common.constant.SecurityConstants;
import com.central.common.context.LoginUserContextHolder;
import com.central.common.feign.UserService;
import com.central.common.model.LoginAppUser;
import com.central.common.model.SysRole;
import com.central.common.model.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

/**
 * 获取当前登录人工具类
 *
 * @author zlt
 * @version 1.0
 * @date 2022/6/26
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class LoginUserUtils {
    private final static String ATT_PERMISSIONS = "permissions";

    /**
     * 获取当前登录人
     */
    public static LoginAppUser getCurrentUser(HttpServletRequest request, boolean isFull) {
        LoginAppUser user = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = getUser(authentication);
        }
        if (user == null) {
            String userId = request.getHeader(SecurityConstants.USER_ID_HEADER);
            String username = request.getHeader(SecurityConstants.USER_HEADER);
            String roles = request.getHeader(SecurityConstants.ROLE_HEADER);

            if (StrUtil.isAllNotBlank(username, userId)) {
                if (isFull) {
                    SysUser sysUser = getSysUser(username);
                    if (sysUser != null) {
                        return getLoginAppUser(sysUser);
                    }
                } else {
                    Collection<GrantedAuthority> authorities = new HashSet<>();
                    if (StrUtil.isNotBlank(roles)) {
                        Arrays.stream(roles.split(",")).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                    }
                    return new LoginAppUser(Long.valueOf(userId), username, authorities);
                }
            }
        }
        return user;
    }

    public static LoginAppUser getCurrentUser(boolean isFull) {
        // 从请求上下文里获取 Request 对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest contextRequest = requestAttributes.getRequest();
        if (contextRequest != null) {
            return LoginUserUtils.getCurrentUser(contextRequest, isFull);
        }
        return null;
    }

    public static SysUser getCurrentSysUser() {
        LoginAppUser appUser = getCurrentUser(false);
        if (appUser != null) {
            return getSysUser(appUser.getUsername());
        }
        return null;
    }

    /**
     * 获取登陆的用户对象
     */
    public static LoginAppUser getUser(Authentication authentication) {
        LoginAppUser user = null;
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            //客户端模式只返回一个clientId
            if (principal instanceof LoginAppUser) {
                user = (LoginAppUser) principal;
            } else if (principal instanceof Jwt jwt) {
                Map<String, Object> userMap = jwt.getClaim(SecurityConstants.DETAILS_USER);
                user = getLoginAppUser(userMap);
            }
        }
        return user;
    }

    /**
     * 获取登陆的用户名
     */
    public static String getUsername(Authentication authentication) {
        LoginAppUser user = getUser(authentication);
        String username = null;
        if (user != null) {
            username = user.getUsername();
        }
        return username;
    }

    /**
     * 用户信息赋值 context 对象
     */
    public static LoginAppUser setContext(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginAppUser user = getUser(authentication);
        LoginUserContextHolder.setUser(user);
        return user;
    }

    public static LoginAppUser getLoginAppUser(SysUser sysUser) {
        List<SysRole> sysRoles = sysUser.getRoles();
        Collection<GrantedAuthority> authorities = new HashSet<>();
        if (sysRoles != null) {
            sysRoles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getCode())));
        }

        return new LoginAppUser(sysUser.getId()
                , sysUser.getUsername(), sysUser.getPassword()
                , sysUser.getMobile(), sysUser.getPermissions()
                , sysUser.getEnabled(), true, true, true
                , authorities);
    }

    @SuppressWarnings("unchecked")
    public static LoginAppUser getLoginAppUser(Map<String, Object> userObj) {
        return new LoginAppUser(
                (Long)userObj.get("id"),
                (String)userObj.get("username"),
                (String)userObj.get("password"),
                (String)userObj.get("mobile"),
                (Collection<String>)userObj.get(ATT_PERMISSIONS),
                Optional.ofNullable((Boolean)userObj.get("enabled")).orElse(true),
                true, true, true,
                getGrantedAuthoritys((List<Map<String, String>>)userObj.get("authorities")));
    }
    private static Collection<? extends GrantedAuthority> getGrantedAuthoritys(List<Map<String, String>> authorities) {
        if (authorities != null) {
            List<GrantedAuthority> result = new ArrayList<>(authorities.size());
            authorities.forEach(e -> result.add(new SimpleGrantedAuthority(e.get("role"))));
            return result;
        }
        return null;
    }

    private static SysUser getSysUser(String username) {
        UserService userService = SpringUtil.getBean(UserService.class);
        return userService.findByUsername(username);
    }
}
