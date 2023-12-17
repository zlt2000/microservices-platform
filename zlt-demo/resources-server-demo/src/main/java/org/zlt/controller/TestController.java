package org.zlt.controller;

import com.central.common.context.LoginUserContextHolder;
import com.central.common.utils.LoginUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zlt
 * @date 2022/6/25
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@RestController
public class TestController {
    /**
     * 通过 LoginUserUtils 获取当前登录人
     */
    @GetMapping("/test/auth")
    public String auth() {
        return "auth：" + LoginUserUtils.getCurrentUser(false).getUsername();
    }

    /**
     * 通过 LoginUserContextHolder 获取当前登录人
     */
    @GetMapping("/test/auth2")
    public String auth2() {
        return "auth2：" + LoginUserContextHolder.getUser().getUsername();
    }

    @GetMapping("/test/notAuth")
    public String notAuth() {
        return "notAuth：ok";
    }
}
