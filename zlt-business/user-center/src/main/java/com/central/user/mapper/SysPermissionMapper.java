package com.central.user.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.central.common.model.SysPermission;

/**
* @author zlt
 * 权限
 */
@Mapper
public interface SysPermissionMapper {

	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Insert("insert into sys_permission(permission, name, createTime, updateTime) values(#{permission}, #{name}, #{createTime}, #{createTime})")
	int save(SysPermission sysPermission);

	@Update("update sys_permission t set t.name = #{name}, t.permission = #{permission}, t.updateTime = #{updateTime} where t.id = #{id}")
	int update(SysPermission sysPermission);

	@Delete("delete from sys_permission where id = #{id}")
	int delete(Long id);

	@Select("select * from sys_permission t where t.id = #{id}")
	SysPermission findById(Long id);

	@Select("select * from sys_permission t where t.permission = #{permission}")
	SysPermission findByPermission(String permission);

	int count(Map<String, Object> params);

	List<SysPermission> findList(Map<String, Object> params);

}
