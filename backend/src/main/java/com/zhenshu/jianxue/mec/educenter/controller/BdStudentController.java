package com.zhenshu.jianxue.mec.educenter.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.zhenshu.common.annotation.Response;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.lock.LockFunction;
import com.zhenshu.framework.aspectj.lang.annotation.Log;
import com.zhenshu.framework.aspectj.lang.enums.BusinessType;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.framework.web.page.TableDataInfo;
import com.zhenshu.jianxue.mec.educenter.domain.bo.*;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCoursePurchaseVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseTimeUpdateVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdStudentVO;
import com.zhenshu.jianxue.mec.educenter.manager.StudentManager;
import com.zhenshu.jianxue.mec.educenter.service.IBdStudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import com.zhenshu.common.utils.poi.ExcelUtil;
import com.zhenshu.framework.web.domain.AjaxResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 学生Controller
 *
 * @author jing
 * @date 2020-09-25
 */
@Api(value = "学生", tags = "学生接口")
@RestController
@RequestMapping("/educenter/student")
@PreAuthorize("@ss.hasPermi('educenter:student:list')")
public class BdStudentController extends BaseController {

    @Resource
    private IBdStudentService bdStudentService;

    @Resource
    private StudentManager studentManager;

    @ApiOperation(value = "查询在读学员列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdStudentVO", value = "学生封装类", dataType = "BdStudentVO")
    @GetMapping("/list")
    @Response
    public TableDataInfo list(BdStudentVO bdStudentVO) {
        Page<BdStudentBO> page = studentManager.selectBdStudentList(bdStudentVO);
        if(page == null){
            return new TableDataInfo();
        }
        return getDataTable(page);
    }


    @ApiOperation(value = "查询回收站的学员", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdStudentVO", value = "学生封装类", dataType = "BdStudentVO")
    @GetMapping("/list/recycle")
    @Response
    public TableDataInfo recycleStu(BdStudentVO bdStudentVO) {
        Page<BdStudentBO> page = bdStudentService.selectRecycleStuList(bdStudentVO);
        if (page.getRecords() == null || page.getRecords().isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(page);
    }


    @ApiOperation(value = "获取学生基本信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "id", value = "获取学生基本信息", dataType = "Long")
    @GetMapping(value = "/base/{id}")
    @Response
    public Result<BdStudentBO> getBaseInfo(@PathVariable("id") Long id) {
        return new Result<BdStudentBO>().success(studentManager.selectBdStudentBaseById(id));
    }


    @ApiOperation(value = "修改学生信息", httpMethod = "PUT")
    @Log(title = "学生", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(paramType = "query", name = "BdStudentVO", value = "修改学生信息", dataType = "BdStudentVO")
    @PutMapping
    @Response
    public Result<Boolean> edit(@RequestBody BdStudentVO bdStudentVO) {
        return new Result<Boolean>().success(studentManager.updateBdStudent(bdStudentVO));
    }


    @ApiOperation(value = "新增学生", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "BdStudentVO", value = "新增学生详细信息", dataType = "BdStudentVO")
    @Log(title = "学生", businessType = BusinessType.INSERT)
    @PostMapping
    @Response
    @LockFunction(methodName = "addStudent")
    public Result<Boolean> add(@RequestBody BdStudentVO bdStudentVO) {
        return new Result<Boolean>().success(bdStudentService.insertBdStudent(bdStudentVO));
    }


    @ApiOperation(value = "批量删除学生")
    @Log(title = "学生", businessType = BusinessType.DELETE)
    @ApiImplicitParam(paramType = "path", name = "ids", value = "删除的学生的ids", dataType = "Long[]")
    @DeleteMapping("/{ids}")
    @Response
    public Result<Boolean> remove(@PathVariable Long[] ids) {
        if (ids.length <= 0){
            throw new CustomException("学生id不能为空");
        }
        studentManager.deleteStudent(ids);
        return new Result<Boolean>().success();
    }


    @ApiOperation(value = "恢复回收站的学员", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "id", value = "学生id", dataType = "Long")
    @Log(title = "恢复回收站的学员", businessType = BusinessType.UPDATE)
    @GetMapping("/recoverRec/{id}")
    @Response
    public Result<Boolean> recoverRecStu(@PathVariable Long id) {
        return new Result<Boolean>().success(studentManager.updateRecoverRec(id));
    }

    @ApiOperation(value = "获取账户信息（课次信息）", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdStudentVO", value = "获取账户信息（课次信息）", dataType = "BdStudentVO")
    @GetMapping(value = "/getInfoWithClassTime")
    @Response
    public TableDataInfo getInfoWithClassTime(BdStudentVO bdStudentVO) {
        startPage();
        List<CourseInfoStuBO> list = studentManager.getCourseTimeByStuId(bdStudentVO);
        if (list == null || list.isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(list);
    }


    @ApiOperation(value = "课次更新", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "courseTimeVO", value = "获取账户信息（修改课次）", dataType = "BdCourseTimeVO")
    @Log(title = "课次更新", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/updateCourseTime")
    @Response
    public Result<Boolean> updateCourseTime(@RequestBody BdCourseTimeUpdateVO courseTimeUpdateVO) {
        //课次更新不能为负数
        int num = courseTimeUpdateVO.getNum();
        if (num <= 0) {
            throw new CustomException("课次更新不能为负数或零，请重新输入");
        }
        studentManager.updateCourseTime(courseTimeUpdateVO);
        return new Result<Boolean>().success();
    }

    @ApiOperation(value = "", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "courseTimeVO", value = "获取账户信息（修改课次）", dataType = "BdCourseTimeVO")
    @Log(title = "修改总课次", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/updateTotalCourseTime")
    @Response
    public Result<Boolean> updateTotalCourseTime(@RequestBody BdCourseTimeUpdateVO courseTimeUpdateVO) {
        //课次更新不能为负数
        int num = courseTimeUpdateVO.getNum();
        if (num <= 0) {
            throw new CustomException("课次更新不能为负数或零，请重新输入");
        }
        studentManager.updateTotalCourseTime(courseTimeUpdateVO);
        return new Result<Boolean>().success();
    }


    @ApiOperation(value = "获取账户信息（修改课次记录）", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdStudentVO", value = "账户信息（修改课次记录）", dataType = "BdStudentVO")
    @GetMapping(value = "/getInfoWithClsTimeUp")
    @Response
    public TableDataInfo getInfoWithClassTimeUpdate(BdStudentVO bdStudentVO) {
        startPage();
        List<UpCouInfoStuBO> list = studentManager.getCourseTimeUpdateByStuId(bdStudentVO);
        if (list == null || list.isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(list);
    }


    @ApiOperation(value = "获取账户信息（课程购买记录）", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdStudentVO", value = "账户信息（课程购买记录）", dataType = "BdStudentVO")
    @GetMapping(value = "/getInfoWithCouPur")
    @Response
    public TableDataInfo getInfoWithCoursePurchase(BdStudentVO bdStudentVO) {
        startPage();
        List<BdCoursePurchaseBO> list = studentManager.getInfoWithCoursePurchase(bdStudentVO);
        return getDataTable(list);
    }


    @ApiOperation(value = "导出学员课时账户", httpMethod = "GET")
    @Log(title = "学员课时账户导出", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(BdStudentVO bdStudentVO) {
        List<com.zhenshu.jianxue.mec.educenter.domain.vo.StudentExportVO> list = studentManager.exportStudents(bdStudentVO);
        ExcelUtil<com.zhenshu.jianxue.mec.educenter.domain.vo.StudentExportVO> util =
                new ExcelUtil<>(com.zhenshu.jianxue.mec.educenter.domain.vo.StudentExportVO.class);
        // AjaxResult.success(String) 重载会把参数放进 msg；强转 (Object) 走 success(Object) 才放进 data
        Object filename = util.exportExcel(list, "学员课时账户").get(AjaxResult.MSG_TAG);
        return AjaxResult.success((Object) filename);
    }

    @ApiOperation(value = "学生新增课程购买记录 ", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "BdCoursePurchaseVO", value = "新增课程购买记录 详细信息", dataType = "BdCoursePurchaseVO")
    @Log(title = "学生新增课程购买记录 ", businessType = BusinessType.INSERT)
    @PostMapping("/addCoursePurchase")
    @Response
    @LockFunction(methodName = "addCoursePurchase")
    public Result<Boolean> addCoursePurchase(@RequestBody BdCoursePurchaseVO bdCoursePurchaseVO) {
        studentManager.insertBdCoursePurchase(bdCoursePurchaseVO);
        return new Result<Boolean>().success();
    }


    @ApiOperation(value = "查询所有有效的课程", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdCourseVO", value = "课程封装类", dataType = "BdCourseVO")
    @GetMapping("/selectAllCourse")
    @Response
    public TableDataInfo selectAll(BdCourseVO bdCourseVO) {
        List<BdCourseBO> list = studentManager.selectAllCourse(bdCourseVO);
        return getDataTable(list);
    }


    @ApiOperation(value = "学生导入", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "BdStudentVO", value = "学生导入", dataType = "BdStudentVO")
    @Log(title = "学生", businessType = BusinessType.IMPORT)
    @PostMapping("/importStudents")
    @Response
    public Result<String> importStudents(@RequestParam("file") MultipartFile file) {
        return new Result<String>().success(studentManager.importStudents(file));
    }
}
