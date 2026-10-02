package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdAskLeaveBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdAskLeaveVO;
import com.zhenshu.mapper.domain.educenter.BdAskLeave;

import java.util.List;

/**
 * 学生请假Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdAskLeaveService extends IService<BdAskLeave> {

    /**
     * 查询学生请假
     *
     * @param id 学生请假ID
     * @return 学生请假
     */
    BdAskLeaveBO selectBdAskLeaveById(Long id);

    /**
     * 分页查询学生请假列表
     *
     * @param bdAskLeaveVO 学生请假
     * @return 分页学生请假
     */
    List<BdAskLeaveBO>  selectBdAskLeaveList(BdAskLeaveVO bdAskLeaveVO);

    /**
     * 修改学生请假
     *
     * @param bdAskLeaveVO 学生请假
     * @return 结果
     */
    boolean updateBdAskLeave(BdAskLeaveVO bdAskLeaveVO);

    /**
     * 通过上课时间查找请假的学生
     *
     * @param endInWeek   这周这节课的结束上课时间
     * @return List<BdAskLeaveBO>
     */
    List<BdAskLeaveBO> selectStudentAtTime( String endInWeek, String startInWeek);

}
