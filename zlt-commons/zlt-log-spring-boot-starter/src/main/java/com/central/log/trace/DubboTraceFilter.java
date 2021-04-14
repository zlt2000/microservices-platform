package com.central.log.trace;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.springframework.util.StringUtils;

/**
 * dubbo过滤器，传递traceId
 *
 * @author zlt
 * @date 2021/1/30
 * <p>
 * Blog: https://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER}, order = MDCTraceUtils.FILTER_ORDER)
public class DubboTraceFilter implements Filter {
    /**
     * 服务消费者：传递traceId给下游服务
     * 服务提供者：获取traceId并赋值给MDC
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        boolean isProviderSide = RpcContext.getContext().isProviderSide();
        if (isProviderSide) { //服务提供者逻辑
            String traceId = invocation.getAttachment(MDCTraceUtils.KEY_TRACE_ID);
            if (StringUtils.isEmpty(traceId)) {
                MDCTraceUtils.addTraceId();
            } else {
                MDCTraceUtils.putTraceId(traceId);
            }
        } else { //服务消费者逻辑
            String traceId = MDCTraceUtils.getTraceId();
            if (!StringUtils.isEmpty(traceId)) {
                invocation.setAttachment(MDCTraceUtils.KEY_TRACE_ID, traceId);
            }
        }
        try {
            return invoker.invoke(invocation);
        } finally {
            if (isProviderSide) {
                MDCTraceUtils.removeTraceId();
            }
        }
    }
}
