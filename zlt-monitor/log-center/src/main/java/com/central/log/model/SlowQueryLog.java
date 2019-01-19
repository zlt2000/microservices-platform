package com.central.log.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

/**
 * 慢查询日志对象
 *
 * @author zlt
 */
@Data
@Document(indexName = "mysql-slowlog-*", type = "doc")
public class SlowQueryLog {
    @Id
    private String id;
    private Date timestamp;
    private String query_str;
    private String user;
    private String clientip;
    private Float query_time;
    private Float lock_time;
    private Long rows_sent;
    private Long rows_examined;
}