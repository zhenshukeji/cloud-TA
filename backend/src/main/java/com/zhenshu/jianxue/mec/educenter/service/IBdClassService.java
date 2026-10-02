package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTeacherBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassVO;
import com.zhenshu.jianxue.mec.statistics.domain.bo.ClassDetailDataBO;
import com.zhenshu.jianxue.mec.statistics.domain.vo.BdClassDetailVO;
import com.zhenshu.mapper.domain.educenter.BdClass;
import com.zhenshu.mapper.domain.educenter.BdClassRecord;

import java.util.List;
import java.util.Map;


/**
 * 班级Service接口
 *
 * @author yuxi
 * @date 2020-10-04
 */
public interface IBdClassService extends IService<BdClass> {


    /**
     * 得到班级基础信息
     *
     * @param id 班级ID
     * @return 班级
     */
    BdClassBO selectBaseInfoById(Long id);

    /**
     * 新增班级
     *
     * @param bdClassVO 班级
     */
    void insertBdClass(BdClassVO bdClassVO);

    /**
     * 删除班级信息
     *
     * @param id 班级ID
     * @return 结果
     */
    boolean deleteBdClassById(Long id);

    /**
     * 教师id当主教的班级
     *
     * @param teacherId 教师id
     * @return String
     */
    String getBishopClasses(Long teacherId);

    /**
     * 教师id当助教的班级
     *
     * @param teacherId 教师id
     * @return String
     */
    String getHelpClasses(Long teacherId);

    /**
     * 得到所有班级
     *
     * @param bdClassVO 班级信息入参VO
     * @return List<BdClassBO>
     */
    List<BdClassBO> getAll(BdClassVO bdClassVO);

    /**
     * 得到所有班级
     *
     * @param bdClassVO 班级信息入参VO
     * @return List<BdClassBO>
     */
    Page<BdClassBO> getAllClass(BdClassVO bdClassVO);

    /**
     * 更新班级的已排课次
     *
     * @param bdClassRecord 上课记录信息
     * @return 是否更新成功
     */
    Boolean updateClassHasClass(BdClassRecord bdClassRecord);

    /**
     * 根据id全局搜索
     *
     * @param classId 学生ID
     * @return 学生信息
     */
    BdClassBO selectClassDetailById(Long classId);

    /**
     * 更新班级已有人数
     *
     * @param bdClass 班级信息
     * @return 是否更新成功
     */
    boolean updateClassById(BdClass bdClass);

    /**
     * 获取课程列表
     *
     * @param bdClassVO 获取课程详细信息列表
     * @return 列表数据
     */
    List<BdClassBO> getClassDetailedList(BdClassVO bdClassVO);

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
    Map<Long, Integer> getTotalTaught(List<Long> teacherIdList);

    /**
     * 获得该课程的班级
     *
     * @param classVO 该课程的班级
     * @return 该课程的班级
     */
    Page<BdClassBO> getClassList(BdClassVO classVO);

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
    List<BdTeacherBO> getClassesByTeacher(List<Long> idList);

    /**
     * 获得这些班级的学生数量以及班级可容纳数量
     *
     * @param classIds 班级ids
     * @return 学生数量以及班级可容纳数量
     */
    List<BdClassBO> selectStuNumByClsIds(List<Long> classIds);

    /**
     * 教师id当主教的班级
     *
     * @param bishop 老师Id
     * @return 集合
     */
    Integer getBishopClassCount(Long bishop);

    /**
     * 获得颜色，主教老师名称，辅助老师名称，班级人数
     *
     * @param classIds 班级集合
     * @return 获得颜色，主教老师名称，辅助老师名称，班级人数
     */
    List<BdClassBO> getClassInfoByClassIds(List<Long> classIds);

    /**
     * 获得其他班级
     * @param nowClassId 当前班级id
     * @return 其他班级列表
     */
    Page<BdClassBO> selectOtherClass(BdClassVO bdClassVO);
}
