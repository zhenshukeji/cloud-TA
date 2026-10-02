package com.zhenshu.mapper.educenter;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassTimeBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.ClassTimeVO;
import com.zhenshu.mapper.domain.educenter.BdClassTime;

import java.util.List;

/**
 * 班级上课时间 Mapper接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface BdClassTimeMapper extends BaseMapper<BdClassTime> {

    /**
     * 查询班级上课时间
     *
     * @param classTimeVO 课程名称 班级名称
     * @return 班级上课时间
     */
    List<BdClassTimeBO> selectAll(ClassTimeVO classTimeVO);

    /**
     * 课表里查询班级上课时间
     * @param classTimeVO 入参列表
     * @return 班级上课时间
     */
    List<BdClassTimeBO> selectByTimeTable(ClassTimeVO classTimeVO);
}
