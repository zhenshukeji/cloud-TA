package com.zhenshu.jianxue.mec.base.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.base.MecEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.common.utils.page.PageUtils;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMechanismBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdMechanismVO;
import com.zhenshu.jianxue.mec.base.service.IBdMechanismService;
import com.zhenshu.mapper.base.BdMechanismMapper;
import com.zhenshu.mapper.domain.base.BdMechanism;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 机构信息Service业务层处理
 *
 * @author jing
 * @date 2020-10-04
 */
@Service
public class BdMechanismServiceImpl extends ServiceImpl<BdMechanismMapper, BdMechanism> implements IBdMechanismService {

    @Resource
    private BdMechanismMapper mechanismMapper;

    /**
     * 查询机构信息
     *
     * @param id 机构信息ID
     * @return 机构信息
     */
    @Override
    public BdMechanismBO selectBdMechanismById(Long id) {
        BdMechanism bdMechanism = this.selectById(id);
        if (bdMechanism == null){
            return null;
        }
        BdMechanismBO bdMechanismBO = new BdMechanismBO();
        BeanUtils.copyBeanProp(bdMechanismBO, bdMechanism);
        return bdMechanismBO;
    }

    /**
     * 查询机构信息列表
     *
     * @param bdMechanismVO 机构信息
     * @return 机构信息
     */
    @Override
    public Page<BdMechanismBO> selectBdMechanismList(BdMechanismVO bdMechanismVO) {
        BdMechanism bdMechanism = new BdMechanism();
        BeanUtils.copyBeanProp(bdMechanism, bdMechanismVO);
        EntityWrapper<BdMechanism> wrapper = new EntityWrapper<>();
        //设置机构与校区id
        bdMechanism.setIsDelete(Constants.NOT_DELETED);

        wrapper.setEntity(bdMechanism);
        return page(bdMechanismVO, wrapper);
    }

    public Page<BdMechanismBO> page(BdMechanismVO bdMechanismVO, EntityWrapper<BdMechanism> wrapper) {
        Page<BdMechanism> page = this.selectPage(new Page<>(bdMechanismVO.getPageNum(), bdMechanismVO.getPageSize()), wrapper);
        List<BdMechanismBO> resultList = new ArrayList<>();

        for (BdMechanism source : page.getRecords()) {
            BdMechanismBO bdMechanismBO = new BdMechanismBO();
            BeanUtils.copyBeanProp(bdMechanismBO, source);
            resultList.add(bdMechanismBO);
        }
        // 重新组装page
        return new PageUtils<BdMechanismBO>().getPage(resultList, this.selectCount(wrapper));
    }

    /**
     * 新增机构信息
     *
     * @param bdMechanismVO 机构信息
     * @return 结果
     */
    @Override
    public boolean insertBdMechanism(BdMechanismVO bdMechanismVO) {
        BdMechanism bdMechanism = new BdMechanism();
        bdMechanismVO.setIsDelete(Constants.NOT_DELETED);
        bdMechanismVO.setCreationTime(new Date());
        bdMechanismVO.setCreator(SecurityUtils.getUserId());

        BeanUtils.copyBeanProp(bdMechanism, bdMechanismVO);
        //设置机构id与校区id
        boolean result = this.insert(bdMechanism);
        bdMechanismVO.setId(bdMechanism.getId());
        return result;
    }

    /**
     * 修改机构信息
     *
     * @param bdMechanismVO 机构信息
     * @return 结果
     */
    @Override
    public boolean updateBdMechanism(BdMechanismVO bdMechanismVO) {
        BdMechanism bdMechanism = new BdMechanism();
        BeanUtils.copyBeanProp(bdMechanism, bdMechanismVO);
        EntityWrapper<BdMechanism> wrapper = new EntityWrapper<>();
        bdMechanismVO.setModifier(SecurityUtils.getUserId());
        bdMechanismVO.setUpdateTime(new Date());
        wrapper.eq("id", bdMechanism.getId());
        return this.update(bdMechanism, wrapper);
    }

    /**
     * 删除机构信息信息
     *
     * @param id 机构信息ID
     * @return 结果
     */
    @Override
    public boolean deleteBdMechanismById(Long id) {
        BdMechanism bdMechanism = new BdMechanism();
        bdMechanism.setIsDelete(Constants.DELETED);
        bdMechanism.setId(id);
        bdMechanism.setUpdateBy(SecurityUtils.getUserId().toString());
        bdMechanism.setUpdateTime(new Date());
        return this.updateById(bdMechanism);
    }

    /**
     * 根据机构名称进行查询
     *
     * @param mecName 机构名称
     * @return 机构信息
     */
    @Override
    public BdMechanismBO selectByName(String mecName) {
        BdMechanism mechanism = new BdMechanism();
        // 完全匹配机构名称
        mechanism.setName(mecName);
        mechanism.setIsDelete(Constants.NOT_DELETED);

        EntityWrapper<BdMechanism> wrapper = new EntityWrapper<>();
        BdMechanism bdMechanism = this.selectOne(wrapper);
        BdMechanismBO bdMechanismBO = new BdMechanismBO();
        if (bdMechanism != null) {
            BeanUtils.copyBeanProp(bdMechanismBO, bdMechanism);
            return bdMechanismBO;
        }
        return null;
    }

    /**
     * 获得机构详情
     * @author tyh
     * @param id 机构id
     * @return
     */
    @Override
    public BdMechanismBO queryMechanismInFoById(Long id) {
        BdMechanismBO bdMechanismBO = mechanismMapper.queryMechanismInFoById(id);
        if (bdMechanismBO == null){
            throw new CustomException("id 对应的机构信息不存在");
        }
        return bdMechanismBO;
    }

}
