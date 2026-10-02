package com.zhenshu.jianxue.mec.educenter.controller;

import com.zhenshu.common.annotation.Response;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdAskLeaveBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdAskLeaveVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdAskLeaveService;
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
 * 学生请假Controller
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Api(value = "学生请假", tags = "学生请假接口")
@RestController
@RequestMapping("/educenter/vacate")
// 因为请假与上课记录是同一个模块，所以共用同一个权限
@PreAuthorize("@ss.hasPermi('educenter:record:list')")
public class BdAskLeaveController extends BaseController {

    @Resource
    private IBdAskLeaveService bdAskLeaveService;


    @ApiOperation(value = "查询学生请假列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdAskLeaveVO", value = "学生请假封装类", dataType = "bdAskLeaveVO")
    @GetMapping("/list")
    @Response
    public TableDataInfo list(BdAskLeaveVO bdAskLeaveVO) {
        startPage();
        List<BdAskLeaveBO> list = bdAskLeaveService.selectBdAskLeaveList(bdAskLeaveVO);
        if (list == null || list.isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(list);
    }


    @ApiOperation(value = "获取学生请假详细信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "bdAskLeaveVO", value = "获取学生请假详细信息", dataType = "Long")
    @GetMapping(value = "/{id}")
    @Response
    public Result<BdAskLeaveBO> getInfo(@PathVariable("id") Long id) {
        return new Result<BdAskLeaveBO>().success(bdAskLeaveService.selectBdAskLeaveById(id));
    }


    @ApiOperation(value = "修改学生请假详细信息", httpMethod = "PUT")
    @Log(title = "学生请假", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(paramType = "query", name = "bdAskLeaveVO", value = "修改学生请假详细信息", dataType = "bdAskLeaveVO")
    @PutMapping
    @Response
    public Result<Boolean> edit(@RequestBody BdAskLeaveVO bdAskLeaveVO) {
        return new Result<Boolean>().success(bdAskLeaveService.updateBdAskLeave(bdAskLeaveVO));
    }
}
