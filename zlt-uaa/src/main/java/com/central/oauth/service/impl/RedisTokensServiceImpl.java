package com.central.oauth.service.impl;

import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import com.central.common.constant.SecurityConstants;
import com.central.common.model.PageResult;
import com.central.common.redis.template.RedisRepository;
import com.central.oauth.model.TokenVo;
import com.central.oauth.service.ITokensService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * token管理服务(redis token)
 *
 * @author zlt
 * @date 2019/7/12
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTokensServiceImpl implements ITokensService {
    private final static SerializationCodec AUTH_CODEC = new SerializationCodec();
    private static final String AUTHORIZATION = "token";
    private final RedissonClient redisson;

    @Override
    public PageResult<TokenVo> listTokens(Map<String, Object> params, String clientId) {
        Integer page = MapUtils.getInteger(params, "page");
        Integer limit = MapUtils.getInteger(params, "limit");
        int[] startEnds = PageUtil.transToStartEnd(page-1, limit);
        //根据请求参数生成redis的key
        String redisKey = getRedisKey(params, clientId);
        RList<String> tokenList = redisson.getList(redisKey);
        long size = tokenList.size();
        List<TokenVo> result = new ArrayList<>(limit);
        //查询token集合
        List<String> tokens = tokenList.range(startEnds[0], startEnds[1]-1);
        if (tokens != null) {
            for (String token : tokens) {
                //构造token对象
                TokenVo tokenVo = new TokenVo();
                tokenVo.setTokenValue(token);
                //获取用户信息
                RBucket<OAuth2Authorization> rBucket = redisson.getBucket(buildKey(OAuth2ParameterNames.ACCESS_TOKEN, token), AUTH_CODEC);
                OAuth2Authorization authorization = rBucket.get();
                if (authorization != null) {
                    OAuth2AccessToken accessToken = authorization.getAccessToken().getToken();
                    if (accessToken != null && accessToken.getExpiresAt() != null) {
                        tokenVo.setExpiration(Date.from(accessToken.getExpiresAt()));
                    }
                    tokenVo.setUsername(authorization.getPrincipalName());
                    tokenVo.setClientId(authorization.getRegisteredClientId());
                    tokenVo.setGrantType(authorization.getAuthorizationGrantType().getValue());

                    String accountType = (String)authorization.getAttributes().get(SecurityConstants.ACCOUNT_TYPE_PARAM_NAME);
                    tokenVo.setAccountType(accountType);
                }
                result.add(tokenVo);
            }
        }
        return PageResult.<TokenVo>builder().data(result).code(0).count(size).build();
    }

    /**
     * 根据请求参数生成redis的key
     */
    private String getRedisKey(Map<String, Object> params, String clientId) {
        String result;
        String username = MapUtils.getString(params, "username");
        if (StrUtil.isNotEmpty(username)) {
            result = this.buildKey(SecurityConstants.REDIS_UNAME_TO_ACCESS, clientId + "::" + username);
        } else {
            result = this.buildKey(SecurityConstants.REDIS_CLIENT_ID_TO_ACCESS, clientId);
        }
        return result;
    }

    private String buildKey(String type, String id) {
        return String.format("%s::%s::%s", AUTHORIZATION, type, id);
    }
}
