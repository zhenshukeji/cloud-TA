package com.zhenshu.mapper.educenter;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdStudentBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.CourseInfoStuBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdStudentVO;
import com.zhenshu.mapper.domain.educenter.BdStudent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生Mapper接口
 *
 * @author jing
 * @date 2020-09-25
 */
public interface BdStudentMapper extends BaseMapper<BdStudent> {

    /**
     * 学员课时账户导出查询（OS024，C14）：按会话机构/校区过滤，一行 = 学员 × 课时账户。
     */
    List<com.zhenshu.jianxue.mec.educenter.domain.vo.StudentExportVO> selectStudentExportList(
            @Param("mecId") Long mecId, @Param("campusId") Long campusId, @Param("studentName") String studentName);

    /**
     * 获取不在该班级的学生
     * @param bdClassVO 班级信息
     * @return List<BdStudentBO>
     */
    List<BdStudentBO> getStudentList(BdClassVO bdClassVO);

    /**
     * 获取不在该班级的学生数量
     * @param bdClassVO 班级信息
     * @return Integer
     */
    Integer getStudentListCount(BdClassVO bdClassVO);

    /**
     * 查询学生的课次列表
     * @param studentId 学员id
     * @return 课次信息列表
     */
    List<CourseInfoStuBO> selectCourseTimeDetailList(Long studentId);

    /**
     * 获取某个老师主教下的学员列表（包括学员班级信息）（加上模糊查询），供学员列表与学员评价等场景复用
     *
     * @param bdStudentVO 学生信息
     * @return 学生信息
     */
    List<BdStudentBO> getStudentDetailByTeacher(BdStudentVO bdStudentVO);

    /**
     * 在评价学生列表里面得到学生所在的班级
     * @param bdStudentVO 校区id 机构id
     * @return 评价学生列表里面得到学生所在的班级
     */
    List<BdStudentBO> getStudentClassDetail(BdStudentVO bdStudentVO);

    /**
     * 获得学生集合
     * @param stuIds 学生id集合
     * @return 学生集合
     */
    List<BdStudentBO> getStudentListByIds(@Param("stuIds") Long[] stuIds);

    /**
     * 按id列表查询学生，并过滤机构与校区，防止跨校区 enrol
     *
     * @param stuIds    学生id集合
     * @param mecId     机构id
     * @param campusId  校区id
     * @return 学生集合
     */
    List<BdStudentBO> getStudentListByIdsWithScope(@Param("stuIds") Long[] stuIds, @Param("mecId") Long mecId, @Param("campusId") Long campusId);
}
