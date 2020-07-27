package com.central.oauth2.common.token;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 增加租户id，解决不同租户单点登录时角色没变化
 *
 * @author zlt
 * @date 2020/6/10
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class TenantUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private static final long serialVersionUID = -5638287853803374687L;

    /**
     * 租户id
     */
    @Getter
    private final String clientId;

    public TenantUsernamePasswordAuthenticationToken(Object principal, Object credentials, String clientId) {
        super(principal, credentials);
        this.clientId = clientId;
    }

    public TenantUsernamePasswordAuthenticationToken(Object principal, Object credentials,
                                                     Collection<? extends GrantedAuthority> authorities, String clientId) {
        super(principal, credentials, authorities);
        this.clientId = clientId;
    }
}
