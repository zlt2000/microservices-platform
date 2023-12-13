package com.central.oauth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.central.common.constant.SecurityConstants;
import com.central.common.model.PageResult;
import com.central.oauth.model.Client;
import com.central.oauth.service.IClientService;
import com.central.oauth2.common.pojo.ClientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
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
public class RegisteredClientService {
    private final IClientService clientService;
    private final RedissonClient redisson;

    public void saveClient(Client client) throws Exception {
        clientService.saveClient(client);
        ClientDto clientDto = BeanUtil.copyProperties(client, ClientDto.class);
        redisson.getBucket(clientRedisKey(client.getClientId())).set(clientDto);
    }

    public PageResult<ClientDto> listClient(Map<String, Object> params, boolean isPage) {
        PageResult<Client> clientPage = clientService.listClient(params, isPage);
        PageResult<ClientDto> result = new PageResult<>();
        result.setCode(clientPage.getCode());
        result.setCount(clientPage.getCount());
        result.setData(BeanUtil.copyToList(clientPage.getData(), ClientDto.class));
        return result;
    }

    public ClientDto getById(long id) {
        return BeanUtil.copyProperties(clientService.getById(id), ClientDto.class);
    }

    public void delClient(long id) {
        Client client = clientService.getById(id);
        if (client != null) {
            clientService.delClient(id);
            redisson.getBucket(clientRedisKey(client.getClientId())).delete();
        }
    }

    public ClientDto loadClientByClientId(String clientId) {
        RBucket<ClientDto> clientBucket = redisson.getBucket(clientRedisKey(clientId));
        ClientDto clientDto = clientBucket.get();
        if (clientDto != null) {
            return clientDto;
        }
        Client clientObj = clientService.loadClientByClientId(clientId);
        clientDto = BeanUtil.copyProperties(clientObj, ClientDto.class);
        clientBucket.set(clientDto);
        return clientDto;
    }

    public void loadAllClientToCache() {
        List<Client> clientList = clientService.list();
        clientList.forEach(c -> {
            ClientDto clientDto = BeanUtil.copyProperties(c, ClientDto.class);;
            redisson.getBucket(clientRedisKey(c.getClientId())).set(clientDto);
        });
    }

    public List<ClientDto> list() {
        return BeanUtil.copyToList(clientService.list(), ClientDto.class);
    }

    public ClientDto getRegisteredClientByClientId(String clientId) {
        Client clientObj = clientService.loadClientByClientId(clientId);
        return BeanUtil.copyProperties(clientObj, ClientDto.class);
    }

    private String clientRedisKey(String clientId) {
        return SecurityConstants.CACHE_CLIENT_KEY + ":" + clientId;
    }
}
