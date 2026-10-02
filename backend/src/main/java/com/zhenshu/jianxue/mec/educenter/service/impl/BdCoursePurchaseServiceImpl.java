package com.zhenshu.jianxue.mec.educenter.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCoursePurchaseBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCoursePurchaseVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdCoursePurchaseService;
import com.zhenshu.mapper.domain.educenter.BdCoursePurchase;
import com.zhenshu.mapper.educenter.BdCoursePurchaseMapper;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 课程购买记录 Service业务层处理
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Service
public class BdCoursePurchaseServiceImpl extends ServiceImpl<BdCoursePurchaseMapper, BdCoursePurchase> implements IBdCoursePurchaseService {

    @Resource
    private BdCoursePurchaseMapper bdCoursePurchaseMapper;


    /**
     * 新增课程购买记录
     *
     * @param bdCoursePurchaseVO 课程购买记录
     * @return 结果
     */
    @Override
    public boolean insertBdCoursePurchase(BdCoursePurchaseVO bdCoursePurchaseVO) {
        BdCoursePurchase bdCoursePurchase = VOToPO(bdCoursePurchaseVO);
        bdCoursePurchase.setStatus(Constants.YES);
        bdCoursePurchase.setCreateTime(DateUtils.getNowDate());
        bdCoursePurchase.setCreateBy(SecurityUtils.getUserId().toString());
        return this.insert(bdCoursePurchase);
    }

    /**
     * 根据学生id 获取课程购买记录
     *
     * @param studentId 学生id
     */
    @Override
    public List<BdCoursePurchaseBO> selectListByStudentId(Long studentId) {
        return bdCoursePurchaseMapper.selectListByStudentId(studentId);
    }


    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdCoursePurchaseVO 入参VO
     * @return bdCallRecord
     */
    public BdCoursePurchase VOToPO(BdCoursePurchaseVO bdCoursePurchaseVO) {
        BdCoursePurchase bdCoursePurchase = new BdCoursePurchase();
        BeanUtils.copyBeanProp(bdCoursePurchase, bdCoursePurchaseVO);
        SysUser user = SecurityUtils.getSysUser();
        bdCoursePurchase.setCampusId(user.getCampusId());
        bdCoursePurchase.setMecId(user.getMecId());
        bdCoursePurchase.setIsDelete(Constants.NOT_DELETED);
        return bdCoursePurchase;
    }

}
