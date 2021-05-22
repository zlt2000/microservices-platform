package com.central.oauth2.common.constants;

/**
 * id_token属性名常量
 *
 * @author zlt
 * @version 1.0
 * @date 2021/4/23
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class IdTokenClaimNames {
    /**
     * {@code iss} - the Issuer identifier
     */
    public final static String ISS = "iss";

    /**
     * {@code sub} - the Subject identifier
     */
    public final static String SUB = "sub";

    /**
     * {@code aud} - the Audience(s) that the ID Token is intended for
     */
    public final static String AUD = "aud";

    /**
     * {@code exp} - the Expiration time on or after which the ID Token MUST NOT be accepted
     */
    public final static String EXP = "exp";

    /**
     * {@code iat} - the time at which the ID Token was issued
     */
    public final static String IAT = "iat";

    /**
     * {@code auth_time} - the time when the End-User authentication occurred
     */
    public final static String AUTH_TIME = "auth_time";

    /**
     * {@code nonce} - a {@code String} value used to associate a Client session with an ID Token,
     * and to mitigate replay attacks.
     */
    public final static String NONCE = "nonce";

    /**
     * {@code acr} - the Authentication Context Class Reference
     */
    public final static String ACR = "acr";

    /**
     * {@code amr} - the Authentication Methods References
     */
    public final static String AMR = "amr";

    /**
     * {@code azp} - the Authorized party to which the ID Token was issued
     */
    public final static String AZP = "azp";

    /**
     * {@code at_hash} - the Access Token hash value
     */
    public final static String AT_HASH = "at_hash";

    /**
     * {@code c_hash} - the Authorization Code hash value
     */
    public final static String C_HASH = "c_hash";

    /**
     * {@code name} - 用户姓名
     */
    public final static String NAME = "name";

    /**
     * {@code login_name} - 登录名
     */
    public final static String L_NAME = "login_name";

    /**
     * {@code picture} - 头像照片
     */
    public final static String PIC = "picture";
}
