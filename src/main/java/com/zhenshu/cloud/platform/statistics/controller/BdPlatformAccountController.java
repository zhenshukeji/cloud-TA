package com.zhenshu.cloud.platform.statistics.controller;

import com.zhenshu.common.annotation.Response;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.cloud.platform.statistics.domain.bo.BdPlatformAccountBO;
import com.zhenshu.cloud.platform.statistics.manager.PlatformManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 平台账户 Controller
 *
 * @author yuxi
 * @date 2020-11-24
 */
@Api(value = "平台账户 ", tags = "平台账户 接口")
@RestController
@RequestMapping("/platform/account")
//因为平台流水与平台账户是在原型图上是同一个页面，所以需要用同一个权限标识
@PreAuthorize("@ss.hasPermi('platform:turnover:list')")
public class BdPlatformAccountController extends BaseController {

    @Resource
    private PlatformManager platformManager;


    @ApiOperation(value = "获取平台账户 详细信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "BdPlatformAccountVO", value = "获取平台账户 信息", dataType = "Long")
    @GetMapping
    @Response
    public Result<BdPlatformAccountBO> getInfo() {
        return new Result<BdPlatformAccountBO>().success(platformManager.selectBdPlatformAccountById(Constants.PLATFROM_ID));
    }

}
