package com.zhenshu.mapper.educenter;

import java.util.List;

import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCallRecordBO;
import com.zhenshu.mapper.domain.educenter.BdCallRecord;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 课消记录 Elimination of class Mapper接口
 *
 * @author jing
 * @date 2020-09-25
 */
public interface BdCallRecordMapper extends BaseMapper<BdCallRecord> {

    /**
     * 获得点名记录的详情
     *
     * @param classRecordId 上课记录信息
     * @return List<BdCallRecordBO>
     */
    List<BdCallRecordBO> getListByClassRecordId(Long classRecordId);

    /**
     * 查询课消记录
     *
     * @param callRecordId 课消记录ID
     * @return 课消记录
     */
    BdCallRecordBO getCallRecordDetailById(Long callRecordId);

    /**
     *  查询出该上课记录的到课人数
     * @param classRecordId 上课记录id
     * @return 到课人数
     */
    Integer selectNumOfAttendByClassRecordId(@Param("classRecordId") Long classRecordId, @Param("type") Integer type);

    /**
     * 按主键读取课消记录并加行锁（FOR UPDATE，OS022）：
     * 纠错事务在读取状态与写入之间用行锁串行化，杜绝并发双重纠错。
     *
     * @param id 课消记录id
     * @return 课消记录（已锁定该行直到事务结束）
     */
    BdCallRecord selectByIdForUpdate(@Param("id") Long id);
}
