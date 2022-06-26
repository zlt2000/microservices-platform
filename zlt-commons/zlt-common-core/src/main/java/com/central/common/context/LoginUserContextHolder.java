package com.central.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.central.common.model.SysUser;

/**
 * 登录用户holder
 *
 * @author zlt
 * @date 2022/6/26
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class LoginUserContextHolder {
    private static final ThreadLocal<SysUser> CONTEXT = new TransmittableThreadLocal<>();

    public static void setUser(SysUser user) {
        CONTEXT.set(user);
    }

    public static SysUser getUser() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}