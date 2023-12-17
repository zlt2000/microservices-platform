package com.central.oauth2.common.component;

import com.central.oauth2.common.properties.PermitProperties;
import com.central.oauth2.common.properties.SecurityProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实现排除鉴权的地址即使参数带 access_token 也不会进行鉴权
 *
 * @author zlt
 * @version 1.0
 * @date 2023/12/5
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class CustomServerBearerTokenAuthConverter implements ServerAuthenticationConverter {
    private final PathMatcher pathMatcher = new AntPathMatcher();

    private static final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$",
            Pattern.CASE_INSENSITIVE);

    private boolean allowUriQueryParameter = false;

    private String bearerTokenHeaderName = HttpHeaders.AUTHORIZATION;

    private final PermitProperties permitProperties;

    public CustomServerBearerTokenAuthConverter(SecurityProperties securityProperties) {
        this.permitProperties = securityProperties.getIgnore();
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> token(exchange.getRequest())).map((token) -> {
            if (token.isEmpty()) {
                BearerTokenError error = invalidTokenError();
                throw new OAuth2AuthenticationException(error);
            }
            return new BearerTokenAuthenticationToken(token);
        });
    }

    private String token(ServerHttpRequest request) {
        boolean match = Arrays.stream(permitProperties.getUrls())
                .anyMatch(url -> pathMatcher.match(url, request.getURI().getPath()));

        if (match) {
            return null;
        }

        String authorizationHeaderToken = resolveFromAuthorizationHeader(request.getHeaders());
        String parameterToken = resolveAccessTokenFromRequest(request);

        if (authorizationHeaderToken != null) {
            if (parameterToken != null) {
                BearerTokenError error = BearerTokenErrors
                        .invalidRequest("Found multiple bearer tokens in the request");
                throw new OAuth2AuthenticationException(error);
            }
            return authorizationHeaderToken;
        }
        if (parameterToken != null && isParameterTokenSupportedForRequest(request)) {
            return parameterToken;
        }
        return null;
    }

    private static String resolveAccessTokenFromRequest(ServerHttpRequest request) {
        List<String> parameterTokens = request.getQueryParams().get("access_token");
        if (CollectionUtils.isEmpty(parameterTokens)) {
            return null;
        }
        if (parameterTokens.size() == 1) {
            return parameterTokens.get(0);
        }

        BearerTokenError error = BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request");
        throw new OAuth2AuthenticationException(error);

    }

    /**
     * Set if transport of access token using URI query parameter is supported. Defaults
     * to {@code false}.
     *
     * The spec recommends against using this mechanism for sending bearer tokens, and
     * even goes as far as stating that it was only included for completeness.
     * @param allowUriQueryParameter if the URI query parameter is supported
     */
    public void setAllowUriQueryParameter(boolean allowUriQueryParameter) {
        this.allowUriQueryParameter = allowUriQueryParameter;
    }

    /**
     * Set this value to configure what header is checked when resolving a Bearer Token.
     * This value is defaulted to {@link HttpHeaders#AUTHORIZATION}.
     *
     * This allows other headers to be used as the Bearer Token source such as
     * {@link HttpHeaders#PROXY_AUTHORIZATION}
     * @param bearerTokenHeaderName the header to check when retrieving the Bearer Token.
     * @since 5.4
     */
    public void setBearerTokenHeaderName(String bearerTokenHeaderName) {
        this.bearerTokenHeaderName = bearerTokenHeaderName;
    }

    private String resolveFromAuthorizationHeader(HttpHeaders headers) {
        String authorization = headers.getFirst(this.bearerTokenHeaderName);
        if (!StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
            return null;
        }
        Matcher matcher = authorizationPattern.matcher(authorization);
        if (!matcher.matches()) {
            BearerTokenError error = invalidTokenError();
            throw new OAuth2AuthenticationException(error);
        }
        return matcher.group("token");
    }

    private static BearerTokenError invalidTokenError() {
        return BearerTokenErrors.invalidToken("Bearer token is malformed");
    }

    private boolean isParameterTokenSupportedForRequest(ServerHttpRequest request) {
        return this.allowUriQueryParameter && HttpMethod.GET.equals(request.getMethod());
    }
}
