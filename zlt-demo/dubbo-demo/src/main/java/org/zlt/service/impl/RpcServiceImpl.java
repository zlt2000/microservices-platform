package org.zlt.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.zlt.service.RpcService;

/**
 * @author zlt
 * @date 2020/1/30
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@DubboService(protocol = "dubbo")
public class RpcServiceImpl implements RpcService {
    @Override
    public String test(String param) {
        log.info("==============RpcServiceImpl");
        return "dubbo service: " + param;
    }
}
