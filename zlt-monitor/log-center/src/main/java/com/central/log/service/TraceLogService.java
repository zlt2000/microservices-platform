package com.central.log.service;

import cn.hutool.core.util.StrUtil;
import com.central.common.utils.JsonUtil;
import com.central.log.model.TraceLog;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 日志链路service
 *
 * @author zlt
 * @version 1.0
 * @date 2022/1/27
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Service
public class TraceLogService {
    public List<TraceLog> transTraceLog(List<JsonNode> jsonNodeList) {
        List<TraceLog> logList = new ArrayList<>();
        Set<String> logSet = new HashSet<>();
        jsonNodeList.forEach(e -> {
            TraceLog log = JsonUtil.toObject(e, TraceLog.class);
            String spanId = log.getSpanId();
            if (StrUtil.isNotEmpty(spanId)) {
                if (spanId.length() == 1) {
                    log.setParentId("-1");
                } else {
                    log.setParentId(spanId.substring(0, spanId.length() - 2));
                }
                if (checkLog(genLogKey(log), logSet)) {
                    logList.add(log);
                }
            }
        });
        return logList;
    }

    /**
     * 通过集合来去重
     */
    private boolean checkLog(String logKey, Set<String> logSet) {
        if (logSet.contains(logKey)) {
            return false;
        } else {
            logSet.add(logKey);
            return true;
        }
    }

    private String genLogKey(TraceLog log) {
        return StrUtil.format("{}_{}_{}_{}", log.getAppName(), log.getSpanId(), log.getServerIp(), log.getServerPort());
    }
}
