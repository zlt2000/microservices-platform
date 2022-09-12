package com.central.log.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 日志链路对象
 *
 * @author zlt
 * @version 1.0
 * @date 2022/1/27
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Setter
@Getter
public class TraceLog {
    private String spanId;
    private String parentId;
    private String appName;
    private String serverIp;
    private String serverPort;
}
