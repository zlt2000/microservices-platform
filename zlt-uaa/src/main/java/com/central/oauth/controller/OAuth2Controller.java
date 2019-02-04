package com.central.oauth.controller;

import com.central.common.annotation.LoginUser;
import com.central.common.constant.SecurityConstants;
import com.central.common.feign.UserService;
import com.central.common.model.LoginAppUser;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.common.utils.SpringUtil;
import com.central.oauth.mobile.MobileAuthenticationToken;
import com.central.oauth.openid.OpenIdAuthenticationToken;
import com.central.oauth.service.impl.RedisClientDetailsService;
import com.central.oauth2.common.util.AuthUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2相关操作
 *
 * @author zlt
 */
@Api(tags = "OAuth2相关操作")
@Slf4j
@RestController
public class OAuth2Controller {
    @Resource
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Resource
    private UserService userService;

    @GetMapping("/oauth/test")
    public SysUser testNacos() {
        return userService.selectByUsername("admin");
    }

    @GetMapping("/oauth/test2")
    public String testNacos2() throws InterruptedException {
        Thread.sleep(5000);
        return "success";
    }

    @ApiOperation(value = "用户名密码获取token")
    @PostMapping(SecurityConstants.PASSWORD_LOGIN_PRO_URL)
    public void getUserTokenInfo(
            @ApiParam(required = true, name = "username", value = "账号") String username,
            @ApiParam(required = true, name = "password", value = "密码") String password,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        writerToken(request, response, token, "用户名或密码错误");
    }

    @ApiOperation(value = "openId获取token")
    @PostMapping(SecurityConstants.OPENID_TOKEN_URL)
    public void getTokenByOpenId(
            @ApiParam(required = true, name = "openId", value = "openId") String openId,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        OpenIdAuthenticationToken token = new OpenIdAuthenticationToken(openId);
        writerToken(request, response, token, "openId错误");
    }

    @ApiOperation(value = "mobile获取token")
    @PostMapping(SecurityConstants.MOBILE_TOKEN_URL)
    public void getTokenByMobile(
            @ApiParam(required = true, name = "mobile", value = "mobile") String mobile,
            @ApiParam(required = true, name = "password", value = "密码") String password,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        MobileAuthenticationToken token = new MobileAuthenticationToken(mobile, password);
        writerToken(request, response, token, "手机号或密码错误");
    }

    /**
     * 当前登陆用户信息
     * security获取当前登录用户的方法是SecurityContextHolder.getContext().getAuthentication()
     * 这里的实现类是org.springframework.security.oauth2.provider.OAuth2Authentication
     *
     * @return
     */
    @ApiOperation(value = "当前登陆用户信息")
    @GetMapping(value = {"/oauth/userinfo"}, produces = "application/json") // 获取用户信息。/auth/user
    public Map<String, Object> getCurrentUserDetail(@LoginUser SysUser user) {
        Map<String, Object> userInfo = new HashMap<>();
        if (user != null) {
            LoginAppUser loginAppUser = userService.findByUsername(user.getUsername());
            userInfo.put("user", loginAppUser);
            userInfo.put("permissions", loginAppUser.getPermissions());
            userInfo.put("resp_code", "200");
        }
        return userInfo;
    }

    private void writerToken(HttpServletRequest request, HttpServletResponse response, AbstractAuthenticationToken token
            , String badCredenbtialsMsg) throws IOException {
        try {
            final String[] clientInfos = AuthUtils.extractClient(request);
            String clientId = clientInfos[0];
            String clientSecret = clientInfos[1];

            ClientDetails clientDetails = getClient(clientId, clientSecret, null);
            TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_MAP, clientId, clientDetails.getScope(), "customer");
            OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
            OAuth2AccessToken oAuth2AccessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
            oAuth2Authentication.setAuthenticated(true);
            writerObj(response, oAuth2AccessToken);
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            exceptionHandler(response, badCredenbtialsMsg);
        } catch (Exception e) {
            exceptionHandler(response, e);
        }
    }

    private void exceptionHandler(HttpServletResponse response, Exception e) throws IOException {
        log.error("exceptionHandler-error:", e);
        exceptionHandler(response, e.getMessage());
    }

    private void exceptionHandler(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        writerObj(response, Result.failed(msg));
    }

    private void writerObj(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try (
                Writer writer = response.getWriter()
        ) {
            writer.write(objectMapper.writeValueAsString(obj));
            writer.flush();
        }
    }

    private ClientDetails getClient(String clientId, String clientSecret, RedisClientDetailsService clientDetailsService) {
        if (clientDetailsService == null) {
            clientDetailsService = SpringUtil.getBean(RedisClientDetailsService.class);
        }
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

        if (clientDetails == null) {
            throw new UnapprovedClientAuthenticationException("clientId对应的信息不存在");
        } else if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
            throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
        }
        return clientDetails;
    }
}
