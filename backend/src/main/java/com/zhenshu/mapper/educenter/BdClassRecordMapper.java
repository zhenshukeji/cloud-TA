package com.zhenshu.mapper.educenter;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassRecordBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassRecordVO;
import com.zhenshu.mapper.domain.educenter.BdClassRecord;

import java.util.List;

/**
 * 上课记录Mapper接口
 *
 * @author jing
 * @date 2020-09-25
 */
public interface BdClassRecordMapper extends BaseMapper<BdClassRecord> {

    /**
     * 上课记录
     *
     * @param bdClassRecordVO 上课记录入参VO
     * @return 上课记录集合
     */
    List<BdClassRecordBO> getClassRecordList(BdClassRecordVO bdClassRecordVO);

    /**
     * 上课记录
     *
     * @param bdClassRecordVO 上课记录入参VO
     * @return 数量
     */
    Integer getClassRecordListCount(BdClassRecordVO bdClassRecordVO);

    /**
     * 查询上课记录详细信息
     *
     * @param id 上课记录ID
     * @return 上课记录
     */
    BdClassRecordBO selectBdClassRecordDetailById(Long id);
}
