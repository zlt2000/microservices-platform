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
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Getter
public class OpenIdAuthenticationToken extends BaseAuthenticationToken {
	public static final AuthorizationGrantType GRANT_TYPE = new  AuthorizationGrantType("openId");
	private final Object principal;

	public OpenIdAuthenticationToken(String openId) {
		super(GRANT_TYPE);
		this.principal = openId;
		super.setAuthenticated(true);
	}

	public OpenIdAuthenticationToken(UserDetails principal, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
	}
}
