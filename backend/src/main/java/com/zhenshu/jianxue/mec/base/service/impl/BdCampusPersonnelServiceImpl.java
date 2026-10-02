package com.zhenshu.jianxue.mec.base.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusPersonnelVO;
import com.zhenshu.jianxue.mec.base.service.IBdCampusPersonnelService;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.base.CampusPersonEnum;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.common.utils.page.PageUtils;
import com.zhenshu.mapper.base.BdCampusPersonnelMapper;
import com.zhenshu.mapper.domain.base.BdCampusPersonnel;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 校区人员Service业务层处理
 *
 * @author jing
 * @date 2020-10-04
 */
@Service
public class BdCampusPersonnelServiceImpl extends ServiceImpl<BdCampusPersonnelMapper, BdCampusPersonnel> implements IBdCampusPersonnelService {

    @Resource
    private BdCampusPersonnelMapper campusPersonnelMapper;

    /**
     * 查询校区人员
     *
     * @param id 校区人员ID
     * @return 校区人员
     */
    @Override
    public BdCampusPersonnelBO selectBdCampusPersonnelById(Long id) {
        BdCampusPersonnel bdCampusPersonnel = new BdCampusPersonnel();
        //未删除的 机构id，校区id
        bdCampusPersonnel.setIsDelete(Constants.NOT_DELETED);
        bdCampusPersonnel.setId(id);

        EntityWrapper<BdCampusPersonnel> wrapper = new EntityWrapper<>();
        wrapper.setEntity(bdCampusPersonnel);
        bdCampusPersonnel = this.selectOne(wrapper);
        if (bdCampusPersonnel == null) {
            return null;
        }
        BdCampusPersonnelBO bdCampusPersonnelBO = new BdCampusPersonnelBO();
        BeanUtils.copyBeanProp(bdCampusPersonnelBO, bdCampusPersonnel);
        return bdCampusPersonnelBO;
    }

    /**
     * 查询校区人员列表
     *
     * @param bdCampusPersonnelVO 校区人员
     * @return 校区人员
     */
    @Override
    public List<BdCampusPersonnelBO> selectBdCampusPersonnelList(BdCampusPersonnelVO bdCampusPersonnelVO) {
        SysUser sysUser = SecurityUtils.getSysUser();
        bdCampusPersonnelVO.setCampusId(sysUser.getCampusId());
        bdCampusPersonnelVO.setMecId(sysUser.getMecId());
        return campusPersonnelMapper.getDetailsList(bdCampusPersonnelVO);
    }

    /**
     * 新增校区人员
     *
     * @param bdCampusPersonnelVO 校区人员
     * @return 结果
     */
    @Override
    public boolean insertBdCampusPersonnel(BdCampusPersonnelVO bdCampusPersonnelVO) {
        BdCampusPersonnel bdCampusPersonnel = new BdCampusPersonnel();
        bdCampusPersonnel.setIsDelete(Constants.NOT_DELETED);

        BeanUtils.copyBeanProp(bdCampusPersonnel, bdCampusPersonnelVO);
        //设置机构id与校区id
        bdCampusPersonnel.setCreateTime(DateUtils.getNowDate());
        bdCampusPersonnel.setCreateBy(SecurityUtils.getUserId().toString());
        boolean result = this.insert(bdCampusPersonnel);
        bdCampusPersonnelVO.setId(bdCampusPersonnel.getId());
        return result;
    }

    /**
     * 修改校区人员
     *
     * @param bdCampusPersonnelVO 校区人员
     * @return 结果
     */
    @Override
    public boolean updateBdCampusPersonnel(BdCampusPersonnelVO bdCampusPersonnelVO) {
        BdCampusPersonnel bdCampusPersonnel = new BdCampusPersonnel();
        BeanUtils.copyBeanProp(bdCampusPersonnel, bdCampusPersonnelVO);

        bdCampusPersonnel.setUpdateTime(DateUtils.getNowDate());
        bdCampusPersonnel.setUpdateBy(SecurityUtils.getUserId().toString());
        EntityWrapper<BdCampusPersonnel> wrapper = new EntityWrapper<>();
        wrapper.eq("id", bdCampusPersonnel.getId());
        return this.update(bdCampusPersonnel, wrapper);
    }


    /**
     * 删除校区人员信息
     *
     * @param id 校区人员ID
     * @return 结果
     */
    @Override
    public boolean deleteBdCampusPersonnelById(Long id) {
        BdCampusPersonnel bdCampusPersonnel = new BdCampusPersonnel();
        bdCampusPersonnel.setIsDelete(Constants.DELETED);
        bdCampusPersonnel.setId(id);
        bdCampusPersonnel.setUpdateTime(DateUtils.getNowDate());
        bdCampusPersonnel.setUpdateBy(SecurityUtils.getUserId().toString());
        return this.updateById(bdCampusPersonnel);
    }

    /**
     * 根据机构id进行查询
     *
     * @param campusId 校区id
     */
    @Override
    public BdCampusPersonnel selectByCampusId(Long campusId) {
        BdCampusPersonnel bdCampusPersonnel = new BdCampusPersonnel();
        bdCampusPersonnel.setCampusId(campusId);
        bdCampusPersonnel.setIsDelete(Constants.NOT_DELETED);

        EntityWrapper<BdCampusPersonnel> wrapper = new EntityWrapper<>();
        wrapper.setEntity(bdCampusPersonnel);

        bdCampusPersonnel = this.selectOne(wrapper);
        return bdCampusPersonnel;
    }

    /**
     * 得到校区的校区人员
     *
     * @param campusPersonnelVO 入参
     * @return 集合
     */
    @Override
    public Page<BdCampusPersonnelBO> getCamPerByCampusId(BdCampusPersonnelVO campusPersonnelVO, List<Long> campusPersonIds) {
        EntityWrapper<BdCampusPersonnel> wrapper = new EntityWrapper<>();
        wrapper.like("name", campusPersonnelVO.getName());
        wrapper.notIn("id", campusPersonIds);
        wrapper.eq("type", CampusPersonEnum.ORDINARY_PEOPLE.getCode());
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        wrapper.eq("campus_id", SecurityUtils.getLoginCampusId());
        Page<BdCampusPersonnel> page = this.selectPage(new Page<>(campusPersonnelVO.getPageNum(), campusPersonnelVO.getPageSize()), wrapper);
        List<BdCampusPersonnelBO> resultList = copyList(page.getRecords());
        // 重新组装page
        return new PageUtils<BdCampusPersonnelBO>().getPage(resultList, this.selectCount(wrapper));
    }


    /**
     * 进行分页
     *
     * @param bdCampusPersonnel   数据库对象 设置查询参数
     * @param bdCampusPersonnelVO 入参对象
     * @param wrapper             查询参数
     * @return Page<BdCampusPersonnelBO>分页好的出参
     */
    public Page<BdCampusPersonnelBO> getPage(BdCampusPersonnel bdCampusPersonnel, BdCampusPersonnelVO bdCampusPersonnelVO, EntityWrapper<BdCampusPersonnel> wrapper) {
        wrapper.setEntity(bdCampusPersonnel);
        Page<BdCampusPersonnel> page = this.selectPage(new Page<>(bdCampusPersonnelVO.getPageNum(), bdCampusPersonnelVO.getPageSize()), wrapper);
        List<BdCampusPersonnelBO> resultList = copyList(page.getRecords());

        // 重新组装page
        return new PageUtils<BdCampusPersonnelBO>().getPage(resultList, this.selectCount(wrapper));
    }

    /**
     * 将数据库对象转换为BO对象
     *
     * @param bdCampusPersonnelList 数据库对象集合
     * @return List<BdCampusPersonnelBO> bo对象集合
     */
    public List<BdCampusPersonnelBO> copyList(List<BdCampusPersonnel> bdCampusPersonnelList) {
        List<BdCampusPersonnelBO> resultList = new ArrayList<>();
        for (BdCampusPersonnel source : bdCampusPersonnelList) {
            BdCampusPersonnelBO bdCampusPersonnelBO = new BdCampusPersonnelBO();
            BeanUtils.copyBeanProp(bdCampusPersonnelBO, source);
            resultList.add(bdCampusPersonnelBO);
        }
        return resultList;
    }

}
