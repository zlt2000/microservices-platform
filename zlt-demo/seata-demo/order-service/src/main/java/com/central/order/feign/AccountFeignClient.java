package com.central.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zlt
 * @date 2019/9/14
 */
@FeignClient(name = "account-service")
public interface AccountFeignClient {
    @PostMapping("account/reduce")
    Boolean reduce(@RequestParam("userId") String userId, @RequestParam("money") Integer money);
}
