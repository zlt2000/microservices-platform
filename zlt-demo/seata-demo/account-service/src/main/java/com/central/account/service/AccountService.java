package com.central.account.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.central.account.model.Account;
import com.central.account.mapper.AccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author zlt
 * @date 2019/9/14
 */
@Slf4j
@Service
public class AccountService {
    @Resource
    private AccountMapper accountMapper;

    /**
     * 减账号金额
     */
    //@Transactional(rollbackFor = Exception.class)
    public void reduce(String userId, int money) {
        if ("U002".equals(userId)) {
            throw new RuntimeException("this is a mock Exception");
        }

        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        wrapper.setEntity(new Account().setUserId(userId));
        Account account = accountMapper.selectOne(wrapper);
        account.setMoney(account.getMoney() - money);
        accountMapper.updateById(account);
    }
}
