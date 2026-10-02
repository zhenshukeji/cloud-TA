package com.zhenshu.common.manager.mec.educenter;

import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.jianxue.mec.project.system.service.ISysUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/24 0024 19:59
 **/
@Service
public class ClassRecordWithSysUserManager {

    @Resource
    ISysUserService sysUserService;

    /**
     * 获得用户名的nickname
     * @param sysUserId 用户id
     * @return nickname
     */
    public String getCallTeacherName(Long sysUserId){
        SysUser sysUser = sysUserService.selectUserById(sysUserId);
        return sysUser.getNickName();
    }
}
