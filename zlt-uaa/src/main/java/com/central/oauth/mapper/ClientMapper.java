package com.central.oauth.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.db.mapper.SuperMapper;
import org.apache.ibatis.annotations.Param;

import com.central.oauth.model.Client;

/**
 * @author zlt
 */
public interface ClientMapper extends SuperMapper<Client> {
    //int count(@Param("params") Map<String, Object> params);

    List<Client> findList(Page<Client> page, @Param("params") Map<String, Object> params );

    /*@Select("select * from oauth_client_details t where t.client_id = #{clientId}")
    Client getClient(String clientId);*/

    /*@Update("update oauth_client_details t set t.client_secret = #{clientSecret},t.client_secret_str = #{clientSecretStr}  where t.id = #{id}")
    int update(Client client);*/
}
