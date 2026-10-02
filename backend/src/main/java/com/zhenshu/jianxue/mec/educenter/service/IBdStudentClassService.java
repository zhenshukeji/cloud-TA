package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdStudentBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdStudentClassBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdStudentClassVO;
import com.zhenshu.mapper.domain.educenter.BdStudentClass;

import java.util.List;

/**
 * 学生班级中间Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdStudentClassService extends IService<BdStudentClass> {

    /**
     * 查询学生班级中间
     *
     * @param id 学生班级中间ID
     * @return 学生班级中间
     */
    BdStudentClassBO selectBdStudentClassById(Long id);

    /**
     * 分页查询学生班级中间列表
     *
     * @param bdStudentClassVO 学生班级中间
     * @return 分页学生班级中间
     */
    Page<BdStudentClassBO> selectBdStudentClassList(BdStudentClassVO bdStudentClassVO);

    /**
     * 分页查询学生班级中间列表
     *
     * @param classVO 学生班级中间 模糊查询
     * @return 分页学生班级中间
     */
    List<BdStudentBO> selectStudentByClass(BdClassVO classVO);

    /**
     * 新增学生班级中间
     *
     * @param bdStudentClassVO 学生班级中间
     * @return 结果
     */
    boolean insertBdStudentClass(BdStudentClassVO bdStudentClassVO);

    /**
     * 修改学生班级中间
     *
     * @param bdStudentClassVO 学生班级中间
     * @return 结果
     */
    boolean updateBdStudentClass(BdStudentClassVO bdStudentClassVO);

    /**
     * 删除学生班级中间信息
     *
     * @param id 学生班级中间ID
     * @return 结果
     */
    boolean deleteBdStudentClassById(Long id);

    /**
     * 查询所有无分页
     *
     * @param bdStudentClassVO 学生班级
     * @return List<BdStudentClassBO>
     */
    List<BdStudentClassBO> getAll(BdStudentClassVO bdStudentClassVO);

    /**
     * 查询所有无分页
     *
     * @param classId 学生班级
     * @return List<BdStudentClassBO>
     */
    List<BdStudentClassBO> getAllByClassId(Long classId);

    /**
     * 班级移除学生
     *
     * @param studentClassVO 班级id，学生id
     * @return boolean
     */
    boolean deleteStu(BdStudentClassVO studentClassVO);

    /**
     * 查看该学生是否在该班级
     *
     * @param studentClassVO 学生，班级
     * @return 学生班级信息
     */
    BdStudentClassBO selectByStuCls(BdStudentClassVO studentClassVO);

    /**
     * 得到学生所在的班级
     *
     * @param studentIdList 学生ids
     * @return 学生所在的班级集合
     */
    List<BdStudentClassBO> selectClassByStudentIds(List<Long> studentIdList);

    /**
     * 得到班级里面的学生总数
     *
     * @param classIds 班级ids
     * @return 学生总数
     */
    List<BdStudentClassBO> selectStuCountByClsIds(List<Long> classIds);

    /**
     * 查询该班级上课的学生（去过滤请假的学生）
     * @param classId 班级id
     * @param clsStartTime 班级上课的开始时间
     * @param clsEndTime 班级上课的结束时间
     * @return 查询该班级上课的学生（去掉请假的学生）
     */
    List<BdStudentClassBO> getStudentByClassIdFilterAskLeave(Long classId, String clsStartTime, String clsEndTime);

    /**
     * 获得该班级的所有学生
     * @param classId 班级id
     * @return
     */
    List<String> getAllStudentByClassId(Long classId);

    /**
     * 获得班级id集合的学生
     * @param classIdList 班级id集合
     * @return 学生集合
     */
    List<BdStudentClass> selectStuByClassIds(List<Long> classIdList);
}
