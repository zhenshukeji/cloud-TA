package com.zhenshu.common.manager.mec.statistics;

import com.zhenshu.jianxue.mec.educenter.domain.bo.CourseInfoStuBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseTimeVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdCourseTimeService;
import com.zhenshu.jianxue.mec.educenter.service.IBdStudentService;
import com.zhenshu.jianxue.mec.statistics.domain.bo.StudentInfoBO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jing
 * @version 1.0
 * @desc 课次管理
 * @date 2020/11/13 0013 10:11
 **/
@Service
public class CourseTimeManager {

    @Resource
    private IBdCourseTimeService bdCourseTimeService;

    @Resource
    private IBdStudentService bdStudentService;

    /**
     * 查询学员班级列表
     */
    public List<StudentInfoBO> selectStudentInfo(BdCourseTimeVO courseTimeVO) {
        return bdCourseTimeService.selectStudentInfo(courseTimeVO);
    }

    /**
     * 查询学员的课次信息列表
     *
     * @param studentId  学员id
     * @return 课次信息列表
     */
    public List<CourseInfoStuBO> selectCourseTimeDetailList(Long studentId){
        return bdStudentService.selectCourseTimeDetailList(studentId);
    }
}
