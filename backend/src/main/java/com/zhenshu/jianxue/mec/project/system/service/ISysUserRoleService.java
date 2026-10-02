package com.zhenshu.jianxue.mec.project.system.service;

import com.zhenshu.jianxue.mec.project.system.domain.SysUserRole;

import java.util.List;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/12 0012 16:44
 **/
public interface ISysUserRoleService {

    void batchUserRole(List<SysUserRole> roles);

    void deleteUserRoleByUserId(Long userId, Integer userType);

    List<Long> selectByUserIdAndUserType(Long userId ,Integer userType);
}
