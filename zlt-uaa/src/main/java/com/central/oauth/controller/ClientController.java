package com.central.oauth.controller;

import com.central.common.model.PageResult;
import com.central.common.model.Result;
import com.central.oauth.model.Client;
import com.central.oauth.service.impl.RegisteredClientService;
import com.central.oauth2.common.pojo.ClientDto;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 应用相关接口
 *
 * @author zlt
 * <p>
 * Blog: http://zlt2000.gitee.io
 * Github: https://github.com/zlt2000
 */
@Tag(name = "应用")
@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final RegisteredClientService clientService;

    @GetMapping("/list")
    @Operation(summary = "应用列表")
    public PageResult<ClientDto> list(@RequestParam Map<String, Object> params) {
        return clientService.listClient(params, true);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id获取应用")
    public ClientDto get(@PathVariable Long id) {
        return clientService.getById(id);
    }

    @GetMapping("/all")
    @Operation(summary = "所有应用")
    public Result<List<ClientDto>> allClient() {
        PageResult<ClientDto> page = clientService.listClient(Maps.newHashMap(), false);
        return Result.succeed(page.getData());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除应用")
    public void delete(@PathVariable Long id) {
        clientService.delClient(id);
    }

    @PostMapping("/saveOrUpdate")
    @Operation(summary = "保存或者修改应用")
    public Result<String> saveOrUpdate(@RequestBody Client client) throws Exception {
        clientService.saveClient(client);
        return Result.succeed("操作成功");
    }
}
