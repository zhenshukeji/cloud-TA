package com.zhenshu.mapper.educenter;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdStudentBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdStudentClassBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdStudentClassVO;
import com.zhenshu.mapper.domain.educenter.BdStudentClass;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生班级中间表Mapper接口
 *
 * @author jing
 * @date 2020-09-25
 */
public interface BdStudentClassMapper extends BaseMapper<BdStudentClass> {

    /**
     * 模糊查询 得到在该班级的学生
     *
     * @param bdClassVO 模糊查询
     * @return List<BdStudentClassBO>
     */
    List<BdStudentBO> selectStudentByClass(BdClassVO bdClassVO);

    /**
     * 模糊查询 得到在该班级的学生数量
     *
     * @param bdStudentClassVO 模糊查询
     * @return long
     */
    Integer selectStudentByClassCount(BdStudentClassVO bdStudentClassVO);

    /**
     * 获得学生的班级
     *
     * @param studentIdList 学生ids
     * @param mecId         校构id
     * @param campusId      校区id
     * @return 学生所在的班级集合
     */
    List<BdStudentClassBO> selectClassByStudentIds(@Param("studentIdList") List<Long> studentIdList, @Param("mecId") Long mecId, @Param("campusId") Long campusId);

    /**
     * 得到班级里面的学生总数
     * @param classIds 班级ids
     * @return  学生总数
     */
    List<BdStudentClassBO> selectStuCountByClsIds(@Param("classIds")  List<Long> classIds);

    /**
     * 查询该班级上课的学生（去过滤请假的学生）
     * @param classId 班级id
     * @param clsStartTime 班级上课的开始时间
     * @param clsEndTime 班级上课的结束时间
     * @return 查询该班级上课的学生（去掉请假的学生）
     */
    List<BdStudentClassBO> getStudentByClassIdFilterAskLeave(@Param("classId") Long classId, @Param("clsStartTime") String clsStartTime, @Param("clsEndTime")String clsEndTime);

    /**
     * 获得该班级的所有学生
     * @param classId 班级id
     * @return 所有学生
     */
    List<String> getAllStudentByClassId(Long classId);

    /**
     * 得到所有的学生班级
     *
     * @param bdStudentClassVO 学生班级中间入参VO
     * @return List<BdStudentClassBO>
     */
    List<BdStudentClassBO> getStudentsByClassId(BdStudentClassVO bdStudentClassVO);
}
