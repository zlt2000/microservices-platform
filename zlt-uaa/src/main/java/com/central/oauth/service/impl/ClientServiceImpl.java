package com.central.oauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.central.common.constant.SecurityConstants;
import com.central.common.context.LoginUserContextHolder;
import com.central.common.lock.DistributedLock;
import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.common.service.impl.SuperServiceImpl;
import com.central.oauth.mapper.ClientMapper;
import com.central.oauth.model.Client;
import com.central.oauth.service.IClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zlt
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ClientServiceImpl extends SuperServiceImpl<ClientMapper, Client> implements IClientService {
    private final static String LOCK_KEY_CLIENTID = "clientId:";

    private final RedissonClient redisson;

    private final PasswordEncoder passwordEncoder;

    private final DistributedLock lock;

    @Override
    public Result saveClient(Client client) throws Exception {
        client.setClientSecret(passwordEncoder.encode(client.getClientSecretStr()));
        String clientId = client.getClientId();
        if (client.getId() == null) {
            client.setCreatorId(LoginUserContextHolder.getUser().getId());
        }
        super.saveOrUpdateIdempotency(client, lock
                , LOCK_KEY_CLIENTID+clientId
                , new QueryWrapper<Client>().eq("client_id", clientId)
                , clientId + "已存在");

        redisson.getBucket(clientRedisKey(clientId)).set(client);

        return Result.succeed("操作成功");
    }

    @Override
    public PageResult<Client> listClient(Map<String, Object> params, boolean isPage) {
        Page<Client> page;
        if (isPage) {
            page = new Page<>(MapUtils.getInteger(params, "page"), MapUtils.getInteger(params, "limit"));
        } else {
            page = new Page<>(1, -1);
        }
        List<Client> list = baseMapper.findList(page, params);
        page.setRecords(list);
        return PageResult.<Client>builder().data(list).code(0).count(page.getTotal()).build();
    }

    @Override
    public void delClient(long id) {
        String clientId = baseMapper.selectById(id).getClientId();
        baseMapper.deleteById(id);
        redisson.getBucket(clientRedisKey(clientId)).delete();
    }

    @Override
    public Client loadClientByClientId(String clientId) {
        RBucket<Client> clientBucket = redisson.getBucket(clientRedisKey(clientId));
        Client client = clientBucket.get();
        if (client != null) {
            return client;
        }
        QueryWrapper<Client> wrapper = Wrappers.query();
        wrapper.eq("client_id", clientId);
        client = this.getOne(wrapper);
        clientBucket.set(client);
        return client;
    }

    @Override
    public void loadAllClientToCache() {
        List<Client> clientList = this.list();
        clientList.forEach(c -> redisson.getBucket(clientRedisKey(c.getClientId())).set(c));
    }

    private String clientRedisKey(String clientId) {
        return SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId;
    }
}
