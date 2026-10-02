package com.zhenshu.mapper.educenter;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTeacherBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassVO;
import com.zhenshu.jianxue.mec.statistics.domain.bo.ClassDetailDataBO;
import com.zhenshu.jianxue.mec.statistics.domain.vo.BdClassDetailVO;
import com.zhenshu.mapper.domain.educenter.BdClass;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 班级Mapper接口
 *
 * @author jing
 * @date 2020-09-25
 */
public interface BdClassMapper extends BaseMapper<BdClass> {

    /**
     * 获取某课程的所有班级
     *
     * @param courseId 课程id
     * @return 班级集合
     */
    List<BdClass> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 更新班级的某课程名称
     *
     * @param bdClass 班级信息
     * @return boolean
     */
    boolean updateClsCourseName(BdClass bdClass);

    /**
     * 获得班级列表查询
     *
     * @param bdClassVO 班级查询VO
     * @return 班级集合
     */
    List<BdClassBO> getClassList(BdClassVO bdClassVO);

    /**
     * 课表获得所有班级
     *
     * @param bdClassVO 班级名称
     * @return 班级信息
     */
    List<BdClassBO> selectClassBySchoolTimeTable(BdClassVO bdClassVO);

    /**
     * 返回老师教授班级的总人数
     *
     * @param teacherIdList 老师id
     * @return 每个老师教授班级的总人数
     */
    List<BdClassBO> getTotalTaught(@Param("teacherIdList") List<Long> teacherIdList);

    /**
     * 获取班级明细报表
     */
    List<ClassDetailDataBO> getClassData(BdClassDetailVO bdClassDetailVO);

    /**
     * 得到老师所教授的主教班级
     *
     * @param idList 老师ids
     * @return 集合
     */
    List<BdTeacherBO> getClassesByTeacher(@Param("idList") List<Long> idList);

    /**
     * 获得这些班级的学生数量以及班级可容纳数量
     *
     * @param classIds 班级ids
     * @return 学生数量以及班级可容纳数量
     */
    List<BdClassBO> selectStuNumByClsIds(@Param("classIds") List<Long> classIds);

    /**
     * 获得颜色，主教老师名称，辅助老师名称，班级人数
     *
     * @param classIds 班级集合
     * @return 获得颜色，主教老师名称，辅助老师名称，班级人数
     */
    List<BdClassBO> getClassInfoByClassIds(@Param("classIds")List<Long> classIds);

    /**
     * 获得班级详细信息
     * @param classId 班级id
     * @return 班级详细信息
     */
    BdClassBO selectClassDetailById(Long classId);

    /**
     * 原子自增已授课次
     *
     * @param id 班级id
     * @return 受影响行数
     */
    int incrHasClass(@Param("id") Long id);
}
