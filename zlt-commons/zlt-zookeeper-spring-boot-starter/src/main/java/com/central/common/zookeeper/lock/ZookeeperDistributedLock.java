package com.central.common.zookeeper.lock;

import com.central.common.constant.CommonConstant;
import com.central.common.exception.LockException;
import com.central.common.lock.DistributedLock;
import com.central.common.lock.ZLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * zookeeper分布式锁实现
 *
 * @author zlt
 * @version 1.0
 * @date 2021/4/3
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Component
@ConditionalOnProperty(prefix = "zlt.lock", name = "lockerType", havingValue = "ZK")
public class ZookeeperDistributedLock implements DistributedLock {
    @Resource
    private CuratorFramework client;

    private ZLock getLock(String key) {
        InterProcessMutex lock = new InterProcessMutex(client, getPath(key));
        return new ZLock(lock, this);
    }

    @Override
    public ZLock lock(String key, long leaseTime, TimeUnit unit, boolean isFair) throws Exception {
        ZLock zLock = this.getLock(key);
        InterProcessMutex ipm = (InterProcessMutex)zLock.getLock();
        ipm.acquire();
        return zLock;
    }

    @Override
    public ZLock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit, boolean isFair) throws Exception {
        ZLock zLock = this.getLock(key);
        InterProcessMutex ipm = (InterProcessMutex)zLock.getLock();
        if (ipm.acquire(waitTime, unit)) {
            return zLock;
        }
        return null;
    }

    @Override
    public void unlock(Object lock) throws Exception {
        if (lock != null) {
            if (lock instanceof InterProcessMutex) {
                InterProcessMutex ipm = (InterProcessMutex)lock;
                if (ipm.isAcquiredInThisProcess()) {
                    ipm.release();
                }
            } else {
                throw new LockException("requires InterProcessMutex type");
            }
        }
    }

    private String getPath(String key) {
        return CommonConstant.PATH_SPLIT + CommonConstant.LOCK_KEY_PREFIX + CommonConstant.PATH_SPLIT + key;
    }
}
