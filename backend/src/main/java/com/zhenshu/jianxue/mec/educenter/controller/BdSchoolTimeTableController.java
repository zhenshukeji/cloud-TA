package com.zhenshu.jianxue.mec.educenter.controller;

import com.zhenshu.common.annotation.Response;
import com.zhenshu.common.lock.LockFunction;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.jackson.JacksonUtil;
import com.zhenshu.framework.aspectj.lang.annotation.Log;
import com.zhenshu.framework.aspectj.lang.enums.BusinessType;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.framework.web.page.TableDataInfo;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassTimeBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdRollCallBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.*;
import com.zhenshu.jianxue.mec.educenter.manager.SchoolTimeTableManager;
import com.zhenshu.jianxue.mec.educenter.manager.TimeTableDataManager;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuxi
 * @desc 课表信息
 * @date 2020-10-15 11:21
 */
@Api(value = "课表信息", tags = "课表信息")
@RestController
@RequestMapping("/educenter/curriculum")
@PreAuthorize("@ss.hasPermi('educenter:curriculum:list')")
public class BdSchoolTimeTableController extends BaseController {


    @Resource
    SchoolTimeTableManager schoolTimeTableManager;

    @Resource
    TimeTableDataManager timeTableDataManager;


    @ApiOperation(value = "课表信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "classTimeVO", value = "班级名称 课程名称", dataType = "ClassTimeVO")
    @GetMapping("/selectAll")
    @Response
    public Result<List<BdClassTimeBO>> selectAll(ClassTimeVO classTimeVO) {
        return new Result<List<BdClassTimeBO>>().success(timeTableDataManager.selectAll(classTimeVO));
    }

    @ApiOperation(value = "编辑课表信息中的课次", httpMethod = "POST")
    @Log(title = "编辑课表信息中的课次 ", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(paramType = "query", name = "bdClassTimeVO", value = "编辑课表信息中的课次", dataType = "BdClassTimeVO")
    @PutMapping("/changeClassTime")
    @Response
    public Result<Boolean> changeClassTime(@RequestBody BdClassTimeVO bdClassTimeVO) {
        return new Result<Boolean>().success(schoolTimeTableManager.changeClassTime(bdClassTimeVO));
    }

    @ApiOperation(value = "删除课表信息中的课次", httpMethod = "PUT")
    @Log(title = "删除课表信息中的课次 ", businessType = BusinessType.DELETE)
    @ApiImplicitParam(paramType = "query", name = "bdClassTimeVO", value = "删除课表信息中的课次", dataType = "BdClassTimeVO")
    @PutMapping("/deleteClassTime")
    @Response
    public Result<Boolean> deleteClassTime(@RequestBody BdClassTimeVO bdClassTimeVO) {
        schoolTimeTableManager.deleteClassTime(bdClassTimeVO);
        return new Result<Boolean>().success();
    }

    @ApiOperation(value = "进入点名页面")
    @ApiImplicitParam(paramType = "query", name = "bdClassTimeVO", value = "班级上课表", dataType = "BdClassTimeVO")
    @GetMapping("/intoRollCall")
    @Response
    public Result<BdRollCallBO> intoRollCallByClassTime(BdClassTimeVO bdClassTimeVO) {
        return new Result<BdRollCallBO>().success(schoolTimeTableManager.intoRollCall(bdClassTimeVO));
    }


    @ApiOperation(value = "点名")
    @Log(title = "点名 ", businessType = BusinessType.OTHER)
    @ApiImplicitParam(paramType = "query", name = "rollCallVO", value = "点名入参VO", dataType = "BdRollCallVO")
    @PostMapping("/rollCall")
    @Response
    @LockFunction(methodName = "rollCall")
    public Result<Boolean> rollCall(@RequestBody BdRollCallVO rollCallVO) {
        schoolTimeTableManager.rollCall(rollCallVO);
        return new Result<Boolean>().success();
    }


    @ApiOperation(value = "获取班级上课时间详细信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "ClassTimeVO", value = "获取班级上课时间详细信息", dataType = "classTimeVO")
    @GetMapping(value = "/getInfo")
    @Response
    public Result<BdClassTimeBO> getInfo(ClassTimeVO classTimeVO) {
        return new Result<BdClassTimeBO>().success(schoolTimeTableManager.getInfo(classTimeVO));
    }

    @ApiOperation(value = "新增课次", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "classChangeVO", value = "新增课次", dataType = "BdClassChangeVO")
    @Log(title = "新增课次", businessType = BusinessType.INSERT)
    @PostMapping(value = "/insertChangeClass")
    @Response
    @LockFunction(methodName = "insertChangeClass")
    public Result<Boolean> insertChangeClass(@RequestBody BdClassChangeVO classChangeVO) {
        return new Result<Boolean>().success(schoolTimeTableManager.insertChangeClass(classChangeVO));
    }

    @ApiOperation(value = "获取所有班级", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "classVO", value = "获取所有班级", dataType = "BdClassVO")
    @GetMapping("/selectAllClass")
    @Response
    public TableDataInfo selectAllClass(BdClassVO classVO) {
        startPage();
        List<BdClassBO> list = schoolTimeTableManager.selectAllClass(classVO);
        if (list == null || list.isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(list);
    }

    @ApiOperation(value = "获取某天所有班级", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "classVO", value = "获取某天所有班级", dataType = "BdClassVO")
    @PostMapping("/getOneDayClass")
    @Response
    public Result<List<BdClassTimeBO>> getOneDayClass(@RequestBody String body) {
        String date = JacksonUtil.parseString(body,"date");
        String className = JacksonUtil.parseString(body,"className");
        SysUser sysUser = SecurityUtils.getSysUser();
        ClassTimeVO classTimeVO = new ClassTimeVO();
        classTimeVO.setClassName(className);
        classTimeVO.setCampusId(sysUser.getCampusId());
        classTimeVO.setMecId(sysUser.getMecId());
        classTimeVO.setClassName(className);
        return new Result<List<BdClassTimeBO>>().success(timeTableDataManager.getOneDayClass(date, classTimeVO));
    }

}
