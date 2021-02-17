package com.central.oss.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * aws s3协议配置
 *
 * @author zlt
 * @date 2021/2/11
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Setter
@Getter
public class S3Properties {
    /**
     * 用户名
     */
    private String accessKey;
    /**
     * 密码
     */
    private String accessKeySecret;
    /**
     * 访问端点
     */
    private String endpoint;
    /**
     * bucket名称
     */
    private String bucketName;
    /**
     * 区域
     */
    private String region;
    /**
     * path-style
     */
    private Boolean pathStyleAccessEnabled = true;
}
