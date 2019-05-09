package com.sharding.demo.service.impl;

import com.central.common.service.impl.SuperServiceImpl;
import com.sharding.demo.mapper.UserMapper;
import com.sharding.demo.model.User;
import com.sharding.demo.service.IUserService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zlt
 */
@Slf4j
@Service
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements IUserService {

}