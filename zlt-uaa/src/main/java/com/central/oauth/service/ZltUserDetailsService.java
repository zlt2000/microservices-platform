package com.central.oauth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author zlt
 * @date 2018/12/28
 */
public interface ZltUserDetailsService extends UserDetailsService {
    UserDetails loadUserByMobile(String mobile) throws UsernameNotFoundException;
}
