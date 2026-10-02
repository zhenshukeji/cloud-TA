package com.zhenshu.jianxue.mec.educenter.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.github.pagehelper.PageInfo;
import com.zhenshu.common.annotation.Response;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.lock.LockFunction;
import com.zhenshu.common.utils.jackson.JacksonUtil;
import com.zhenshu.jianxue.mec.educenter.domain.bo.*;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdStudentClassVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdTeacherVO;
import com.zhenshu.jianxue.mec.educenter.manager.ClassManager;
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
 * @author yuxi
 * @desc 班级Controller
 * @date 2020-10-04
 */
@Api(value = "班级", tags = "班级接口")
@RestController
@RequestMapping("/educenter/class")
@PreAuthorize("@ss.hasPermi('educenter:class:list')")
public class BdClassController extends BaseController {


    @Resource
    private ClassManager classManager;


    @ApiOperation(value = "查询常规班级列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdClassVO", value = "班级封装类", dataType = "BdClassVO")
    @GetMapping("/list")
    @Response
    public TableDataInfo conventionalList(BdClassVO bdClassVO) {
        startPage();
        List<BdClassBO> list = classManager.selectConventionalClsList(bdClassVO);
        if (list == null || list.isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(list);
    }


    @ApiOperation(value = "获取班级详细信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "BdClassVO", value = "获取班级详细信息", dataType = "Long")
    @GetMapping(value = "/{id}")
    @Response
    public Result<BdClassBO> getInfo(@PathVariable("id") Long id) {
        return new Result<BdClassBO>().success(classManager.selectBdClassDetailedById(id));
    }


    @ApiOperation(value = "修改班级详细信息", httpMethod = "PUT")
    @Log(title = "修改班级详细信息", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(paramType = "query", name = "bdClassVO", value = "修改班级详细信息", dataType = "BdClassVO")
    @PutMapping
    @Response
    public Result<Boolean> edit(@RequestBody BdClassVO bdClassVO) {
        classManager.updateBdClass(bdClassVO);
        return new Result<Boolean>().success();
    }


    @ApiOperation(value = "新增班级", httpMethod = "POST")
    @Log(title = "新增班级", businessType = BusinessType.INSERT)
    @ApiImplicitParam(paramType = "query", name = "BdClassVO", value = "新增班级详细信息", dataType = "BdClassVO")
    @PostMapping
    @Response
    public Result<Boolean> add(@RequestBody BdClassVO bdClassVO) {
        classManager.insertBdClass(bdClassVO);
        return new Result<Boolean>().success();
    }


    @ApiOperation(value = "作废班级", httpMethod = "DELETE")
    @Log(title = "作废班级", businessType = BusinessType.DELETE)
    @ApiImplicitParam(paramType = "path", name = "ids", value = "删除的班级的ids", dataType = "Long[]")
    @DeleteMapping("/{ids}")
    @Response
    public Result<String> remove(@PathVariable Long[] ids) {
        classManager.remove(ids);
        return new Result<String>().success();
    }


    @ApiOperation(value = "获得未在该班级的学生", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdClassVO", value = "班级入参", dataType = "BdClassVO")
    @GetMapping(value = "/getStudents")
    @Response
    public TableDataInfo getStudents(BdClassVO bdClassVO) {
        startPage();
        PageInfo<BdCourseTimeBO> pageInfo = classManager.getStudents(bdClassVO);
        if (pageInfo == null) {
            return new TableDataInfo();
        }
        return getDataTable(pageInfo);
    }


    @ApiOperation(value = "获得该班级的学生", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdClassVO", value = "班级入参", dataType = "BdClassVO")
    @GetMapping(value = "/getStudentsByCls")
    @Response
    public TableDataInfo getStudentsByCls(BdClassVO bdClassVO) {
        List<BdStudentBO> list = classManager.getStudentsByClsWithPage(bdClassVO);
        if (list == null || list.isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(list);
    }


    @ApiOperation(value = "班级添加学生", httpMethod = "POST")
    @Log(title = "班级添加学生", businessType = BusinessType.INSERT)
    @ApiImplicitParam(paramType = "query", name = "bdClassVO", value = "班级入参", dataType = "BdClassVO")
    @PostMapping(value = "/addStuClass")
    @Response
    @LockFunction(methodName = "addStuClass")
    public Result<Boolean> addStuClass(@RequestBody BdClassVO bdClassVO) {
        classManager.addStuClass(bdClassVO);
        return new Result<Boolean>().success();
    }

    @ApiOperation(value = "移除班级的学生", httpMethod = "POST")
    @Log(title = "移除班级的学生", businessType = BusinessType.DELETE)
    @ApiImplicitParam(paramType = "query", name = "studentClassVO", value = "学生班级入参", dataType = "BdStudentClassVO")
    @PostMapping(value = "/classRemoveStu")
    @Response
    @LockFunction(methodName = "classRemoveStu")
    public Result<Boolean> classRemoveStu(@RequestBody BdStudentClassVO studentClassVO) {
        classManager.classRemoveStu(studentClassVO);
        return new Result<Boolean>().success();
    }

    @ApiOperation(value = "查询所有有效的课程", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdCourseVO", value = "课程封装类", dataType = "BdCourseVO")
    @GetMapping("/selectAllCourse")
    @Response
    public TableDataInfo selectAll(BdCourseVO bdCourseVO) {
        Page<BdCourseBO> page = classManager.selectAllCourse(bdCourseVO);
        if (page.getRecords() == null || page.getRecords().isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(page);
    }

    @ApiOperation(value = "查询教师列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdTeacher", value = "教师封装类", dataType = "BdTeacherVO")
    @GetMapping("/selectAllTeacher")
    @Response
    public TableDataInfo list(BdTeacherVO bdTeacherVO) {
        Page<BdTeacherBO> page = classManager.selectBdTeacherList(bdTeacherVO);
        if (page == null || page.getRecords() == null || page.getRecords().isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(page);
    }

    @ApiOperation(value = "查询其他的班级", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "bdClassVO", value = "班级封装类", dataType = "BdClassVO")
    @PostMapping("/selectOtherClass")
    @Response
    public TableDataInfo selectOtherClass(@RequestBody BdClassVO bdClassVO) {
        if (bdClassVO.getPageNum() == null || bdClassVO.getPageSize() == null) {
            throw new CustomException("页码，每页条数不可为空");
        }
        if (bdClassVO.getId() == null) {
            throw new CustomException("当前班级id不可为空");
        }
        Page<BdClassBO> page = classManager.selectOtherClass(bdClassVO);
        if (page == null || page.getRecords() == null || page.getRecords().isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(page);
    }

    @ApiOperation(value = "其他班级的学生导入该班级", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdClassVO", value = "班级封装类", dataType = "BdClassVO")
    @PostMapping("/importStuToClass")
    @Response
    public Result<Object> stuToThisClass(@RequestBody String body) {
        Long importClassId = JacksonUtil.parseLong(body, "importClassId");
        Long importedClassId = JacksonUtil.parseLong(body, "importedClassId");
        if (importClassId == null || importedClassId == null) {
            throw new CustomException("参数不可为空");
        }
        return new Result<>().success(classManager.stuToThisClass(importClassId, importedClassId));
    }
}
