package com.central.oauth.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.central.common.feign.UserService;
import com.central.common.redis.template.RedisRepository;
import com.central.common.constant.SecurityConstants;
import com.central.common.model.Result;
import com.central.common.model.SysUser;
import com.central.oauth.exception.ValidateCodeException;
import com.central.oauth.service.IValidateCodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zlt
 * @date 2018/12/10
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
@Service
public class ValidateCodeServiceImpl implements IValidateCodeService {
    @Autowired
    private RedisRepository redisRepository;

    @Resource
    private UserService userService;

    /**
     * 保存用户验证码，和randomStr绑定
     *
     * @param deviceId 客户端生成
     * @param imageCode 验证码信息
     */
    @Override
    public void saveImageCode(String deviceId, String imageCode) {
        redisRepository.setExpire(buildKey(deviceId), imageCode, SecurityConstants.DEFAULT_IMAGE_EXPIRE);
    }

    /**
     * 发送验证码
     * <p>
     * 1. 先去redis 查询是否 60S内已经发送
     * 2. 未发送： 判断手机号是否存 ? false :产生4位数字  手机号-验证码
     * 3. 发往消息中心-》发送信息
     * 4. 保存redis
     *
     * @param mobile 手机号
     * @return true、false
     */
    @Override
    public Result sendSmsCode(String mobile) {
        Object tempCode = redisRepository.get(buildKey(mobile));
        if (tempCode != null) {
            log.error("用户:{}验证码未失效{}", mobile, tempCode);
            return Result.failed("验证码未失效，请失效后再次申请");
        }

        SysUser user = userService.findByMobile(mobile);
        if (user == null) {
            log.error("根据用户手机号{}查询用户为空", mobile);
            return Result.failed("手机号不存在");
        }

        String code = RandomUtil.randomNumbers(4);
        log.info("短信发送请求消息中心 -> 手机号:{} -> 验证码：{}", mobile, code);
        redisRepository.setExpire(buildKey(mobile), code, SecurityConstants.DEFAULT_IMAGE_EXPIRE);
        return Result.succeed("true");
    }

    /**
     * 获取验证码
     * @param deviceId 前端唯一标识/手机号
     */
    @Override
    public String getCode(String deviceId) {
        return (String)redisRepository.get(buildKey(deviceId));
    }

    /**
     * 删除验证码
     * @param deviceId 前端唯一标识/手机号
     */
    @Override
    public void remove(String deviceId) {
        redisRepository.del(buildKey(deviceId));
    }

    /**
     * 验证验证码
     */
    @Override
    public void validate(String deviceId, String validCode) {
        if (StringUtils.isBlank(deviceId)) {
            throw new ValidateCodeException("请在请求参数中携带deviceId参数");
        }
        String code = this.getCode(deviceId);
        if (StringUtils.isBlank(validCode)) {
            throw new ValidateCodeException("请填写验证码");
        }

        if (code == null) {
            throw new ValidateCodeException("验证码不存在或已过期");
        }

        if (!StringUtils.equals(code, validCode.toLowerCase())) {
            throw new ValidateCodeException("验证码不正确");
        }

        this.remove(deviceId);
    }

    private String buildKey(String deviceId) {
        return SecurityConstants.DEFAULT_CODE_KEY + ":" + deviceId;
    }
}
