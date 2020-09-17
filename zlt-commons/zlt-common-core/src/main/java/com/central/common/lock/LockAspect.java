package com.central.common.lock;

import cn.hutool.core.util.StrUtil;
import com.central.common.exception.LockException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 分布式锁切面
 *
 * @author zlt
 * @date 2020/6/6
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@Aspect
public class LockAspect {
    @Autowired(required = false)
    private DistributedLock locker;

    /**
     * 用于SpEL表达式解析.
     */
    private SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@within(lock) || @annotation(lock)")
    public Object aroundLock(ProceedingJoinPoint point, Lock lock) throws Throwable {
        if (lock == null) {
            // 获取类上的注解
            lock = point.getTarget().getClass().getDeclaredAnnotation(Lock.class);
        }
        String lockKey = lock.key();
        if (locker == null) {
            throw new LockException("DistributedLock is null");
        }
        if (StrUtil.isEmpty(lockKey)) {
            throw new LockException("lockKey is null");
        }

        if (lockKey.contains("#")) {
            MethodSignature methodSignature = (MethodSignature)point.getSignature();
            //获取方法参数值
            Object[] args = point.getArgs();
            lockKey = getValBySpEL(lockKey, methodSignature, args);
        }
        ZLock lockObj = null;
        try {
            //加锁
            if (lock.waitTime() > 0) {
                lockObj = locker.tryLock(lockKey, lock.waitTime(), lock.leaseTime(), lock.unit(), lock.isFair());
            } else {
                lockObj = locker.lock(lockKey, lock.leaseTime(), lock.unit(), lock.isFair());
            }

            if (lockObj != null) {
                return point.proceed();
            } else {
                throw new LockException("锁等待超时");
            }
        } finally {
            locker.unlock(lockObj);
        }
    }

    /**
     * 解析spEL表达式
     */
    private String getValBySpEL(String spEL, MethodSignature methodSignature, Object[] args) {
        //获取方法形参名数组
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        if (paramNames != null && paramNames.length > 0) {
            Expression expression = spelExpressionParser.parseExpression(spEL);
            // spring的表达式上下文对象
            EvaluationContext context = new StandardEvaluationContext();
            // 给上下文赋值
            for(int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            return expression.getValue(context).toString();
        }
        return null;
    }
}
