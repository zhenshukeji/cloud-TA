package com.zhenshu.jianxue.mec.base.controller;

import com.zhenshu.jianxue.mec.base.domain.bo.BdMechanismBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdMechanismVO;
import com.zhenshu.jianxue.mec.base.service.IBdMechanismService;
import com.zhenshu.common.annotation.Response;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.manager.mec.base.LoginUserManager;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.framework.aspectj.lang.annotation.Log;
import com.zhenshu.framework.aspectj.lang.enums.BusinessType;
import com.zhenshu.framework.security.LoginUser;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 机构信息Controller
 *
 * @author jing
 * @date 2020-10-04
 */
@Api(value = "机构信息", tags = "机构信息接口")
@RestController
@RequestMapping("/base/mechanism")
@PreAuthorize("@ss.hasPermi('base:mechanism:list')")
public class BdMechanismController extends BaseController {

    @Resource
    private IBdMechanismService bdMechanismService;
    @Resource
    private LoginUserManager loginUserManager;

    @ApiOperation(value = "获取机构信息详细信息", httpMethod = "GET")
    @GetMapping(value = "/get")
    @Response
    public Result<BdMechanismBO> getInfo() {
        Long mecId = SecurityUtils.getLoginMecId();
        return new Result<BdMechanismBO>().success(bdMechanismService.selectBdMechanismById(mecId));
    }


    @ApiOperation(value = "修改机构信息详细信息", httpMethod = "PUT")
    @Log(title = "机构信息", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(paramType = "query", name = "bdMechanismVO", value = "机构信息详细封装参数", dataType = "BdMechanismVO")
    @PutMapping("/updateBdMechanism")
    @Response
    public Result<Boolean> edit(@NotNull @RequestBody BdMechanismVO bdMechanismVO) {
        Long mecId = SecurityUtils.getLoginMecId();
        bdMechanismVO.setId(mecId);
        loginUserManager.updateBdMechanism(bdMechanismVO);
        return new Result<Boolean>().success();
    }

    @ApiOperation(value = "修改机构信息名称与logo", httpMethod = "PUT")
    @Log(title = "修改机构信息名称与logo", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(paramType = "query", name = "bdMechanismVO", value = "机构信息详细封装参数", dataType = "BdMechanismVO")
    @PutMapping("/updateNameOrLogo")
    @Response
    public Result<Boolean> updateNameOrLogo(@NotNull @RequestBody BdMechanismVO bdMechanismVO) {
        Long mecId = SecurityUtils.getLoginMecId();
        bdMechanismVO.setId(mecId);
        loginUserManager.updateNameOrLogo(bdMechanismVO);
        return new Result<Boolean>().success();
    }


    @ApiOperation(value = "机构管理员进入校区")
    @Log(title = "进入校区", businessType = BusinessType.UPDATE)
    @PostMapping("/into/{campusId}")
    @Response
    public Result<String> into(@PathVariable Long campusId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getUser();
        if (StringUtils.isEmpty(sysUser.getUserType())) {
            throw new CustomException("请先登录");
        }
        // 如果不是平台管理员并且不是机构管理员
        if (!SecurityUtils.isMecOrPlatManager()) {
            return new Result<String>().fail("您没有进入校区的权限");
        }
        loginUserManager.intoCampus(loginUser, sysUser, campusId);
        return new Result<String>().success();
    }


    @ApiOperation(value = "机构管理员返回机构")
    @Log(title = "返回机构", businessType = BusinessType.UPDATE)
    @PostMapping("/goBack")
    @Response
    public Result<String> goBack() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getUser();
        if (StringUtils.isEmpty(sysUser.getUserType())) {
            throw new CustomException("请先登录");
        }
        // 如果不是平台管理员并且不是机构管理员
        if (!SecurityUtils.isMecOrPlatManager()) {
            return new Result<String>().fail("您没有返回机构的权限");
        }
        loginUserManager.goBackMec(loginUser, sysUser);
        return new Result<String>().success();
    }
}
