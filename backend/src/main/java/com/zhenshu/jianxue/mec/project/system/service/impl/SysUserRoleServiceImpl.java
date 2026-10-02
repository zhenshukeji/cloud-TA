package com.zhenshu.jianxue.mec.project.system.service.impl;

import com.zhenshu.jianxue.mec.project.system.domain.SysUserRole;
import com.zhenshu.jianxue.mec.project.system.mapper.SysUserRoleMapper;
import com.zhenshu.jianxue.mec.project.system.service.ISysUserRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/12 0012 16:45
 **/
@Service
public class SysUserRoleServiceImpl implements ISysUserRoleService {

    @Resource
    private SysUserRoleMapper userRoleMapper;

    @Override
    public void batchUserRole(List<SysUserRole> roles) {
        userRoleMapper.batchUserRole(roles);
    }

    @Override
    public void deleteUserRoleByUserId(Long userId, Integer userType) {
        userRoleMapper.deleteUserRoleByUserId(userId, userType);
    }

    @Override
    public List<Long> selectByUserIdAndUserType(Long userId, Integer userType) {
        return userRoleMapper.selectByUserIdAndUserType(userId, userType);
    }
}
