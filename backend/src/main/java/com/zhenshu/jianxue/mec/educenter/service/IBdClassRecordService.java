package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassRecordBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassRecordVO;
import com.zhenshu.mapper.domain.educenter.BdClassRecord;

import java.util.List;

/**
 * 上课记录Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdClassRecordService extends IService<BdClassRecord> {

    /**
     * 分页查询上课记录列表
     *
     * @param bdClassRecordVO 上课记录
     * @return 分页上课记录
     */
    List<BdClassRecordBO> selectBdClassRecordList(BdClassRecordVO bdClassRecordVO);

    /**
     * 根据班级id 查询上课记录
     * @param classId 班级id
     * @return 上课记录列表
     */
    List<BdClassRecordBO> getAllByClassId(Long classId);

    /**
     * 查询上课记录详细信息
     *
     * @param id 上课记录ID
     * @return 上课记录
     */
    BdClassRecordBO selectBdClassRecordDetailById(Long id);

    /**
     * 判断该堂课是否点名过
     * @param checkRollCall 班级id 上课开始时间 上课结束时间
     * @return 是否点名过
     */
    boolean checkRollCall(BdClassRecord checkRollCall);
}
