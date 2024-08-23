package com.zhenshu.common.utils;

import com.zhenshu.common.constant.HttpStatus;
import com.zhenshu.common.enums.UserLoginType;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.framework.security.LoginUser;
import com.zhenshu.zhenxueyou.mec.project.system.domain.SysUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 安全服务工具类
 *
 * @author zhenshu
 */
public class SecurityUtils {
    /**
     * 获取用户账户
     **/
    public static String getUsername() {
        try {
            return getLoginUser().getUsername();
        } catch (Exception e) {
            throw new CustomException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户账户
     **/
    public static String getNickname() {
        try {
            return getLoginUser().getUser().getNickName();
        } catch (Exception e) {
            throw new CustomException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户id
     **/
    public static Long getUserId() {
        try {
            return getLoginUser().getUser().getUserId();
        } catch (Exception e) {
            throw new CustomException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }
    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser() {
        try {
            return (LoginUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new CustomException("获取用户信息异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取登录用户的机构id
     */
    public static Long getLoginMecId() {
        try {
            LoginUser loginUser = (LoginUser) getAuthentication().getPrincipal();
            SysUser sysUser = loginUser.getUser();
            return sysUser.getMecId();
        } catch (Exception e) {
            throw new CustomException("获取登录用户所在机构失败", HttpStatus.UNAUTHORIZED);
        }
    }

     /**
     * 获取当前登录的用户信息，里面包括当前登录的校区id
     */
    public static Long getLoginCampusId() {
        try {
            LoginUser loginUser = (LoginUser) getAuthentication().getPrincipal();
            SysUser sysUser = loginUser.getUser();
            return sysUser.getCampusId();
        } catch (Exception e) {
            throw new CustomException("获取登录用户所在校区失败", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取当前登录的用户信息，里面包括当前登录的校区id
     */
    public static SysUser getSysUser() {
        try {
            LoginUser loginUser = (LoginUser) getAuthentication().getPrincipal();
            return loginUser.getUser();
        } catch (Exception e) {
            throw new CustomException("获取登录用户信息", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    /**
     * 是否为管理员
     * @return 结果
     */
    public static boolean isManager(){
        SysUser sysUser = SecurityUtils.getSysUser();
        if (UserLoginType.TYPE_MEC_MANAGER.getCode().equals(sysUser.getUserType())){
            return true;
        }
        if (UserLoginType.TYPE_PLATFORM_MANAGER.getCode().equals(sysUser.getUserType())){
            return true;
        }
        if (UserLoginType.TYPE_CAMPUS_MANAGER.getCode().equals(sysUser.getUserType())){
            return true;
        }
        return false;
    }

    /**
     * 是否为机构管理员或者是平台管理员
     * @return 结果
     */
    public static boolean isMecOrPlatManager(){
        SysUser sysUser = SecurityUtils.getSysUser();
        if (UserLoginType.TYPE_MEC_MANAGER.getCode().equals(sysUser.getUserType())){
            return true;
        }
        if (UserLoginType.TYPE_PLATFORM_MANAGER.getCode().equals(sysUser.getUserType())){
            return true;
        }
        return false;
    }

}
