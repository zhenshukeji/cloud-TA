package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCourseBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseVO;
import com.zhenshu.mapper.domain.educenter.BdCourse;
import com.zhenshu.jianxue.mec.statistics.domain.bo.StudentAttendanceBO;

import java.util.List;

/**
 * 课程Service接口
 *
 * @author yuxi
 * @date 2020-10-04
 */
public interface IBdCourseService extends IService<BdCourse> {

    /**
     * 查询课程
     *
     * @param id 课程ID
     * @return 课程
     */
    BdCourseBO selectBdCourseById(Long id);

    /**
     * 可以根据id搜索到已删除的课程
     *
     * @param courseId 学生ID
     * @return 学生信息
     */
    BdCourseBO selectCourseContainIsDeletedById(Long courseId);

    /**
     * 分页查询常规课程列表
     *
     * @param bdCourseVO 课程
     * @return 分页课程
     */
    Page<BdCourseBO> selectConventionalCourseList(BdCourseVO bdCourseVO);

    /**
     * 查询所有有效的课程
     *
     * @param bdCourseVO 课程
     * @return List<BdCourseBO>
     */
    Page<BdCourseBO> selectAllCourseByPage(BdCourseVO bdCourseVO);

    /**
     * 查询所有有效且上架的课程
     *
     * @param bdCourseVO 课程
     * @return List<BdCourseBO>
     */
    List<BdCourseBO> selectAll(BdCourseVO bdCourseVO);

    /**
     * 查询所有有效且上架的课程
     * @param bdCourseVO
     * @return
     */
    List<BdCourseBO> queryTheCourseOnTheShelves(BdCourseVO bdCourseVO);

    /**
     * 新增课程
     *
     * @param bdCourseVO 课程
     * @return 结果
     */
    boolean insertBdCourse(BdCourseVO bdCourseVO);

    /**
     * 修改课程
     *
     * @param bdCourseVO 课程
     * @return 结果
     */
    boolean updateBdCourse(BdCourseVO bdCourseVO);

    /**
     * 删除课程信息
     *
     * @param id 课程ID
     * @return 结果
     */
    boolean deleteBdCourseById(Long id);


    /**
     * 学员考勤统计
     */
    List<StudentAttendanceBO> getStudentAttendance();
 }
