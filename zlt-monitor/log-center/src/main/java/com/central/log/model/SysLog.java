package com.central.log.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

/**
 * 系统日志对象
 *
 * @author zlt
 */
@Data
@Document(indexName = "sys-log-*", type = "doc")
public class SysLog {
    @Id
    private String id;
    private Date timestamp;
    private String message;
    private String threadName;
    private String serverPort;
    private String serverIp;
    private String logLevel;
    private String appName;
    private String classname;
}