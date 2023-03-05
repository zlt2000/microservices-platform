package com.central.oauth.handler.decryptParamHandler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Map;

/**
 * 解密参数的处理接口
 *
 * @author zlt
 * @version 1.0
 * @date 2022/12/29
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public interface IDecryptParamHandler {
    String SEPARATOR = ";";
    /**
     * 需要解密的参数名，多个以 ; 隔离
     * 例如：decrypt_param=username;password
     */
    String PARAM_KEY_DECRYPT_PARAM = "decrypt_param";

    /**
     * 根据 parameters里面decrypt_param 的值进行参数解密，并把解密后的值替换原值
     * @param parameters 参数集合
     */
    default void decryptParams(Map<String, String> parameters) {
        if (CollUtil.isNotEmpty(parameters)) {
            //从 parameters 中获取 decrypt_param 参数值
            String decryptParam = parameters.get(PARAM_KEY_DECRYPT_PARAM);
            //如果参数decrypt_param有值，则进行参数解密
            if (StrUtil.isNotEmpty(decryptParam)) {
                String[] paramNames = decryptParam.split(SEPARATOR);
                this.decrypt(paramNames, parameters);
            }
        }
    }

    /**
     * 解密并更新参数值
     * @param paramNames 需要解密的参数名
     * @param parameters 参数集合(参数名:参数值)
     */
    void decrypt(String[] paramNames, Map<String, String> parameters);
}
