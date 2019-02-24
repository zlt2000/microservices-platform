package com.central.user.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.central.common.model.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SysUserMapperTest单元测试用例
 *
 * @author zlt
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SysUserMapperTest {
	@Resource
	private SysUserMapper sysUserMapper;

	@Test
	public void testFindByUsername() {
		List<SysUser> users = sysUserMapper.selectList(
				new QueryWrapper<SysUser>().eq("username", "admin")
		);
		assertThat(users.size()).isGreaterThan(0);
	}
}
