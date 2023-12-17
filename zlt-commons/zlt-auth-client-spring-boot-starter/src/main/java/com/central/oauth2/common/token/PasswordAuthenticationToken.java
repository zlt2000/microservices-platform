package com.central.oauth2.common.token;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Collection;

/**
 * @author: zlt
 * @date: 2023/11/18
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Getter
public class PasswordAuthenticationToken extends BaseAuthenticationToken {
    public static final AuthorizationGrantType GRANT_TYPE = new  AuthorizationGrantType("password");
    private final Object principal;
    private final String credentials;

    public PasswordAuthenticationToken(String username, String password) {
        super(GRANT_TYPE);
        this.principal = username;
        this.credentials = password;
        super.setAuthenticated(true);
    }

    public PasswordAuthenticationToken(UserDetails user, String password, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = user;
        this.credentials = password;
    }
}
