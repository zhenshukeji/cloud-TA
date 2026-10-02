package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassTimeBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassTimeVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.ClassTimeVO;
import com.zhenshu.mapper.domain.educenter.BdClassTime;

import java.util.List;
import java.util.Map;

/**
 * 班级上课时间 Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdClassTimeService extends IService<BdClassTime> {


    /**
     * 新增班级上课时间
     *
     * @param bdClassTimeVO 班级上课时间
     * @return 结果
     */
    boolean insertBdClassTime(BdClassTimeVO bdClassTimeVO);

    /**
     * 删除班级上课时间 信息
     *
     * @param id 班级上课时间 ID
     * @return 结果
     */
    boolean deleteBdClassTimeById(Long id);

    /**
     * 查询所有无分页
     *
     * @param bdClassTimeVO 班级上课时间入参VO
     * @return List<BdClassTimeBO>
     */
    List<BdClassTimeBO> getAll(BdClassTimeVO bdClassTimeVO);

    /**
     * 得到某班级的所有上课时间
     *
     * @param classId 班级id
     * @return List<BdClassTimeBO>
     */
    List<BdClassTimeBO> getClassTimeByClassId(Long classId);

    /**
     * 根据班级id与周几修改班级上课时间
     *
     * @param bdClassTimeVO 班级上课时间
     * @return 结果
     */
    boolean updateById(BdClassTimeVO bdClassTimeVO);

    /**
     * 课表里的获取班级上课时间
     *
     * @param classTimeVO  班级id
     * @param weekDays 星期列表
     */
    List<BdClassTimeBO> selectByTimeTable(ClassTimeVO classTimeVO, List<Integer> weekDays);

    /**
     * 获取多个班级的上课时间
     */
    Map<Long, List<String>> selectListByClassIds(Long[] classIds);

}
