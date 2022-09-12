package com.sso.demo.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.central.common.model.Result;
import com.central.common.utils.JsonUtil;
import com.central.common.utils.RsaUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

/**
 * @author zlt
 * @date 2020/5/22
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@RestController
public class ApiController {
    private static final String PUBKEY_START = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBKEY_END = "-----END PUBLIC KEY-----";

    @Value("${zlt.sso.client-id:}")
    private String clientId;

    @Value("${zlt.sso.client-secret:}")
    private String clientSecret;

    @Value("${zlt.sso.redirect-uri:}")
    private String redirectUri;

    @Value("${zlt.sso.access-token-uri:}")
    private String accessTokenUri;

    @Value("${zlt.sso.jwt-key-uri:}")
    private String jwtKeyUri;

    /**
     * 公钥
     */
    private static RSAPublicKey publicKey;

    /**
     * 模拟用户数据库
     */
    private static final Map<Long, MyUser> userDb = new HashMap<>();

    /**
     * nonce存储
     */
    private final static ThreadLocal<String> NONCE = new ThreadLocal<>();

    private final static Map<String, MyUser> localTokenMap = new HashMap<>();

    @GetMapping("/token/{code}")
    public Map<String, Object> tokenInfo(@PathVariable String code) throws Exception {
        //获取token
        Map<String, Object> tokenMap = getAccessToken(code);
        String idTokenStr = (String)tokenMap.get("id_token");
        //解析id_token
        JsonNode idToken = this.getIdTokenJson(idTokenStr);
        //检查id_token的有效性
        checkToken(idToken);
        //获取用户信息
        MyUser user = this.getUserInfo(idToken);
        //判断用户信息是否存在，否则注册用户信息
        if (!userDb.containsKey(user.getId())) {
            userDb.put(user.getId(), user);
        }
        String accessToken = (String)tokenMap.get("access_token");
        localTokenMap.put(accessToken, user);

        Map<String, Object> result = new HashMap<>(2);
        result.put("tokenInfo", tokenMap);
        result.put("userInfo", user);
        return result;
    }

    @GetMapping("/logoutNotify")
    public void logoutNotify(HttpServletRequest request) {
        String tokens = request.getParameter("tokens");
        log.info("=====logoutNotify: " + tokens);
        if (StrUtil.isNotEmpty(tokens)) {
            for (String accessToken : tokens.split(",")) {
                localTokenMap.remove(accessToken);
            }
        }
    }

    @GetMapping("/user")
    public MyUser user(HttpServletRequest request) {
        String token = request.getParameter("access_token");
        return localTokenMap.get(token);
    }

    /**
     * 检查 id_token 的有效性
     */
    private void checkToken(JsonNode idToken) {
        //token有效期
        long expiresAt = idToken.get("exp").asLong();
        long now = System.currentTimeMillis();
        Assert.isTrue((expiresAt > now), "id_token已过期");

        //应用id
        String aud = idToken.get("aud").asText();
        Assert.isTrue(clientId.equals(aud), "非法应用"+aud);

        //随机码
        String nonce = idToken.get("nonce").asText();
        Assert.isTrue((StrUtil.isEmpty(nonce) || nonce.equals(NONCE.get())), "nonce参数无效");
    }

    /**
     * 获取token
     */
    public Map<String, Object> getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String base64Auth = this.getBase64ClientParam();
        headers.add("Authorization", "Basic " + base64Auth);

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("code", code);
        param.add("grant_type", "authorization_code");
        param.add("redirect_uri", redirectUri);
        param.add("scope", "all");
        param.add("nonce", this.genNonce());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(accessTokenUri, request , Map.class);
        return response.getBody();
    }

    private String genNonce() {
        String nonce = RandomUtil.randomString(6);
        NONCE.set(nonce);
        return nonce;
    }

    /**
     * 把 id_token 字符串解析为json对象
     */
    public JsonNode getIdTokenJson(String idToken) throws Exception {
        RSAPublicKey publicKey = getPubKeyObj();
        return this.decodeAndVerify(idToken, publicKey);
    }

    /**
     * 通过 id_token 获取用户信息
     */
    public MyUser getUserInfo(JsonNode idToken) {
        MyUser user = new MyUser();
        user.setId(Long.valueOf(idToken.get("sub").textValue()));
        user.setName(idToken.get("name").textValue());
        user.setLoginName(idToken.get("login_name").textValue());
        user.setPicture(idToken.get("picture").textValue());
        return user;
    }

    private JsonNode decodeAndVerify(String jwtToken, RSAPublicKey rsaPublicKey) {
        SignatureVerifier rsaVerifier = new RsaVerifier(rsaPublicKey);
        Jwt jwt = JwtHelper.decodeAndVerify(jwtToken, rsaVerifier);
        return JsonUtil.parse(jwt.getClaims());
    }

    /**
     * 获取公钥
     */
    public RSAPublicKey getPubKeyObj() throws Exception {
        if (publicKey == null) {
            publicKey = getPubKeyByRemote();
        }
        return publicKey;
    }

    private RSAPublicKey getPubKeyByRemote() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String base64Auth = this.getBase64ClientParam();
        headers.add("Authorization", "Basic " + base64Auth);

        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Result> response = restTemplate.exchange(jwtKeyUri, HttpMethod.GET, request, Result.class);
        Result<String> result = response.getBody();
        Assert.isTrue((result.getResp_code() == 0), result.getResp_msg());

        String publicKeyStr = result.getResp_msg();
        publicKeyStr = publicKeyStr.substring(PUBKEY_START.length(), publicKeyStr.indexOf(PUBKEY_END));
        return RsaUtils.getPublicKey(publicKeyStr);
    }

    /**
     * base64加密应用参数
     */
    private String getBase64ClientParam() {
        byte[] authorization = (clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8);
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(authorization);
    }

    @Data
    public static class MyUser {
        private Long id;
        private String name;
        private String loginName;
        private String picture;
    }
}
