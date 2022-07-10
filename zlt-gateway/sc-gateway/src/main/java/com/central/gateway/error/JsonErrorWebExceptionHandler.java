package com.central.gateway.error;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.reactive.function.server.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义异常处理
 *
 * @author zlt
 * @date 2020/3/30
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
public class JsonErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {
    public JsonErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resourceProperties,
                                        ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 获取异常属性
     */
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = super.getError(request);
        return responseError(request, error);
    }

    /**
     * 指定响应处理方法为JSON处理的方法
     * @param errorAttributes
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * 根据code获取对应的HttpStatus
     * @param errorAttributes
     */
    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        Integer httpStatus = (Integer) errorAttributes.remove("httpStatus");
        return httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    /**
     * 构建异常信息
     * @param request
     * @param ex
     * @return
     */
    private String buildMessage(ServerRequest request, Throwable ex) {
        StringBuilder message = new StringBuilder("Failed to handle request [");
        message.append(request.methodName());
        message.append(" ");
        message.append(request.uri());
        message.append("]");
        if (ex != null) {
            message.append(": ");
            message.append(ex.getMessage());
        }
        return message.toString();
    }

    /**
     * 构建返回的JSON数据格式
     */
    private Map<String, Object> responseError(ServerRequest request, Throwable error) {
        String errorMessage = buildMessage(request, error);
        int httpStatus = getHttpStatus(error);
        Map<String, Object> map = new HashMap<>();
        map.put("resp_code", 1);
        map.put("resp_msg", errorMessage);
        map.put("datas", null);
        map.put("httpStatus", httpStatus);
        return map;
    }

    private int getHttpStatus(Throwable error) {
        int httpStatus;
        if (error instanceof InvalidTokenException) {
            InvalidTokenException ex = (InvalidTokenException)error;
            httpStatus = ex.getHttpErrorCode();
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        return httpStatus;
    }
}
