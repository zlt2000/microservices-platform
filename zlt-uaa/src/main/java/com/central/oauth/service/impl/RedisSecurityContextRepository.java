package com.central.oauth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.central.common.constant.SecurityConstants;
import com.central.oauth.model.SupplierDeferredSecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 基于redis存储认证信息，解决多节点uaa单点登录信息互通
 *
 * @author: zlt
 * @date: 2023/12/06
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Component
@RequiredArgsConstructor
public class RedisSecurityContextRepository implements SecurityContextRepository {
    /**
     * 认证信息存储前缀
     */
    public static final String SECURITY_CONTEXT_PREFIX_KEY = "security_context:";
    /**
     * 随机字符串请求头名字
     */
    public static final String NONCE_HEADER_NAME = "nonceId";

    private final static SerializationCodec AUTH_CODEC = new SerializationCodec();

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();
    private final RedissonClient redissonClient;

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        throw new UnsupportedOperationException("Method deprecated.");
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        String nonce = getNonce(request);
        if (StrUtil.isEmpty(nonce)) {
            return;
        }

        // 如果当前的context是空的，则移除
        SecurityContext emptyContext = this.securityContextHolderStrategy.createEmptyContext();
        RBucket<SecurityContext> rBucket = this.getBucket(nonce);
        if (emptyContext.equals(context)) {
            rBucket.delete();
        } else {
            // 保存认证信息
            rBucket.set(context, Duration.ofSeconds(SecurityConstants.ACCESS_TOKEN_VALIDITY_SECONDS));
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String nonce = getNonce(request);
        if (StrUtil.isEmpty(nonce)) {
            return false;
        }
        // 检验当前请求是否有认证信息
        return this.getBucket(nonce).get() != null;
    }

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        Supplier<SecurityContext> supplier = () -> readSecurityContextFromRedis(request);
        return new SupplierDeferredSecurityContext(supplier, this.securityContextHolderStrategy);
    }

    /**
     * 从redis中获取认证信息
     *
     * @param request 当前请求
     * @return 认证信息
     */
    private SecurityContext readSecurityContextFromRedis(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String nonce = getNonce(request);
        if (StrUtil.isEmpty(nonce)) {
            return null;
        }
        // 根据缓存id获取认证信息
        RBucket<SecurityContext> rBucket = this.getBucket(nonce);
        SecurityContext context = rBucket.get();
        if (context != null) {
            rBucket.expire(Duration.ofSeconds(SecurityConstants.ACCESS_TOKEN_VALIDITY_SECONDS));
        }
        return context;
    }

    /**
     * 先从请求头中找，找不到去请求参数中找，找不到获取当前session的id
     *  2023-07-11新增逻辑：获取当前session的sessionId
     *
     * @param request 当前请求
     * @return 随机字符串(sessionId)，这个字符串本来是前端生成，现在改为后端获取的sessionId
     */
    private String getNonce(HttpServletRequest request) {
        String nonce = request.getHeader(NONCE_HEADER_NAME);
        if (StrUtil.isEmpty(nonce)) {
            nonce = request.getParameter(NONCE_HEADER_NAME);
            if (StrUtil.isEmpty(nonce)) {
                HttpSession session = request.getSession(Boolean.FALSE);
                if (session != null) {
                    nonce = session.getId();
                }
            }
        }
        return nonce;
    }

    private RBucket<SecurityContext> getBucket(String nonce) {
        return redissonClient.getBucket(SECURITY_CONTEXT_PREFIX_KEY + nonce, AUTH_CODEC);
    }

}
