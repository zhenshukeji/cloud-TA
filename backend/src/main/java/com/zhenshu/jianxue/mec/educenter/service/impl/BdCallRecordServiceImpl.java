package com.zhenshu.jianxue.mec.educenter.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCallRecordBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCallRecordVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdCallRecordService;
import com.zhenshu.mapper.domain.educenter.BdCallRecord;
import com.zhenshu.mapper.educenter.BdCallRecordMapper;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 课消记录Service业务层处理
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Service
public class BdCallRecordServiceImpl extends ServiceImpl<BdCallRecordMapper, BdCallRecord> implements IBdCallRecordService {

    @Resource
    private BdCallRecordMapper callRecordMapper;

    /**
     * 查询课消记录
     *
     * @param callRecordId 课消记录ID
     * @return 课消记录
     */
    @Override
    public BdCallRecordBO selectDetailById(Long callRecordId) {
        return callRecordMapper.getCallRecordDetailById(callRecordId);
    }

    /**
     * 按主键读取课消记录并加行锁（FOR UPDATE，OS022）：
     * 纠错事务在「读状态 → 改状态」之间持有行锁，同一记录的并发纠错在此串行化。
     */
    @Override
    public BdCallRecord selectByIdForUpdate(Long callRecordId) {
        return callRecordMapper.selectByIdForUpdate(callRecordId);
    }

    /**
     * 新增课消记录
     *
     * @param bdCallRecordVO 课消记录
     * @return 结果
     */
    @Override
    public boolean insertBdCallRecord(BdCallRecordVO bdCallRecordVO) {
        BdCallRecord bdCallRecord = VOToPO(bdCallRecordVO);
        bdCallRecord.setCreateBy(SecurityUtils.getUserId().toString());
        bdCallRecord.setCreateTime(DateUtils.getNowDate());
        boolean flag = this.insert(bdCallRecord);
        bdCallRecordVO.setId(bdCallRecord.getId());
        return flag;
    }

    /**
     * 修改课消记录
     *
     * @param bdCallRecordVO 课消记录
     * @return 结果
     */
    @Override
    public boolean updateBdCallRecord(BdCallRecordVO bdCallRecordVO) {
        BdCallRecord bdCallRecord = VOToPO(bdCallRecordVO);
        bdCallRecord.setUpdateTime(DateUtils.getNowDate());
        bdCallRecord.setUpdateBy(SecurityUtils.getUserId().toString());
        return this.updateById(bdCallRecord);
    }


    /**
     * 将数据库对象转换为BO对象
     *
     * @param bdCallRecords 数据库对象集合
     * @return List<BdCallRecordBO> bo对象集合
     */
    public List<BdCallRecordBO> copyList(List<BdCallRecord> bdCallRecords) {
        List<BdCallRecordBO> resultList = new ArrayList<>();
        for (BdCallRecord source : bdCallRecords) {
            BdCallRecordBO bdCallRecordBO = new BdCallRecordBO();
            BeanUtils.copyBeanProp(bdCallRecordBO, source);
            resultList.add(bdCallRecordBO);
        }
        return resultList;
    }

    /**
     * 获得点名记录的详情
     *
     * @param classRecordId 上课记录信息
     * @return List<BdCallRecordBO>
     */
    @Override
    public List<BdCallRecordBO> getListByClassRecordId(Long classRecordId) {
        return callRecordMapper.getListByClassRecordId(classRecordId);
    }

    /**
     *  查询出该上课记录的到课人数
     * @param classRecordId 上课记录id
     * @return 到课人数
     */
    @Override
    public Integer selectNumOfAttendByClassRecordId(Long classRecordId, Integer type) {
        return callRecordMapper.selectNumOfAttendByClassRecordId(classRecordId, type);
    }

    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdCallRecordVO 入参VO
     * @return bdCallRecord
     */
    public BdCallRecord VOToPO(BdCallRecordVO bdCallRecordVO) {
        BdCallRecord bdCallRecord = new BdCallRecord();
        BeanUtils.copyBeanProp(bdCallRecord, bdCallRecordVO);
        SysUser user = SecurityUtils.getSysUser();
        bdCallRecord.setCampusId(user.getCampusId());
        bdCallRecord.setMecId(user.getMecId());
        bdCallRecord.setIsDelete(Constants.NOT_DELETED);
        return bdCallRecord;
    }

}
