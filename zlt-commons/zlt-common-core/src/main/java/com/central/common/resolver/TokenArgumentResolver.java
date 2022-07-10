package com.central.common.resolver;

import cn.hutool.core.util.StrUtil;
import com.central.common.annotation.LoginUser;
import com.central.common.constant.SecurityConstants;
import com.central.common.feign.UserService;
import com.central.common.model.SysRole;
import com.central.common.model.SysUser;
import com.central.common.utils.LoginUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Token转化SysUser
 *
 * @author zlt
 * @date 2018/12/21
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Slf4j
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {
    private UserService userService;

    public TokenArgumentResolver(UserService userService) {
        this.userService = userService;
    }

    /**
     * 入参筛选
     *
     * @param methodParameter 参数集合
     * @return 格式化后的参数
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(LoginUser.class) && methodParameter.getParameterType().equals(SysUser.class);
    }

    /**
     * @param methodParameter       入参集合
     * @param modelAndViewContainer model 和 view
     * @param nativeWebRequest      web相关
     * @param webDataBinderFactory  入参解析
     * @return 包装对象
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) {
        LoginUser loginUser = methodParameter.getParameterAnnotation(LoginUser.class);
        boolean isFull = loginUser.isFull();
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        //账号类型
        String accountType = request.getHeader(SecurityConstants.ACCOUNT_TYPE_HEADER);

        return LoginUserUtils.getCurrentUser(request, isFull);
    }
}
