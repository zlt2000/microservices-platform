package com.central.common.passwordEncoder;

import cn.hutool.crypto.SmUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * SM3
 *
 * @author zlt
 * @version 1.0
 * @date 2022/12/29
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
public class SM3PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return SmUtil.sm3(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        }
        if (encodedPassword == null || encodedPassword.length() == 0) {
            log.warn("Empty encoded password");
            return false;
        }
        String rawPasswordEncoded = this.encode(rawPassword.toString());
        return rawPasswordEncoded.equals(encodedPassword);
    }
}
