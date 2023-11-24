package com.central.oauth2.common.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.util.SpringAuthorizationServerVersion;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author: zlt
 * @date: 2023/11/15
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Getter
@Setter
public class BaseAuthenticationToken extends AbstractAuthenticationToken {
	@Serial
	private static final long serialVersionUID = SpringAuthorizationServerVersion.SERIAL_VERSION_UID;
	private final AuthorizationGrantType grantType;
	private Authentication clientPrincipal;
	private Map<String, Object> additionalParameters;
	private Set<String> scopes;

	public BaseAuthenticationToken(AuthorizationGrantType authorizationGrantType) {
		super(null);
		this.grantType = authorizationGrantType;
	}

	public BaseAuthenticationToken(AuthorizationGrantType authorizationGrantType, Set<String> scopes
			, Authentication clientPrincipal, @Nullable Map<String, Object> additionalParameters) {
		super(null);
		this.grantType = authorizationGrantType;
		this.scopes = Collections.unmodifiableSet(scopes != null ? scopes : Collections.emptySet());
		this.clientPrincipal = clientPrincipal;
		this.additionalParameters = additionalParameters;
	}

	public BaseAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.grantType = null;
		this.clientPrincipal = null;
		this.additionalParameters = null;
		this.scopes = null;
	}

	@Override
	public Object getPrincipal() {
		return this.clientPrincipal;
	}

	@Override
	public Object getCredentials() {
		return "";
	}
}
