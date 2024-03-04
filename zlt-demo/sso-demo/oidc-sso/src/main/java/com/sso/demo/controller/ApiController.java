package com.sso.demo.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.central.common.utils.JsonUtil;
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
import org.apache.commons.codec.binary.Base64;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

/**
 * @author zlt
 * @date 2020/5/22
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@RestController
public class ApiController {
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
    private static final Map<String, String> userDb = new HashMap<>();

    private final static Map<String, String> localTokenMap = new HashMap<>();

    @GetMapping("/token/{code}")
    public Map<String, Object> tokenInfo(@PathVariable String code) throws Exception {
        //获取token
        Map<String, Object> tokenMap = getAccessToken(code);
        Map datasMap = (Map)tokenMap.get("datas");
        String idTokenStr = (String)datasMap.get("id_token");
        //解析id_token
        JsonNode idToken = this.getIdTokenJson(idTokenStr);
        //检查id_token的有效性
        checkToken(idToken);
        //获取用户信息
        String username = idToken.get("sub").asText();
        //判断用户信息是否存在，否则注册用户信息
        if (!userDb.containsKey(username)) {
            userDb.put(username, username);
        }
        String accessToken = (String)datasMap.get("access_token");
        localTokenMap.put(accessToken, username);

        Map<String, Object> result = new HashMap<>(2);
        result.put("tokenInfo", datasMap);
        result.put("userInfo", username);
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
    public String user(HttpServletRequest request) {
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
        //Assert.isTrue((expiresAt > now), "id_token已过期");

        //应用id
        String aud = idToken.get("aud").asText();
        Assert.isTrue(clientId.equals(aud), "非法应用"+aud);
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
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(accessTokenUri, request , Map.class);
        return response.getBody();
    }

    /**
     * 把 id_token 字符串解析为json对象
     */
    public JsonNode getIdTokenJson(String idToken) throws Exception {
        RSAPublicKey publicKey = getPubKeyObj();
        return this.decodeAndVerify(idToken, publicKey);
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
        ResponseEntity<JSONObject> response = restTemplate.exchange(jwtKeyUri, HttpMethod.GET, request, JSONObject.class);
        JSONObject result = response.getBody();
        JSONArray keys = (JSONArray) result.get("keys");
        JSONObject keyObj = (JSONObject) keys.get(0);
        String e = (String) keyObj.get("e");
        String n = (String) keyObj.get("n");
        return this.getKeyInstance(n, e);
    }

    private static RSAPublicKey getKeyInstance(String n, String e) throws InvalidKeySpecException, NoSuchAlgorithmException {
        // 将 Base64 编码的字符串解码为字节数组
        byte[] modulus = Base64.decodeBase64(n);
        byte[] exponent = Base64.decodeBase64(e);
        // 创建 RSA 公钥规范
        RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(1, modulus), new BigInteger(1, exponent));
        // 获取 RSA 密钥工厂
        KeyFactory kf = KeyFactory.getInstance("RSA");
        // 生成公钥
        return (RSAPublicKey)kf.generatePublic(spec);
    }

    /**
     * base64加密应用参数
     */
    private String getBase64ClientParam() {
        byte[] authorization = (clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8);
        return Base64.encodeBase64String(authorization);
    }

    @Data
    public static class MyUser {
        private Long id;
        private String name;
        private String loginName;
        private String picture;
    }
}
