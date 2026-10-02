package com.zhenshu.jianxue.mec.educenter.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassRecordBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassRecordVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassRecordService;
import com.zhenshu.mapper.domain.educenter.BdClassRecord;
import com.zhenshu.mapper.educenter.BdClassRecordMapper;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 上课记录Service业务层处理
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Service
public class BdClassRecordServiceImpl extends ServiceImpl<BdClassRecordMapper, BdClassRecord> implements IBdClassRecordService {

    @Resource
    BdClassRecordMapper classRecordMapper;

    /**
     * 查询上课记录列表
     *
     * @param bdClassRecordVO 上课记录
     * @return 上课记录
     */
    @Override
    public List<BdClassRecordBO> selectBdClassRecordList(BdClassRecordVO bdClassRecordVO) {
        SysUser user = SecurityUtils.getSysUser();
        bdClassRecordVO.setCampusId(user.getCampusId());
        bdClassRecordVO.setMecId(user.getMecId());
        bdClassRecordVO.setIsDelete(Constants.NOT_DELETED);
        return classRecordMapper.getClassRecordList(bdClassRecordVO);
    }

    /**
     * 将数据库对象转换为BO对象
     *
     * @param bdClassRecords 数据库对象集合
     * @return List<BdClassRecordBO> bo对象集合
     */
    public List<BdClassRecordBO> copyList(List<BdClassRecord> bdClassRecords) {
        List<BdClassRecordBO> resultList = new ArrayList<>();
        for (BdClassRecord source : bdClassRecords) {
            BdClassRecordBO bdClassRecordBO = new BdClassRecordBO();
            BeanUtils.copyBeanProp(bdClassRecordBO, source);
            resultList.add(bdClassRecordBO);
        }
        return resultList;
    }

    /**
     * 根据班级id 查询上课记录
     *
     * @param classId 班级id
     * @return 上课记录列表
     */
    @Override
    public List<BdClassRecordBO> getAllByClassId(Long classId) {
        EntityWrapper<BdClassRecord> wrapper = new EntityWrapper<>();
        wrapper.eq("class_id", classId);
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        List<BdClassRecord> bdClassRecords = this.selectList(wrapper);
        return copyList(bdClassRecords);
    }

    /**
     * 查询上课记录详细信息
     *
     * @param id 上课记录ID
     * @return 上课记录
     */
    @Override
    public BdClassRecordBO selectBdClassRecordDetailById(Long id) {
        return classRecordMapper.selectBdClassRecordDetailById(id);
    }

    /**
     * 判断该堂课是否点名过
     * @param checkRollCall 班级id 上课开始时间 上课结束时间
     * @return 是否点名过
     */
    @Override
    public boolean checkRollCall(BdClassRecord checkRollCall) {
        EntityWrapper<BdClassRecord> wrapper = new EntityWrapper<>();
        wrapper.eq("class_id", checkRollCall.getClassId());
        wrapper.eq("class_start_time", checkRollCall.getClassStartTime());
        wrapper.eq("class_end_time", checkRollCall.getClassEndTime());
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        BdClassRecord classRecord = this.selectOne(wrapper);
        return classRecord != null;
    }

}
