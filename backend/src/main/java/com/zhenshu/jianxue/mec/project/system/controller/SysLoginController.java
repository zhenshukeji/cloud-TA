package com.zhenshu.jianxue.mec.project.system.controller;

import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.UserLoginType;
import com.zhenshu.common.manager.mec.base.LoginUserManager;
import com.zhenshu.common.utils.ServletUtils;
import com.zhenshu.framework.security.LoginBody;
import com.zhenshu.framework.security.LoginUser;
import com.zhenshu.framework.security.service.SysLoginService;
import com.zhenshu.framework.security.service.SysPermissionService;
import com.zhenshu.framework.security.service.TokenService;
import com.zhenshu.framework.web.domain.AjaxResult;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.jianxue.mec.project.system.domain.SysMenu;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.jianxue.mec.project.system.service.ISysMenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 登录验证
 *
 * @author zhenshu
 */
@RestController
public class SysLoginController {
    @Resource
    private SysLoginService loginService;

    @Resource
    private ISysMenuService menuService;

    @Resource
    private SysPermissionService permissionService;

    @Resource
    private TokenService tokenService;

    @Resource
    private LoginUserManager loginUserManager;

    /**
     * 登录方法
     * <p>
     * username 用户名
     * password 密码
     * captcha 验证码
     * uuid 唯一标识
     *
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }


    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        // 角色集合，用处不大，暂时不需要维护
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = loginUserManager.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        // 用户信息
        SysUser user = loginUser.getUser();
        // 如果是机构人员
        if (UserLoginType.TYPE_MEC_PERSONNEL.getCode().equals(user.getUserType())) {
            if (user.getCampusConnectionId() == null) {
                return AjaxResult.error("该机构人员尚未选择登录校区，无法获取权限列表");
            }
        }
        List<SysMenu> menus = loginUserManager.selectMenuTreeByUser(user);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
