package com.central.oauth2.common.util;

import com.central.common.constant.SecurityConstants;
import com.central.common.utils.JsonUtil;
import com.central.common.utils.RsaUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.stream.Collectors;

/**
 * jwt工具类
 *
 * @author zlt
 * @date 2019/7/21
 */
public class JwtUtils {
    private static final String PUBKEY_START = "-----BEGIN PUBLIC KEY-----";
    private static final String PUBKEY_END = "-----END PUBLIC KEY-----";

    /**
     * 通过classpath获取公钥值
     */
    public static RSAPublicKey getPubKeyObj() {
        Resource res = new ClassPathResource(SecurityConstants.RSA_PUBLIC_KEY);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(res.getInputStream()))) {
            String pubKey = br.lines().collect(Collectors.joining("\n"));
            pubKey = pubKey.substring(PUBKEY_START.length(), pubKey.indexOf(PUBKEY_END));
            return RsaUtils.getPublicKey(pubKey);
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    /**
     * {"exp":1563256084,"user_name":"admin","authorities":["ADMIN"],"jti":"4ce02f54-3d1c-4461-8af1-73f0841a35df","client_id":"webApp","scope":["app"]}
     * @param jwtToken token值
     * @param rsaPublicKey 公钥
     * @return
     */
    public static JsonNode decodeAndVerify(String jwtToken, RSAPublicKey rsaPublicKey) {
        SignatureVerifier rsaVerifier = new RsaVerifier(rsaPublicKey);
        Jwt jwt = JwtHelper.decodeAndVerify(jwtToken, rsaVerifier);
        return JsonUtil.parse(jwt.getClaims());
    }

    /**
     * {"exp":1563256084,"user_name":"admin","authorities":["ADMIN"],"jti":"4ce02f54-3d1c-4461-8af1-73f0841a35df","client_id":"webApp","scope":["app"]}
     * @param jwtToken token值
     * @return
     */
    public static JsonNode decodeAndVerify(String jwtToken) {
        return decodeAndVerify(jwtToken, getPubKeyObj());
    }

    /**
     * 判断jwt是否过期
     * @param claims jwt内容
     * @param currTime 当前时间
     * @return 未过期：true，已过期：false
     */
    public static boolean checkExp(JsonNode claims, long currTime) {
        long exp = claims.get("exp").asLong();
        if (exp < currTime) {
            return false;
        }
        return true;
    }

    /**
     * 判断jwt是否过期
     * @param claims jwt内容
     * @return 未过期：true，已过期：false
     */
    public static boolean checkExp(JsonNode claims) {
        return checkExp(claims, System.currentTimeMillis());
    }

    public static Jwt encode(CharSequence content, KeyProperties keyProperties) {
        KeyPair keyPair = new KeyStoreKeyFactory(
                    keyProperties.getKeyStore().getLocation(),
                    keyProperties.getKeyStore().getSecret().toCharArray())
                .getKeyPair(keyProperties.getKeyStore().getAlias());
        PrivateKey privateKey = keyPair.getPrivate();
        Signer rsaSigner = new RsaSigner((RSAPrivateKey) privateKey);
        return JwtHelper.encode(content, rsaSigner);
    }

    public static String encodeStr(CharSequence content, KeyProperties keyProperties) {
        return encode(content, keyProperties).getEncoded();
    }
}
