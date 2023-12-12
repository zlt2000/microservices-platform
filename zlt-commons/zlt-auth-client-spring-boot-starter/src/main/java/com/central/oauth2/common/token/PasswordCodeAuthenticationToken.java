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
public class PasswordCodeAuthenticationToken extends BaseAuthenticationToken {
    public static final AuthorizationGrantType GRANT_TYPE = new  AuthorizationGrantType("password_code");
    private final Object principal;
    private final String credentials;
    private final String deviceId;
    private final String validCode;

    public PasswordCodeAuthenticationToken(String username, String password, String deviceId, String validCode) {
        super(GRANT_TYPE);
        this.principal = username;
        this.credentials = password;
        this.deviceId = deviceId;
        this.validCode = validCode;
        super.setAuthenticated(true);
    }

    public PasswordCodeAuthenticationToken(UserDetails user, String password, String deviceId
            , Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = user;
        this.credentials = password;
        this.deviceId = deviceId;
        this.validCode = null;
    }
}
