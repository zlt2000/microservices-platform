package com.central.gateway.filter.pre;

import cn.hutool.core.util.StrUtil;
import com.central.log.monitor.PointUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求统计分析埋点过滤器
 *
 * @author zlt
 * @date 2019/5/6
 */
@Slf4j
@Component
public class RequestStatisticsFilter extends ZuulFilter {
    private final static String UNKNOWN_STR = "unknown";

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest req = ctx.getRequest();
        UserAgent userAgent = UserAgent.parseUserAgentString(req.getHeader("User-Agent"));

        //埋点
        PointUtil.debug("0","request-statistics",
                "ip="+this.getIpAddr(req)
                         +"&browser="+userAgent.getBrowser()
                         +"&operatingSystem="+userAgent.getOperatingSystem());
        return null;
    }

    /**
     * 获取Ip地址
     */
    public  String getIpAddr(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");
        if (isEmptyIP(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            if (isEmptyIP(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (isEmptyIP(ip)) {
                    ip = request.getHeader("HTTP_CLIENT_IP");
                    if (isEmptyIP(ip)) {
                        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                        if (isEmptyIP(ip)) {
                            ip = request.getRemoteAddr();
                        }
                    }
                }
            }
        }
        return ip;
    }

    private boolean isEmptyIP(String ip) {
        if (StrUtil.isEmpty(ip) || UNKNOWN_STR.equalsIgnoreCase(ip)) {
            return true;
        }
        return false;
    }
}
