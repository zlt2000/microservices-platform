package com.central.oauth2.common.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * RSA加密测试
 *
 * @author zlt
 * @version 1.0
 * @date 2023/1/30
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@RunWith(SpringRunner.class)
public class RsaUtilsTest {
    /**
     * 私钥
     */
    private final static String PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDq7iw/3r2Fk/sFQR4BcmWrnASZnYIhlTj7kkANjwi6bnmoZoTmpcTAxpX4zYcVZTg9lsd8f5EYQgzCOQBFGiT3eSsWcm67BOMvCDJRKwqbkzu/cSTzN2qzjvi/Wo7GoToGYfP1Al+CcC0VjHATJVGhDXb4gCGk7MNTevPTZVHTmQYKuAZTqzPEO2npxD670AIY/L+4p0hDoftoz+jEzI+XAr0ffyeLovFqLurnqqnoXWnt2vGHwJYlnePQ2JwHlk+c4y2sYvmV+eJ8cGMH4IMle/yR99jTPAvcn4uufIVukp414wlmnKShhbzk93P7xacT0oYwzcR454P9Bf858x0VAgMBAAECggEBAJxK94VGWi+T01wbhirQQHN6yFSqRPiyncY/9f0PO29MMAOosKIBhnP5qaxsj4HcZR4UQYLCG3VX+8T6xwMx8YXyRogYeTJSfhG8Ej2NtPDrcsRaMYrdQ09RvosPZA0hFclJQVOu0HumxVegpq8WFMhgfNW16KwgF5JiKfRpY5aw4NDWZFmdNExdymrWPheI8pWBq/U+l8oqrekhbiEKXM2UXmLlKTS9Nk46LRYwaaiDW8JEFuPdx0cnakb+ecCXGd/8Cc8Hxn/mLyvqS1cWHT3J+lXRkfcnNnrJTR9qhf9l077XBMJqVckFEpK4kKbXHZd957ISAxq48Tm+xMX9KAECgYEA995szPToU0BxTTtOD4Z4c5JvbURQEcDQl/dB+qCQnu/orW5ZWv4++lHq2SZPjzbt7M8OaoRN8A5zYcNrYe4kBhLOLqHdaS1yUGIIjAejEAJplR5GqI3T5qzaLjpyiUZpO0mOcwAazCevnSXH6uO5jP0sjwxwaXz4OsTcpHIXHqECgYEA8qMXhh+G88+vTELL/2dHhtTIf7IAJLQ37c1Xrm6uwOHPfiDr256Lc4EzF8QAQqlHoYm1jRK7xDfFymY/SJKJVhYehWlNilnMuDuDOqseC/Zn2KgjMSjLLVkbp25DcpAu6SSiWlBvemrV3jivX/MU0BFp8HjbrlUcl12lRtYgrfUCgYB+iN2iA6RWW597fbrr0gnLdgXMEgOODJBwA5l7CFzLxk1Ru/OBsCkWQJtTH2ueALyVF16UodXnpnjgf5Jh++AH+bGnvJn7B2hEAMe8NGnZ0mFz7nDDuyNhrvvyfYPa8EboLTS7IGKNtfTAlHjqQDaI8vW8UO1R7KoL1lOM33FOAQKBgQDt/Z6jReU+3CUbbiFeANWdoLSQ2+1cExEQxWsNgy8Rreux0WTG4/nwb3fIBc4jlJrYDZTwLMHTssjkv+muq1zd/ZAuV51g6LfutSEAuLseDLDLSBBMtbCkaFTBo1uw0U/SCsbcQy01K/leoMcUG//8HjiFUGZZ1s3WgloM4xbmyQKBgAipgnoEyzvUfe2OMOc5ARGNSGZG4JGTGCyfnrYvYfffWpAokklHOkZMumeSWJXkx5F+MgJd9fxBK9S57PZ09gWkoeSVg0xcz5QMjh8BswfCdyet/CXQtwIfK0Wf1gWdAxC6vvv3DQ7zXbTlvqMdVOFzCKlocCYkzWMvIsejWXnW";
    /**
     * 公钥
     */
    private final static String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6u4sP969hZP7BUEeAXJlq5wEmZ2CIZU4+5JADY8Ium55qGaE5qXEwMaV+M2HFWU4PZbHfH+RGEIMwjkARRok93krFnJuuwTjLwgyUSsKm5M7v3Ek8zdqs474v1qOxqE6BmHz9QJfgnAtFYxwEyVRoQ12+IAhpOzDU3rz02VR05kGCrgGU6szxDtp6cQ+u9ACGPy/uKdIQ6H7aM/oxMyPlwK9H38ni6Lxai7q56qp6F1p7drxh8CWJZ3j0NicB5ZPnOMtrGL5lfnifHBjB+CDJXv8kffY0zwL3J+LrnyFbpKeNeMJZpykoYW85Pdz+8WnE9KGMM3EeOeD/QX/OfMdFQIDAQAB";

    private static RSA rsaTools;

    @BeforeClass
    public static void init() {
        rsaTools = SecureUtil.rsa(PRIVATE_KEY, PUBLIC_KEY);
    }

    @Test
    public void testDecrypt() {
        String value = "admin";
        String result = rsaTools.encryptBcd(value, KeyType.PublicKey);
        System.out.println(result);
    }

    @Test
    public void testEncrypt() {
        String value = "8A806900A16294BF11F3D0455ADE2759CCC8C52F0BB5DEF731A0E5F596203401899B545B36E7072EC4CC1AA2FDE7481589783D555D36EDDDC19587D62B6AB39A6478FF75600F2B3671816D33C2AF6776F3865838EA2D90060E01C014B6F0DAE7D0D09EC80FF3F2ABFE5F12F111A72390A0083534789F49D9CA36CF39D071622B75C5B5F2BEC609F7C7EE53E324FA0443AC31BE40FF9B6693AE5BEAC0BFDC677794E7A5B503B3A3F571DD6AB5169598F5FC59D63EF60E572FFDF3CA2DCB818A2448C6E25CC46B17CBB232B0F46BC05C916EEB05E9C852B0729C3DE28C7FD679B89E370FB829ACB4485A7346E50AA06655A5A9DFDA5685145A6142A04ACF72D689";
        String result = rsaTools.decryptStr(value, KeyType.PrivateKey);
        System.out.println(result);
    }
}
