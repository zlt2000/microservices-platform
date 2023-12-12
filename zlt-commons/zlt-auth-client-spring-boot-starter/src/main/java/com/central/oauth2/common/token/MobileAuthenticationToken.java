package com.central.oauth2.common.token;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Collection;

/**
 * @author: zlt
 * @date: 2023/11/10
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Getter
public class MobileAuthenticationToken extends BaseAuthenticationToken {
	public static final AuthorizationGrantType GRANT_TYPE = new  AuthorizationGrantType("mobile_password");

	private final Object principal;
	private final String credentials;

	public MobileAuthenticationToken(String mobile, String password) {
		super(GRANT_TYPE);
		this.principal = mobile;
		this.credentials = password;
		super.setAuthenticated(true);
	}

	public MobileAuthenticationToken(UserDetails user, String password, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = user;
		this.credentials = password;
	}
}
