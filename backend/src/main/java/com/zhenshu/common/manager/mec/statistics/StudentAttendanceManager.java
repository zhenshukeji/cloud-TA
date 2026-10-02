package com.zhenshu.common.manager.mec.statistics;

import com.zhenshu.jianxue.mec.educenter.service.IBdCourseService;
import com.zhenshu.jianxue.mec.statistics.domain.bo.StudentAttendanceBO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jing
 * @version 1.0
 * @desc 学员考勤统计报表
 * @date 2020/11/12 0012 17:26
 **/
@Service
public class StudentAttendanceManager {
    @Resource
    private IBdCourseService bdCourseService;

    public List<StudentAttendanceBO> getStudentAttendance() {
        return bdCourseService.getStudentAttendance();
    }
}
