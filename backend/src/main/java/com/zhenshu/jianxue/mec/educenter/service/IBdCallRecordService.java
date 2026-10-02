package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCallRecordBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCallRecordVO;
import com.zhenshu.mapper.domain.educenter.BdCallRecord;

import java.util.List;

/**
 * 课消记录Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdCallRecordService extends IService<BdCallRecord> {

    /**
     * 查询课消记录
     *
     * @param id 课消记录ID
     * @return 课消记录
     */
    BdCallRecordBO selectDetailById(Long id);

    /**
     * 按主键读取课消记录并加行锁（FOR UPDATE，OS022）。
     *
     * @param id 课消记录ID
     * @return 课消记录
     */
    BdCallRecord selectByIdForUpdate(Long id);

    /**
     * 新增课消记录
     *
     * @param bdCallRecordVO 课消记录
     * @return 结果
     */
    boolean insertBdCallRecord(BdCallRecordVO bdCallRecordVO);

    /**
     * 修改课消记录
     *
     * @param classRecordId 课消记录
     * @return 结果
     */
    boolean updateBdCallRecord(BdCallRecordVO classRecordId);

    /**
     * 获得点名记录的详情
     *
     * @param classRecordId 点名记录信息
     * @return List<BdCallRecordBO>
     */
    List<BdCallRecordBO> getListByClassRecordId(Long classRecordId);

    /**
     *  查询出该上课记录的到课人数
     * @param classRecordId 上课记录id
     * @return 到课人数
     */
    Integer selectNumOfAttendByClassRecordId(Long classRecordId, Integer type);
}
