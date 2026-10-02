package com.zhenshu.jianxue.mec.educenter.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.lock.LockFunction;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMecPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusPersonnelVO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdMecPersonnelVO;
import com.zhenshu.common.annotation.Response;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTeacherBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdTeacherVO;
import com.zhenshu.jianxue.mec.educenter.manager.TeacherManager;
import com.zhenshu.jianxue.mec.educenter.service.IBdTeacherService;
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

/**
 * 教师Controller
 *
 * @author jing
 * @date 2020-09-25
 */
@Api(value = "教师", tags = "教师接口")
@RestController
@RequestMapping("/educenter/teacher")
@PreAuthorize("@ss.hasPermi('educenter:teacher:list')")
public class BdTeacherController extends BaseController {

    @Resource
    private IBdTeacherService bdTeacherService;

    @Resource
    private TeacherManager teacherManager;


    @ApiOperation(value = "查询教师列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdTeacher", value = "教师封装类", dataType = "BdTeacherVO")
    @GetMapping("/list")
    @Response
    public TableDataInfo list(BdTeacherVO bdTeacherVO){
        Page<BdTeacherBO> page = bdTeacherService.selectBdTeacherList(bdTeacherVO);
        if (page.getRecords() == null || page.getRecords().isEmpty()){
            return new TableDataInfo();
        }
        teacherManager.getClasses(page.getRecords());
        return getDataTable(page);
    }

    @ApiOperation(value = "获取教师详细信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "BdTeacherVO", value = "获取教师详细信息", dataType = "Long")
    @GetMapping(value = "/{id}")
    @Response
    public Result<BdTeacherBO> getInfo(@PathVariable("id") Long id) {
        return new Result<BdTeacherBO>().success(teacherManager.selectBdTeacherById(id));
    }

    @ApiOperation(value = "修改教师详细信息", httpMethod = "PUT")
    @Log(title = "修改教师详细信息", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(paramType = "query", name = "BdTeacherVO", value = "修改教师详细信息", dataType = "BdTeacherVO")
    @PutMapping
    @Response
    public Result<Boolean> edit(@RequestBody BdTeacherVO bdTeacherVO) {
        return new Result<Boolean>().success(teacherManager.updateBdTeacher(bdTeacherVO));
    }


    @ApiOperation(value = "新增教师", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "BdTeacherVO", value = "新增教师详细信息", dataType = "BdTeacherVO")
    @Log(title = "新增教师", businessType = BusinessType.INSERT)
    @PostMapping
    @Response
    @LockFunction(methodName = "addTeacher")
    public Result<Boolean> add(@RequestBody BdTeacherVO bdTeacherVO) {
        return new Result<Boolean>().success(bdTeacherService.insertBdTeacher(bdTeacherVO));
    }


    @ApiOperation(value = "删除教师")
    @Log(title = "删除教师", businessType = BusinessType.DELETE)
    @ApiImplicitParam(paramType = "path", name = "ids", value = "删除的教师的ids", dataType = "Long[]")
    @DeleteMapping("/{ids}")
    @Response
    public Result<String> remove(@PathVariable Long[] ids) {
        if (ids.length <= 0){
            throw new CustomException("教师id不能为空");
        }
        teacherManager.checkIsDelete(ids);
        return new Result<String>().success();
    }


    @ApiOperation(value = "获取校区人员")
    @ApiImplicitParam(paramType = "query", name = "campusPersonnelVO", value = "获取校区人员", dataType = "BdCampusPersonnelVO")
    @GetMapping("/getCampusPerson")
    @Response
    public TableDataInfo getCampusPerson(BdCampusPersonnelVO campusPersonnelVO) {
        Page<BdCampusPersonnelBO> page = teacherManager.getAllCampusPerson(campusPersonnelVO);
        if (page.getRecords() == null || page.getRecords().isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(page);
    }

    @ApiOperation(value = "获取机构人员")
    @ApiImplicitParam(paramType = "query", name = "mecPersonnelVO", value = "获取机构人员", dataType = "BdMecPersonnelVO")
    @GetMapping("/getMecPerson")
    @Response
    public TableDataInfo getCampusPerson(BdMecPersonnelVO mecPersonnelVO) {
        Page<BdMecPersonnelBO> page = teacherManager.getAllMecPerson(mecPersonnelVO);
        if (page.getRecords() == null || page.getRecords().isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(page);
    }
}
