package com.zhenshu.jianxue.mec.educenter.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassChangeBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassChangeVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassChangeService;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.mapper.domain.educenter.BdClassChange;
import com.zhenshu.mapper.educenter.BdClassChangeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 上课临时更改记录 Service业务层处理
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Service
public class BdClassChangeServiceImpl extends ServiceImpl<BdClassChangeMapper, BdClassChange> implements IBdClassChangeService {

    @Resource
    BdClassChangeMapper classChangeMapper;

    /**
     * 新增上课临时更改记录
     *
     * @param bdClassChangeVO 上课临时更改记录
     * @return 结果
     */
    @Override
    public boolean insertBdClassChange(BdClassChangeVO bdClassChangeVO) {
        BdClassChange bdClassChange = voToPo(bdClassChangeVO);
        bdClassChange.setCreateTime(DateUtils.getNowDate());
        bdClassChange.setCreateBy(SecurityUtils.getUserId().toString());
        bdClassChange.setVersion(Constants.VERSION);
        boolean result = this.insert(bdClassChange);
        bdClassChangeVO.setId(bdClassChange.getId());
        return result;
    }

    /**
     * 修改上课临时更改记录
     *
     * @param bdClassChangeVO 上课临时更改记录
     * @return 结果
     */
    @Override
    public boolean updateBdClassChange(BdClassChangeVO bdClassChangeVO) {
        BdClassChange bdClassChange = new BdClassChange();
        BeanUtils.copyBeanProp(bdClassChange, bdClassChangeVO);
        bdClassChange.setUpdateTime(DateUtils.getNowDate());
        bdClassChange.setUpdateBy(SecurityUtils.getUserId().toString());
        return this.updateById(bdClassChange);
    }

    /**
     * 删除上课临时更改记录 信息
     *
     * @param id 上课临时更改记录 ID
     * @return 结果
     */
    @Override
    public boolean deleteBdClassChangeById(Long id) {
        BdClassChange bdClassChange = new BdClassChange();
        bdClassChange.setIsDelete(Constants.DELETED);
        bdClassChange.setId(id);
        bdClassChange.setUpdateBy(SecurityUtils.getUserId().toString());
        bdClassChange.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(bdClassChange);
    }

    /**
     * 查询调课调到本周的调课记录
     *
     * @param vo 上课临时更改记录
     * @return 调课调到本周的调课记录
     */
    @Override
    public List<BdClassChangeBO> changeToThisWeek(BdClassChangeVO vo) {
        return classChangeMapper.changeToThisWeek(vo);
    }


    /**
     * 查询调课调到其他周的调课记录
     *
     * @param vo 上课临时更改记录
     * @return 查询调课调到其他周的调课记录
     */
    @Override
    public List<BdClassChangeBO> selectChangeToOther(BdClassChangeVO vo) {
        return classChangeMapper.selectChangeToOther(vo);
    }

    /***
     * 通过班级上课表id将调课中大于当前系统日期的调课进行取消
     * @param classTimeId 班级上课表id
     */
    @Override
    public void deleteClassChangeByClassTimeId(long classTimeId) {
        List<BdClassChangeBO> list = classChangeMapper.selectByClassTimeIdAndAfterNow(classTimeId);
        if (!list.isEmpty()) {
            for (BdClassChangeBO bo : list) {
                deleteBdClassChangeById(bo.getId());
            }
        }
    }

    /**
     * 将数据库对象转换为BO对象
     *
     * @param bdClassChanges 数据库对象集合
     * @return List<BdClassChangeBO> bo对象集合
     */
    public List<BdClassChangeBO> copyList(List<BdClassChange> bdClassChanges) {
        List<BdClassChangeBO> resultList = new ArrayList<>();
        for (BdClassChange source : bdClassChanges) {
            BdClassChangeBO bdClassChangeBO = new BdClassChangeBO();
            BeanUtils.copyBeanProp(bdClassChangeBO, source);
            resultList.add(bdClassChangeBO);
        }
        return resultList;
    }

    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdClassChangeVO 入参VO
     * @return bdCallRecord
     */
    public BdClassChange voToPo(BdClassChangeVO bdClassChangeVO) {
        BdClassChange bdClassChange = new BdClassChange();
        BeanUtils.copyBeanProp(bdClassChange, bdClassChangeVO);
        SysUser user = SecurityUtils.getSysUser();
        bdClassChange.setCampusId(user.getCampusId());
        bdClassChange.setMecId(user.getMecId());
        bdClassChange.setIsDelete(Constants.NOT_DELETED);
        return bdClassChange;
    }

    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param classChangeVO 入参VO
     * @return bdCallRecord
     */
    public BdClassChangeVO setVoId(BdClassChangeVO classChangeVO) {
        SysUser user = SecurityUtils.getSysUser();
        classChangeVO.setCampusId(user.getCampusId());
        classChangeVO.setMecId(user.getMecId());
        classChangeVO.setIsDelete(Constants.NOT_DELETED);
        return classChangeVO;
    }
}
