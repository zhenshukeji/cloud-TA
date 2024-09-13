package com.zhenshu.cloud.platform.miniapp.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.zhenshu.common.annotation.Response;
import com.zhenshu.cloud.platform.miniapp.domain.vo.MinLoginVO;
import com.zhenshu.cloud.platform.miniapp.domain.bo.MinLoginBO;
import com.zhenshu.cloud.platform.miniapp.service.IMinLoginService;
import com.zhenshu.framework.aspectj.lang.annotation.Log;
import com.zhenshu.framework.aspectj.lang.enums.BusinessType;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.Result;
        import com.zhenshu.framework.web.page.TableDataInfo;
    import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 小程序登录用户 Controller
 *
 * @author jing
 * @date 2020-11-11
 */
@Api(value = "小程序登录用户 ", tags = "小程序登录用户 接口")
@RestController
@RequestMapping("/miniapp/login")
@PreAuthorize("@ss.hasPermi('miniapp:login:list')")
public class MinLoginController extends BaseController {
        @Resource
    private IMinLoginService minLoginService;


    @ApiOperation(value = "查询小程序登录用户 列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "MinLoginVO", value = "小程序登录用户 封装类", dataType = "MinLoginVO")
    @GetMapping("/list")
    @Response
    public TableDataInfo listByPlus(MinLoginVO minLoginVO) {
        Page<MinLoginBO> page = minLoginService.listByMybatisPlus(minLoginVO);
        if (page.getRecords() == null || page.getRecords().isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(page);
    }
    public TableDataInfo listByPageHelper(MinLoginVO minLoginVO) {
        startPage();
        List<MinLoginBO> list = minLoginService.listByPageHelper(minLoginVO);
        if (list == null || list.isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(list);
    }


    @ApiOperation(value = "获取小程序登录用户 详细信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "MinLoginVO", value = "获取小程序登录用户 信息", dataType = "Long")
    @GetMapping(value = "/{id}")
    @Response
    public Result<MinLoginBO> getInfo(@PathVariable("id") Long id) {
        return new Result<MinLoginBO>().success(minLoginService.selectMinLoginById(id));
    }

    @ApiOperation(value = "修改小程序登录用户 详细信息", httpMethod = "PUT")
    @Log(title = "小程序登录用户 ", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(paramType = "query", name = "MinLoginVO", value = "修改小程序登录用户 详细信息", dataType = "MinLoginVO")
    @PutMapping
    @Response
    public Result<Boolean> edit(@RequestBody MinLoginVO minLoginVO) {
        return new Result<Boolean>().success(minLoginService.updateMinLogin(minLoginVO));
    }

    @ApiOperation(value = "新增小程序登录用户 ", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "MinLoginVO", value = "新增小程序登录用户 详细信息", dataType = "MinLoginVO")
    @Log(title = "小程序登录用户 ", businessType = BusinessType.INSERT)
    @PostMapping
    @Response
    public Result<Boolean> add(@RequestBody MinLoginVO minLoginVO) {
        return new Result<Boolean>().success(minLoginService.insertMinLogin(minLoginVO));
    }

    @ApiOperation(value = "删除小程序登录用户 ")
    @Log(title = "小程序登录用户 ", businessType = BusinessType.DELETE)
    @ApiImplicitParam(paramType = "path", name = "id", value = "删除的小程序登录用户 的id", dataType = "Long")
    @DeleteMapping("/{id}")
    @Response
    public Result<Boolean> remove(@PathVariable Long id) {
        return new Result<Boolean>().success(minLoginService.deleteMinLoginById(id));
    }
}
