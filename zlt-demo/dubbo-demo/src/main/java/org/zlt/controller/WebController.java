package org.zlt.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.zlt.service.RpcService;

/**
 * @author zlt
 * @date 2020/6/26
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@RestController
public class WebController {
    @DubboReference
    private RpcService dubboService;

    @GetMapping("/test/{p}")
    public String test(@PathVariable("p") String param) {
        log.info("==============WebController");
        return dubboService.test(param);
    }
}
