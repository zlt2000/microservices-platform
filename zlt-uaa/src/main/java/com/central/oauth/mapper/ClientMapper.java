package com.central.oauth.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.db.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.central.oauth.model.Client;

/**
 * @author zlt
 */
@Mapper
public interface ClientMapper extends SuperMapper<Client> {
    List<Client> findList(Page<Client> page, @Param("params") Map<String, Object> params );
}
