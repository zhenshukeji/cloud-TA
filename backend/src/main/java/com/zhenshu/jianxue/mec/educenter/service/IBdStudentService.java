package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdStudentBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.CourseInfoStuBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdStudentVO;
import com.zhenshu.mapper.domain.educenter.BdStudent;

import java.util.List;
import java.util.TreeSet;

/**
 * 学生Service接口
 *
 * @author jing
 * @date 2020-09-25
 */
public interface IBdStudentService extends IService<BdStudent> {

    /**
     * 学员课时账户导出查询（OS024，C14）。
     */
    java.util.List<com.zhenshu.jianxue.mec.educenter.domain.vo.StudentExportVO> selectStudentExportList(
            Long mecId, Long campusId, String studentName);

    /**
     * 查询基本学生
     *
     * @param id 学生ID
     * @return 学生
     */
    BdStudentBO selectBdStudentBaseById(Long id);

    /**
     * 根据手机号查找学生
     *
     * @param phone 手机号
     * @return BdStudentBO
     */
    BdStudentBO selectBdStudentBaseByPhone(String phone);

    /**
     * 查询学生列表
     *
     * @param bdStudentVO 学生
     * @return 学生集合
     */
    Page<BdStudentBO> selectBdStudentList(BdStudentVO bdStudentVO);

    /**
     * 查询回收站学生列表
     *
     * @param bdStudentVO 学生
     * @return 学生集合
     */
    Page<BdStudentBO> selectRecycleStuList(BdStudentVO bdStudentVO);

    /**
     * 新增学生
     *
     * @param bdStudentVO 学生
     * @return 结果
     */
    boolean insertBdStudent(BdStudentVO bdStudentVO);

    /**
     * 修改学生
     *
     * @param bdStudentVO 学生
     * @return 结果
     */
    boolean updateBdStudent(BdStudentVO bdStudentVO);

    /**
     * 恢复回收站的学员
     *
     * @param id 学生
     * @return 结果
     */
    boolean updateRecoverRec(Long id);


    /**
     * 删除学生信息
     *
     * @param id 学生ID
     * @return 结果
     */
    boolean deleteBdStudentById(Long id);

    /**
     * 得到不在该班级的学生
     *
     * @param bdClassVO 班级入参VO
     * @return List<BdStudentBO>
     */
    List<BdStudentBO> getStudentList(BdClassVO bdClassVO);


    /**
     * 查询学员的课次信息列表
     *
     * @param studentId  学员id
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
    List<BdStudentBO> getStudentListByIds(Long[] stuIds);

    /**
     * 查询在该手机集合中，是否重复
     * @param phoneSet 手机号集合
     * @return 手机号已存在的学生集合
     */
    List<BdStudent> selectStudentByPhones(TreeSet<String> phoneSet);
}
