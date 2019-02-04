package com.central.user.service.impl;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.central.common.service.impl.SuperServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.central.common.model.SysMenu;
import com.central.user.mapper.SysMenuMapper;
import com.central.user.mapper.SysRoleMenuMapper;
import com.central.user.service.ISysMenuService;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author zlt
 */
@Slf4j
@Service
public class SysMenuServiceImpl extends SuperServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {
 	@Resource
	private SysRoleMenuMapper sysRoleMenuMapper;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void setMenuToRole(Long roleId, Set<Long> menuIds) {
		sysRoleMenuMapper.delete(roleId, null);

		if (!CollectionUtils.isEmpty(menuIds)) {
			menuIds.forEach(menuId -> sysRoleMenuMapper.save(roleId, menuId));
		}
	}

	/**
	 * 角色菜单列表
	 * @param roleIds
	 * @return
	 */
	@Override
	public List<SysMenu> findByRoles(Set<Long> roleIds) {
		return sysRoleMenuMapper.findMenusByRoleIds(roleIds, null);
	}

	/**
	 * 角色菜单列表
	 * @param roleIds 角色ids
	 * @param roleIds 是否菜单
	 * @return
	 */
	@Override
	public List<SysMenu> findByRoles(Set<Long> roleIds, Integer type) {
		return sysRoleMenuMapper.findMenusByRoleIds(roleIds, type);
	}

	@Override
	public List<SysMenu> findByRoleCodes(Set<String> roleCodes, Integer type) {
		return sysRoleMenuMapper.findMenusByRoleCodes(roleCodes, type);
	}

    /**
     * 查询所有菜单
     */
	@Override
	public List<SysMenu> findAll() {
		return baseMapper.selectList(
                new QueryWrapper<SysMenu>().orderByAsc("sort")
        );
	}

    /**
     * 查询所有一级菜单
     */
	@Override
	public List<SysMenu> findOnes() {
        return baseMapper.selectList(
                new QueryWrapper<SysMenu>()
                        .eq("type", 1)
                        .orderByAsc("sort")
        );
	}
}
