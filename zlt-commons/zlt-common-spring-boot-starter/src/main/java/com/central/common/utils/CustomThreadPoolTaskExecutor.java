package com.central.common.utils;

import cn.hutool.core.util.StrUtil;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 这是{@link ThreadPoolTaskExecutor}的一个简单替换，可以在每个任务之前设置子线程的租户和MDC数据
 *
 * @author zlt
 * @date 2019/8/14
 */
public class CustomThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    /**
     * 把父线程的租户和MDC内容赋值给子线程
     * @param runnable
     */
    @Override
    public void execute(Runnable runnable) {
        String tenantId = TenantContextHolder.getTenant();
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        super.execute(() -> run(runnable, tenantId, mdcContext));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        String tenantId = TenantContextHolder.getTenant();
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        return super.submit(() -> call(task, tenantId, mdcContext));
    }

    @Override
    public Future<?> submit(Runnable task) {
        String tenantId = TenantContextHolder.getTenant();
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        return super.submit(() -> run(task, tenantId, mdcContext));
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        String tenantId = TenantContextHolder.getTenant();
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        return super.submitListenable(() -> run(task, tenantId, mdcContext));
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        String tenantId = TenantContextHolder.getTenant();
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        return super.submitListenable(() -> call(task, tenantId, mdcContext));
    }

    /**
     * 子线程委托的执行方法
     * @param runnable {@link Runnable}
     * @param tenantId 租户id
     * @param mdcContext 父线程MDC内容
     */
    private void run(Runnable runnable, String tenantId, Map<String, String> mdcContext) {
        // 将父线程的租户id传给子线程
        if (StrUtil.isNotEmpty(tenantId)) {
            TenantContextHolder.setTenant(tenantId);
        }
        // 将父线程的MDC内容传给子线程
        if (mdcContext != null) {
            MDC.setContextMap(mdcContext);
        }
        try {
            // 执行异步操作
            runnable.run();
        } finally {
            // 清空租户内容
            TenantContextHolder.clear();
            // 清空MDC内容
            MDC.clear();
        }
    }

    /**
     * 子线程委托的执行方法
     * @param task {@link Callable}
     * @param tenantId 租户id
     * @param mdcContext 父线程MDC内容
     */
    private <T> T call(Callable<T> task, String tenantId, Map<String, String> mdcContext) throws Exception {
        // 将父线程的租户id传给子线程
        if (StrUtil.isNotEmpty(tenantId)) {
            TenantContextHolder.setTenant(tenantId);
        }
        // 将父线程的MDC内容传给子线程
        if (mdcContext != null) {
            MDC.setContextMap(mdcContext);
        }
        try {
            // 执行异步操作
            return task.call();
        } finally {
            // 清空租户内容
            TenantContextHolder.clear();
            // 清空MDC内容
            MDC.clear();
        }
    }
}
