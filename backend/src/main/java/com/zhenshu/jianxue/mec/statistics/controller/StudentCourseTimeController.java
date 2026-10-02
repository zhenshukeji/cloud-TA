package com.zhenshu.jianxue.mec.statistics.controller;

import com.zhenshu.common.manager.mec.statistics.CourseTimeManager;
import com.zhenshu.jianxue.mec.educenter.domain.bo.CourseInfoStuBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseTimeVO;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.framework.web.page.TableDataInfo;
import com.zhenshu.jianxue.mec.statistics.domain.bo.StudentInfoBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jing
 * @version 1.0
 * @desc 学员课时统计
 * @date 2020/11/12 0012 16:32
 **/
@RestController
@Api(value = "学员课时统计 ", tags = "学员课时统计 接口")
@RequestMapping("/statistics/classTime")
@PreAuthorize("@ss.hasPermi('statistics:classTime:list')")
public class StudentCourseTimeController extends BaseController {

    @Resource
    private CourseTimeManager courseTimeManager;

    @ApiOperation(value = "学员信息")
    @GetMapping("/list")
    public TableDataInfo getStudentInfo(BdCourseTimeVO courseTimeVO) {
        startPage();
        List<StudentInfoBO> list = courseTimeManager.selectStudentInfo(courseTimeVO);
        if (list == null || list.isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(list);
    }

    @ApiOperation(value = "课次详细信息")
    @GetMapping("/detail")
    public Result<Object> getCourseTime(Long studentId) {
        List<CourseInfoStuBO> list = courseTimeManager.selectCourseTimeDetailList(studentId);
        return new Result<>().success(list);
    }


}
