package com.zhenshu.mapper.educenter;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdAskLeaveBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdAskLeaveVO;
import com.zhenshu.mapper.domain.educenter.BdAskLeave;

import java.util.List;

/**
 * 学生请假Mapper接口
 *
 * @author jing
 * @date 2020-09-25
 */
public interface BdAskLeaveMapper extends BaseMapper<BdAskLeave>
{

    /**
     * 使用模糊查询
     * @param bdAskLeaveVO 请假入参vo
     * @return List<BdAskLeaveBO>
     */
    List<BdAskLeaveBO> selectAskLeaveList(BdAskLeaveVO bdAskLeaveVO);

    /**
     * 通过上课时间查找请假的学生
     *
     * @param askLeave 具有这周这节课的上课时间
     * @return List<BdAskLeaveBO>
     */
    List<BdAskLeaveBO> selectAskLeaveByTime(BdAskLeaveVO askLeave);

    /**
     * 根据id 查询请假详细信息
     * @param askLeaveId 请假id
     * @return 请假详细信息
     */
    BdAskLeaveBO selectAskLeaveInfoById(Long askLeaveId);

}
