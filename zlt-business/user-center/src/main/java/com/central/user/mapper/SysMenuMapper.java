package com.central.user.mapper;

import com.central.db.mapper.SuperMapper;
import com.central.common.model.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单
 *
 * @author zlt
 */
@Mapper
public interface SysMenuMapper extends SuperMapper<SysMenu> {
    List<SysMenu> findByUserId(@Param("userId") Long userId, @Param("type") Integer type);
}
