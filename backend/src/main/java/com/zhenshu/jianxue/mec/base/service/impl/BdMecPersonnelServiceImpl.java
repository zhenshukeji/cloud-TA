package com.zhenshu.jianxue.mec.base.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMecPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdMecPersonnelVO;
import com.zhenshu.jianxue.mec.base.service.IBdMecPersonnelService;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.constant.MecConstant;
import com.zhenshu.common.enums.base.MecPersonEnum;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.common.utils.page.PageUtils;
import com.zhenshu.mapper.base.BdMecPersonnelMapper;
import com.zhenshu.mapper.domain.base.BdMecPersonnel;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 机构人员 Service业务层处理
 *
 * @author jing
 * @date 2020-10-04
 */
@Service
public class BdMecPersonnelServiceImpl extends ServiceImpl<BdMecPersonnelMapper, BdMecPersonnel> implements IBdMecPersonnelService {


    /**
     * 查询机构人员
     *
     * @param id 机构人员 ID
     * @return 机构人员
     */
    @Override
    public BdMecPersonnelBO selectBdMecPersonnelById(Long id) {
        BdMecPersonnel bdMecPersonnel = new BdMecPersonnel();
        //未删除的 机构id，校区id
        bdMecPersonnel.setIsDelete(Constants.NOT_DELETED);
        bdMecPersonnel.setId(id);

        EntityWrapper<BdMecPersonnel> wrapper = new EntityWrapper<>();
        wrapper.setEntity(bdMecPersonnel);
        bdMecPersonnel = this.selectOne(wrapper);
        if (bdMecPersonnel == null) {
            return null;
        }
        BdMecPersonnelBO bdMecPersonnelBO = new BdMecPersonnelBO();
        BeanUtils.copyBeanProp(bdMecPersonnelBO, bdMecPersonnel);
        return bdMecPersonnelBO;
    }

    /**
     * 查询机构人员 列表
     *
     * @param bdMecPersonnelVO 机构人员
     * @return 机构人员
     */
    @Override
    public Page<BdMecPersonnelBO> selectBdMecPersonnelList(BdMecPersonnelVO bdMecPersonnelVO) {
        BdMecPersonnel bdMecPersonnel = new BdMecPersonnel();
        BeanUtils.copyBeanProp(bdMecPersonnel, bdMecPersonnelVO);
        EntityWrapper<BdMecPersonnel> wrapper = new EntityWrapper<>();
        return getPage(bdMecPersonnel, bdMecPersonnelVO, wrapper);
    }

    /**
     * 进行分页
     *
     * @param bdMecPersonnel   数据库对象 设置查询参数
     * @param bdMecPersonnelVO 入参对象
     * @param wrapper          查询参数
     * @return Page<BdMecPersonnelBO>分页好的出参
     */
    public Page<BdMecPersonnelBO> getPage(BdMecPersonnel bdMecPersonnel, BdMecPersonnelVO bdMecPersonnelVO, EntityWrapper<BdMecPersonnel> wrapper) {
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        if (!StringUtils.isEmpty(bdMecPersonnel.getName())) {
            wrapper.like("name", bdMecPersonnel.getName());
        }
        if (!StringUtils.isEmpty(bdMecPersonnel.getPhone())) {
            wrapper.like("phone", bdMecPersonnel.getPhone());
        }
        wrapper.eq("type", MecPersonEnum.ORDINARY_PEOPLE.getCode());
        wrapper.eq("mec_id", SecurityUtils.getLoginMecId());
        wrapper.orderBy("create_time", false);
        // 添加id倒序，为了避免出现同时间内出现数据自由排序的情况
        wrapper.orderBy("id", false);
        Page<BdMecPersonnel> page = this.selectPage(new Page<>(bdMecPersonnelVO.getPageNum(), bdMecPersonnelVO.getPageSize()), wrapper);
        List<BdMecPersonnelBO> resultList = copyList(page.getRecords());
        // 重新组装page
        return new PageUtils<BdMecPersonnelBO>().getPage(resultList, this.selectCount(wrapper));
    }

    /**
     * 无分页无参数列表
     */
    @Override
    public List<BdMecPersonnelBO> selectBdMecPersonnelList() {
        EntityWrapper<BdMecPersonnel> wrapper = new EntityWrapper<>();
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        return copyList(this.selectList(wrapper));
    }

    /**
     * 新增机构人员
     *
     * @param bdMecPersonnelVO 机构人员
     * @return 结果
     */
    @Override
    public boolean insertBdMecPersonnel(BdMecPersonnelVO bdMecPersonnelVO) {
        BdMecPersonnel bdMecPersonnel = new BdMecPersonnel();
        bdMecPersonnelVO.setIsDelete(Constants.NOT_DELETED);
        BeanUtils.copyBeanProp(bdMecPersonnel, bdMecPersonnelVO);
        //设置机构id与校区id
        bdMecPersonnel.setCreateTime(DateUtils.getNowDate());
        bdMecPersonnel.setCreateBy(SecurityUtils.getUserId().toString());
        boolean result = this.insert(bdMecPersonnel);
        bdMecPersonnelVO.setId(bdMecPersonnel.getId());
        return result;
    }

    /**
     * 修改机构人员
     *
     * @param bdMecPersonnelVO 机构人员
     * @return 结果
     */
    @Override
    public boolean updateBdMecPersonnel(BdMecPersonnelVO bdMecPersonnelVO) {
        BdMecPersonnel bdMecPersonnel = new BdMecPersonnel();
        BeanUtils.copyBeanProp(bdMecPersonnel, bdMecPersonnelVO);
        bdMecPersonnel.setUpdateTime(DateUtils.getNowDate());
        bdMecPersonnel.setUpdateBy(SecurityUtils.getUserId().toString());
        EntityWrapper<BdMecPersonnel> wrapper = new EntityWrapper<>();
        wrapper.eq("id", bdMecPersonnel.getId());
        return this.update(bdMecPersonnel, wrapper);
    }

    /**
     * 批量删除机构人员
     *
     * @param idList 需要删除的机构人员 ID
     * @return 结果
     */
    @Override
    public boolean deleteBdMecPersonnelByIds(Collection<? extends Serializable> idList) {
        List<Long> list = (List<Long>) idList;
        boolean flag = true;
        for (Long id : list) {
            flag = deleteBdMecPersonnelById(id);
            if (!flag) {
                break;
            }
        }
        return flag;
    }

    /**
     * 删除机构人员 信息
     *
     * @param id 机构人员 ID
     * @return 结果
     */
    @Override
    public boolean deleteBdMecPersonnelById(Long id) {
        BdMecPersonnel bdMecPersonnel = new BdMecPersonnel();
        bdMecPersonnel.setIsDelete(Constants.DELETED);
        bdMecPersonnel.setId(id);
        bdMecPersonnel.setUpdateTime(DateUtils.getNowDate());
        bdMecPersonnel.setUpdateBy(SecurityUtils.getUserId().toString());
        return this.updateById(bdMecPersonnel);
    }

    /**
     * 根据机构id查询机构负责人信息
     *
     * @param mecId 机构id
     * @return 机构人员信息
     */
    @Override
    public BdMecPersonnel selectByMecId(Long mecId) {
        BdMecPersonnel bdMecPersonnel = new BdMecPersonnel();
        bdMecPersonnel.setIsDelete(Constants.NOT_DELETED);
        bdMecPersonnel.setMecId(mecId);
        bdMecPersonnel.setType(MecConstant.MEC_MANAGER);

        EntityWrapper<BdMecPersonnel> wrapper = new EntityWrapper<>();
        return this.selectOne(wrapper);
    }

    /**
     * 获得校区人员集合
     *
     * @param mecPersonnel 机构人员查询信息
     * @param mecPersonIds 已使用过的机构人员ids
     * @return 校区人员集合
     */
    @Override
    public Page<BdMecPersonnelBO> getMecPerByMecId(BdMecPersonnelVO mecPersonnel, List<Long> mecPersonIds) {
        EntityWrapper<BdMecPersonnel> wrapper = new EntityWrapper<>();
        wrapper.like("name", mecPersonnel.getName());
        wrapper.notIn("id", mecPersonIds);
        wrapper.eq("type", MecPersonEnum.ORDINARY_PEOPLE.getCode());
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        wrapper.eq("mec_id", SecurityUtils.getLoginMecId());
        Page<BdMecPersonnel> page = this.selectPage(new Page<>(mecPersonnel.getPageNum(), mecPersonnel.getPageSize()), wrapper);
        List<BdMecPersonnelBO> resultList = copyList(page.getRecords());

        // 重新组装page
        return new PageUtils<BdMecPersonnelBO>().getPage(resultList, this.selectCount(wrapper));
    }

    /**
     * 将数据库对象转换为BO对象
     *
     * @param bdMecPersonnels 数据库对象集合
     * @return List<BdMecPersonnelBO> bo对象集合
     */
    public List<BdMecPersonnelBO> copyList(List<BdMecPersonnel> bdMecPersonnels) {
        List<BdMecPersonnelBO> resultList = new ArrayList<>();
        for (BdMecPersonnel source : bdMecPersonnels) {
            BdMecPersonnelBO BdMecPersonnelBO = new BdMecPersonnelBO();
            BeanUtils.copyBeanProp(BdMecPersonnelBO, source);
            resultList.add(BdMecPersonnelBO);
        }
        return resultList;
    }
}
