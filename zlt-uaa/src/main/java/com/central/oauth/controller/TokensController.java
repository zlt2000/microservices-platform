package com.central.oauth.controller;

import com.central.common.model.PageResult;
import com.central.oauth.model.TokenVo;
import com.central.oauth.service.ITokensService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * token管理接口
 *
 * @author zlt
 */
@Tag(name = "Token管理")
@Slf4j
@RestController
@RequestMapping("/tokens")
public class TokensController {
    @Resource
    private ITokensService tokensService;

    @GetMapping("")
    @Operation(summary = "token列表")
    public PageResult<TokenVo> list(@RequestParam Map<String, Object> params, String tenantId) {
        return tokensService.listTokens(params, tenantId);
    }
}
