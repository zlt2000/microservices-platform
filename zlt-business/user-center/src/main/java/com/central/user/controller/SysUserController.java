package com.central.user.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.central.common.annotation.LoginUser;
import com.central.common.constant.CommonConstant;
import com.central.common.model.*;
import com.central.common.utils.ExcelUtil;
//import com.central.log.annotation.AuditLog;
import com.central.log.annotation.AuditLog;
import com.central.search.client.service.IQueryService;
import com.central.search.model.LogicDelDto;
import com.central.search.model.SearchDto;
import com.central.user.model.SysUserExcel;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import com.central.user.service.ISysUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 作者 owen E-mail: 624191343@qq.com
 * 用户
 */
@Slf4j
@RestController
@Api(tags = "用户模块api")
public class SysUserController {
    private static final String ADMIN_CHANGE_MSG = "超级管理员不给予修改";

    /**
     * 全文搜索逻辑删除Dto
     */
    private static final LogicDelDto SEARCH_LOGIC_DEL_DTO = new LogicDelDto("isDel", "否");

    @Autowired
    private ISysUserService appUserService;

    @Autowired
    private IQueryService queryService;

    /**
     * 当前登录用户 LoginAppUser
     *
     * @return
     */
    @ApiOperation(value = "根据access_token当前登录用户")
    @GetMapping("/users/current")
    public Result<LoginAppUser> getLoginAppUser(@LoginUser(isFull = true) SysUser user) {
        return Result.succeed(appUserService.getLoginAppUser(user));
    }

    /**
     * 查询用户实体对象SysUser
     */
    @GetMapping(value = "/users/name/{username}")
    @ApiOperation(value = "根据用户名查询用户实体")
    @Cacheable(value = "user", key = "#username")
    public SysUser selectByUsername(@PathVariable String username) {
        return appUserService.selectByUsername(username);
    }

    /**
     * 查询用户登录对象LoginAppUser
     */
    @GetMapping(value = "/users-anon/login", params = "username")
    @ApiOperation(value = "根据用户名查询用户")
    public LoginAppUser findByUsername(String username) {
        return appUserService.findByUsername(username);
    }

    /**
     * 通过手机号查询用户、角色信息
     *
     * @param mobile 手机号
     */
    @GetMapping(value = "/users-anon/mobile", params = "mobile")
    @ApiOperation(value = "根据手机号查询用户")
    public SysUser findByMobile(String mobile) {
        return appUserService.findByMobile(mobile);
    }

    /**
     * 根据OpenId查询用户信息
     *
     * @param openId openId
     */
    @GetMapping(value = "/users-anon/openId", params = "openId")
    @ApiOperation(value = "根据OpenId查询用户")
    public SysUser findByOpenId(String openId) {
        return appUserService.findByOpenId(openId);
    }

    @GetMapping("/users/{id}")
    public SysUser findUserById(@PathVariable Long id) {
        return appUserService.getById(id);
    }

    /**
     * 管理后台修改用户
     *
     * @param sysUser
     */
    @PutMapping("/users")
    @CachePut(value = "user", key = "#sysUser.username", unless="#result == null")
    //@AuditLog(operation = "'更新用户:' + #sysUser")
    public void updateSysUser(@RequestBody SysUser sysUser) {
        appUserService.updateById(sysUser);
    }

    /**
     * 管理后台给用户分配角色
     *
     * @param id
     * @param roleIds
     */
    @PostMapping("/users/{id}/roles")
    public void setRoleToUser(@PathVariable Long id, @RequestBody Set<Long> roleIds) {
        appUserService.setRoleToUser(id, roleIds);
    }

    /**
     * 获取用户的角色
     *
     * @param
     * @return
     */
    @GetMapping("/users/{id}/roles")
    public List<SysRole> findRolesByUserId(@PathVariable Long id) {
        return appUserService.findRolesByUserId(id);
    }

    /**
     * 用户查询
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "用户查询列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer")
    })
    @GetMapping("/users")
    public PageResult<SysUser> findUsers(@RequestParam Map<String, Object> params) {
        return appUserService.findUsers(params);
    }

    /**
     * 修改用户状态
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "修改用户状态")
    @GetMapping("/users/updateEnabled")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "enabled", value = "是否启用", required = true, dataType = "Boolean")
    })
    public Result updateEnabled(@RequestParam Map<String, Object> params) {
        Long id = MapUtils.getLong(params, "id");
        if (checkAdmin(id)) {
            return Result.failed(ADMIN_CHANGE_MSG);
        }
        return appUserService.updateEnabled(params);
    }

    /**
     * 管理后台，给用户重置密码
     *
     * @param id
     */
    @PutMapping(value = "/users/{id}/password")
    //@AuditLog(operation = "'重置用户密码:' + #id")
    public Result resetPassword(@PathVariable Long id) {
        if (checkAdmin(id)) {
            return Result.failed(ADMIN_CHANGE_MSG);
        }
        appUserService.updatePassword(id, null, null);
        return Result.succeed("重置成功");
    }

    /**
     * 用户自己修改密码
     */
    @PutMapping(value = "/users/password")
    public Result resetPassword(@RequestBody SysUser sysUser) {
        if (checkAdmin(sysUser.getId())) {
            return Result.failed(ADMIN_CHANGE_MSG);
        }
        appUserService.updatePassword(sysUser.getId(), sysUser.getOldPassword(), sysUser.getNewPassword());
        return Result.succeed("重置成功");
    }

    /**
     * 删除用户
     *
     * @param id
     */
    @DeleteMapping(value = "/users/{id}")
    //@AuditLog(operation = "'删除用户:' + #id")
    public Result delete(@PathVariable Long id) {
        if (checkAdmin(id)) {
            return Result.failed(ADMIN_CHANGE_MSG);
        }
        appUserService.delUser(id);
        return Result.succeed("删除成功");
    }


    /**
     * 新增or更新
     *
     * @param sysUser
     * @return
     */
    @CacheEvict(value = "user", key = "#sysUser.username")
    @PostMapping("/users/saveOrUpdate")
    @AuditLog(operation = "'新增或更新用户:' + #sysUser.username")
    public Result saveOrUpdate(@RequestBody SysUser sysUser) throws Exception {
        return appUserService.saveOrUpdateUser(sysUser);
    }

    /**
     * 导出excel
     *
     * @return
     */
    @PostMapping("/users/export")
    public void exportUser(@RequestParam Map<String, Object> params, HttpServletResponse response) throws IOException {
        List<SysUserExcel> result = appUserService.findAllUsers(params);
        //导出操作
        ExcelUtil.exportExcel(result, null, "用户", SysUserExcel.class, "user", response);
    }

    @PostMapping(value = "/users/import")
    public Result importExcl(@RequestParam("file") MultipartFile excl) throws Exception {
        int rowNum = 0;
        if(!excl.isEmpty()) {
            List<SysUserExcel> list = ExcelUtil.importExcel(excl, 0, 1, SysUserExcel.class);
            rowNum = list.size();
            if (rowNum > 0) {
                List<SysUser> users = new ArrayList<>(rowNum);
                list.forEach(u -> {
                    SysUser user = new SysUser();
                    BeanUtil.copyProperties(u, user);
                    user.setPassword(CommonConstant.DEF_USER_PASSWORD);
                    user.setType(UserType.BACKEND.name());
                    users.add(user);
                });
                appUserService.saveBatch(users);
            }
        }
        return Result.succeed("导入数据成功，一共【"+rowNum+"】行");
    }

    @ApiOperation(value = "用户全文搜索列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "queryStr", value = "搜索关键字", dataType = "String")
    })
    @GetMapping("/users/search")
    public PageResult<JSONObject> search(SearchDto searchDto) {
        searchDto.setIsHighlighter(true);
        searchDto.setSortCol("createTime");
        return queryService.strQuery("sys_user", searchDto, SEARCH_LOGIC_DEL_DTO);
    }

    /**
     * 是否超级管理员
     */
    private boolean checkAdmin(long id) {
        return id == 1L;
    }
}
