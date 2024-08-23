package com.zhenshu.framework.security.service;

import com.zhenshu.common.manager.mec.base.LoginUserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户验证处理，用于获取登录用户的详细信息
 *
 * @author zhenshu
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private LoginUserManager loginUserManager;


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return loginUserManager.loadUserByUsername(userName);
    }
}
