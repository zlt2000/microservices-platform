package com.sharding.demo.mapper;

import com.central.db.mapper.SuperMapper;
import com.sharding.demo.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zlt
 */
@Mapper
public interface UserMapper extends SuperMapper<User> {

}
