package com.central.log.service;

import com.central.log.model.Audit;

/**
 * 审计日志接口
 *
 * @author zlt
 * @date 2020/2/3
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public interface IAuditService {
    void save(Audit audit);
}
