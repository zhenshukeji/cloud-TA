package com.zhenshu.jianxue.mec.base.controller;

import com.zhenshu.common.annotation.Response;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.jianxue.mec.base.manager.MecPersonnelInfoManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * @author jing
 * @version 1.0
 * @desc 无需权限的接口放在这里
 * @date 2020/10/12 0012 21:53
 *
 * <p>社区版（OS008）收口：移除机构自助注册（平台入驻/审核语义，社区版由初始化脚本创建唯一机构）、
 * 微信支付回调与平台资讯接口。
 *
 * <p>社区版（OS011）收口：移除「忘记密码」两步（{@code /getUser} 与 {@code /forGetPassword}）。
 * 这两步依赖短信验证码，而社区版不提供任何短信服务；去掉验证码后，未认证就能改任意账号口令
 * 会变成直接的越权漏洞，因此整个入口一并移除，不做「无验证码版」保留。
 * 运维重置口令请在部署环境直接改库；已登录用户改密走 {@code /system/user/profile/updatePwd}。
 */
@Api(value = "基础部分", tags = "无需权限的接口")
@RestController
@RequestMapping("/base/access")
public class BdAccessController extends BaseController {

    @Resource
    private MecPersonnelInfoManager mecPersonnelInfoManager;

    @ApiOperation(value = "机构人员登录", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdMecPersonnelVO", value = "机构人员登录", dataType = "BdMecPersonnelVO")
    @GetMapping("/login")
    @Response
    public Result<String> login(@NotNull Long campusId, HttpServletRequest request) {
        mecPersonnelInfoManager.login(request, campusId);
        return new Result<String>().success();
    }
}
