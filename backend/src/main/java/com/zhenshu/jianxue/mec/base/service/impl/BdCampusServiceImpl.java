package com.zhenshu.jianxue.mec.base.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.base.CampusEnum;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusVO;
import com.zhenshu.jianxue.mec.base.service.IBdCampusService;
import com.zhenshu.mapper.base.BdCampusMapper;
import com.zhenshu.mapper.domain.base.BdCampus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 校区Service业务层处理
 *
 * @author jing
 * @date 2020-10-04
 */
@Service
public class BdCampusServiceImpl extends ServiceImpl<BdCampusMapper, BdCampus> implements IBdCampusService {

    @Resource
    private BdCampusMapper bdCampusMapper;

    /**
     * 查询校区
     *
     * @param id 校区ID
     * @return 校区
     */
    @Override
    public BdCampusBO selectBdCampusById(Long id) {
        BdCampus result = this.selectById(id);
        BdCampusBO bdCampusBO = new BdCampusBO();
        BeanUtils.copyBeanProp(bdCampusBO, result);
        return bdCampusBO;
    }

    /**
     * 获得校区详细信息
     * @param id 校区ID
     * @return 校区
     */
    @Override
    public BdCampusBO selectBdCampusDetailById(Long id) {
        return bdCampusMapper.selectBdCampusDetailById(id);
    }

    /**
     * 新增校区
     *
     * @param bdCampusVO 校区
     * @return 结果
     */
    @Override
    public boolean insertBdCampus(BdCampusVO bdCampusVO) {
        BdCampus bdCampus = new BdCampus();
        BeanUtils.copyBeanProp(bdCampus, bdCampusVO);
        bdCampus.setMecId(SecurityUtils.getLoginMecId());
        bdCampus.setCreateTime(DateUtils.getNowDate());
        bdCampus.setIsDelete(Constants.NOT_DELETED);
        bdCampus.setCreateBy(SecurityUtils.getUserId().toString());
        boolean result = this.insert(bdCampus);
        bdCampusVO.setId(bdCampus.getId());
        return result;
    }

    /**
     * 修改校区
     *
     * @param bdCampusVO 校区
     * @return 结果
     */
    @Override
    public boolean updateBdCampus(BdCampusVO bdCampusVO) {
        BdCampus bdCampus = new BdCampus();
        BeanUtils.copyBeanProp(bdCampus, bdCampusVO);
        bdCampus.setUpdateTime(DateUtils.getNowDate());
        bdCampus.setUpdateBy(SecurityUtils.getUserId().toString());

        return this.updateById(bdCampus);
    }


    /**
     * 删除校区信息
     *
     * @param id 校区ID
     * @return 结果
     */
    @Override
    public boolean deleteBdCampusById(Long id) {
        BdCampus bdCampus = new BdCampus();
        bdCampus.setIsDelete(Constants.DELETED);
        bdCampus.setId(id);
        bdCampus.setUpdateTime(DateUtils.getNowDate());
        bdCampus.setUpdateBy(SecurityUtils.getUserId().toString());
        return this.updateById(bdCampus);
    }

    /**
     * 根据机构id查询校区列表
     *
     * @param bdCampusVO 校区参数
     * @param mecId      机构id
     * @return 返回信息
     */
    @Override
    public List<BdCampusBO> selectBdCampusList(BdCampusVO bdCampusVO, Long mecId) {
        bdCampusVO.setMecId(mecId);
        return bdCampusMapper.selectBdCampusList(bdCampusVO);
    }

    /**
     * 获得机构的校区信息
     *
     * @param campusVO 机构id,校区名称，校区负责人，联系方式
     * @return 校区信息
     */
    @Override
    public List<BdCampusBO> getCampusInfoByMecId(BdCampusVO campusVO) {
        return bdCampusMapper.getCampusInfoByMecId(campusVO);
    }

    /**
     * 一个机构不能拥有相同账号的校区登录账号
     * @param mgrPhone 登录账户
     * @return 是否拥有
     */
    @Override
    public boolean checkRepeatMgrPhone(String mgrPhone) {
        EntityWrapper<BdCampus> wrapper = new EntityWrapper<>();
        wrapper.eq("mgr_phone", mgrPhone);
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        wrapper.eq("mec_id", SecurityUtils.getLoginMecId());
        BdCampus campus = this.selectOne(wrapper);
        return campus != null;
    }

}
