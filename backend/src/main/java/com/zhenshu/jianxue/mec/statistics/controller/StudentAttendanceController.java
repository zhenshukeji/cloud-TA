package com.zhenshu.jianxue.mec.statistics.controller;

import com.zhenshu.common.manager.mec.statistics.StudentAttendanceManager;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.jianxue.mec.statistics.domain.bo.StudentAttendanceBO;
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
 * @desc 学员考勤统计报表
 * @date 2020/11/12 0012 16:30
 **/
@RestController
@Api(value = "学员考勤统计报表 ", tags = "学员考勤统计报表 接口")
@RequestMapping("/statistics/attendance")
@PreAuthorize("@ss.hasPermi('statistics:attendance:list')")
public class StudentAttendanceController extends BaseController {

    @Resource
    private StudentAttendanceManager studentAttendanceManager;

    @ApiOperation(value = "学员考勤统计报表")
    @GetMapping("/list")
    public Result<Object> getStudentAttendance() {
        List<StudentAttendanceBO> list = studentAttendanceManager.getStudentAttendance();
        return new Result<>().success(list);
    }


}
