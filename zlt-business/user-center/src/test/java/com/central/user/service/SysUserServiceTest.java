package com.central.user.service;

import com.central.common.lock.DistributedLock;
import com.central.common.model.LoginAppUser;
import static org.assertj.core.api.Assertions.*;

import com.central.common.model.PageResult;
import com.central.common.model.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * SysUserServiceTest单元测试用例
 *
 * @author zlt
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SysUserServiceTest {
	@Autowired
	private ISysUserService sysUserService;

	@Autowired
	private DistributedLock locker;

	@Test
	public void testLock() throws Exception {
		Object lock = null;
		try {
			lock = locker.tryLock("test", 1000, TimeUnit.MILLISECONDS, true);
			Thread.sleep(5000);
		} finally {
			locker.unlock(lock);
		}
	}

	@Test
	public void testFindByUsername() {
		LoginAppUser loginAppUser = sysUserService.findByUsername("admin");
		assertThat(loginAppUser).isNotNull();
	}

	@Test
	public void testFindUsers() {
		Map<String, Object> params = new HashMap<>(2);
		params.put("page", 1);
		params.put("limit", 10);
		PageResult<SysUser> page = sysUserService.findUsers(params);
		assertThat(page).isNotNull();
		assertThat(page.getCount()).isGreaterThan(0);
	}
}
