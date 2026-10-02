package com.zhenshu.mapper.educenter;

import java.util.List;

import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCourseBO;
import com.zhenshu.mapper.domain.educenter.BdCourse;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.statistics.domain.bo.StudentAttendanceBO;
import org.apache.ibatis.annotations.Param;

/**
 * 课程Mapper接口
 *
 * @author jing
 * @date 2020-09-25
 */
public interface BdCourseMapper extends BaseMapper<BdCourse> {

    List<StudentAttendanceBO> getStudentAttendance(@Param("mecId") Long mecId, @Param("campusId") Long campusId);

    /**
     * 查询上架且有效的课程
     * @param bdCourse 入参
     * @return 课程信息
     */
    List<BdCourseBO> queryTheCourseOnTheShelves(BdCourse bdCourse);
}
