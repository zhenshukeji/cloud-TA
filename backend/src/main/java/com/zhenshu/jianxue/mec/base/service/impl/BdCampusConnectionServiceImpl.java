package com.zhenshu.jianxue.mec.base.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.common.utils.page.PageUtils;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusConnectionVO;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusConnectionBO;
import com.zhenshu.jianxue.mec.base.service.IBdCampusConnectionService;
import com.zhenshu.mapper.base.BdCampusConnectionMapper;
import com.zhenshu.mapper.domain.base.BdCampusConnection;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.Collection;

/**
 * 机构人员和校区的关联 Service业务层处理
 *
 * @author jing
 * @date 2020-10-04
 */
@Service
public class BdCampusConnectionServiceImpl extends ServiceImpl<BdCampusConnectionMapper, BdCampusConnection> implements IBdCampusConnectionService {

    @Resource
    private BdCampusConnectionMapper campusConnectionMapper;

    /**
     * 查询机构人员和校区的关联
     *
     * @param id 机构人员和校区的关联 ID
     * @return 机构人员和校区的关联
     */
    @Override
    public BdCampusConnectionBO selectBdCampusConnectionById(Long id) {
        BdCampusConnection bdCampusConnection = new BdCampusConnection();
        //未删除的 机构id，校区id
        bdCampusConnection.setIsDelete(Constants.NOT_DELETED);
        bdCampusConnection.setId(id);

        EntityWrapper<BdCampusConnection> wrapper = new EntityWrapper<>();
        wrapper.setEntity(bdCampusConnection);
        bdCampusConnection = this.selectOne(wrapper);
        BdCampusConnectionBO bdCampusConnectionBO = new BdCampusConnectionBO();
        BeanUtils.copyBeanProp(bdCampusConnectionBO, bdCampusConnection);
        return bdCampusConnectionBO;
    }

    /**
     * 查到机构人员id和机构之间的联系
     */
    @Override
    public List<BdCampusConnectionBO> selectByMecPersonnelId(Long mecPersonnelId) {
        // 不在使用mybatis-plus的，因为bd_campus_connection中的校区名称没有进行维护
        return campusConnectionMapper.selectByMecPersonnelId(mecPersonnelId);
    }


    /**
     * 查询机构人员和校区的关联 列表
     *
     * @param bdCampusConnectionVO 机构人员和校区的关联
     * @return 机构人员和校区的关联
     */
    @Override
    public Page<BdCampusConnectionBO> selectBdCampusConnectionList(BdCampusConnectionVO bdCampusConnectionVO) {
        BdCampusConnection bdCampusConnection = new BdCampusConnection();
        BeanUtils.copyBeanProp(bdCampusConnection, bdCampusConnectionVO);
        EntityWrapper<BdCampusConnection> wrapper = new EntityWrapper<>();
        //设置机构与校区id
        bdCampusConnection.setIsDelete(Constants.NOT_DELETED);

        return getPage(bdCampusConnection, bdCampusConnectionVO, wrapper);
    }

    /**
     * 无分页无参数列表
     */
    @Override
    public List<BdCampusConnectionBO> selectBdCampusConnectionList() {
        EntityWrapper<BdCampusConnection> wrapper = new EntityWrapper<>();
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        return copyList(this.selectList(wrapper));
    }

    /**
     * 新增机构人员和校区的关联
     *
     * @param bdCampusConnectionVO 机构人员和校区的关联
     * @return 结果
     */
    @Override
    public boolean insertBdCampusConnection(BdCampusConnectionVO bdCampusConnectionVO) {
        BdCampusConnection bdCampusConnection = new BdCampusConnection();
        BeanUtils.copyBeanProp(bdCampusConnection, bdCampusConnectionVO);
        bdCampusConnection.setIsDelete(Constants.NOT_DELETED);
        //设置机构id与校区id
        bdCampusConnection.setCreateTime(DateUtils.getNowDate());
        bdCampusConnection.setCreateBy(SecurityUtils.getUserId().toString());
        return this.insert(bdCampusConnection);
    }

    /**
     * 修改机构人员和校区的关联
     *
     * @param bdCampusConnectionVO 机构人员和校区的关联
     * @return 结果
     */
    @Override
    public boolean updateBdCampusConnection(BdCampusConnectionVO bdCampusConnectionVO) {
        BdCampusConnection bdCampusConnection = new BdCampusConnection();
        BeanUtils.copyBeanProp(bdCampusConnection, bdCampusConnectionVO);
        bdCampusConnection.setUpdateTime(DateUtils.getNowDate());
        bdCampusConnection.setUpdateBy(SecurityUtils.getUserId().toString());

        EntityWrapper<BdCampusConnection> wrapper = new EntityWrapper<>();
        wrapper.eq("id", bdCampusConnection.getId());
        return this.update(bdCampusConnection, wrapper);
    }

    /**
     * 批量删除机构人员和校区的关联
     *
     * @param idList 需要删除的机构人员和校区的关联 ID
     * @return 结果
     */
    @Override
    public boolean deleteBdCampusConnectionByIds(Collection<? extends Serializable> idList) {
        List<Long> list = (List<Long>) idList;
        boolean flag = true;
        for (Long id : list) {
            flag = deleteBdCampusConnectionById(id);
            if (!flag) {
                break;
            }
        }
        return flag;
    }

    /**
     * 删除机构人员和校区的关联 信息
     *
     * @param id 机构人员和校区的关联 ID
     * @return 结果
     */
    @Override
    public boolean deleteBdCampusConnectionById(Long id) {
        BdCampusConnection bdCampusConnection = new BdCampusConnection();
        bdCampusConnection.setIsDelete(Constants.DELETED);
        bdCampusConnection.setId(id);
        bdCampusConnection.setUpdateTime(DateUtils.getNowDate());
        bdCampusConnection.setUpdateBy(SecurityUtils.getUserId().toString());
        return this.updateById(bdCampusConnection);
    }

    /**
     * 根据校区进行查询
     *
     * @param campusConnectionVO 校区Vo
     * @return 机构人员信息
     */
    @Override
    public List<BdCampusConnectionBO> selectListByCampus(BdCampusConnectionVO campusConnectionVO) {
        return this.baseMapper.selectListByCampus(campusConnectionVO);
    }

    /**
     * 根据机构id 和校区id 进行查询，包括已删除的
     *
     * @param mecPersonnelId 机构人员id
     * @return 返回数据
     */
    @Override
    public BdCampusConnectionBO selectByMecPersonnelIdCampusID(Long mecPersonnelId, Long campusId) {
        BdCampusConnection bdCampusConnection = new BdCampusConnection();
        bdCampusConnection.setMecPersonnelId(mecPersonnelId);
        bdCampusConnection.setCampusId(campusId);

        EntityWrapper<BdCampusConnection> wrapper = new EntityWrapper<>();
        wrapper.setEntity(bdCampusConnection);
        BdCampusConnection campusConnection = this.selectOne(wrapper);
        if (campusConnection == null) {
            return null;
        }

        BdCampusConnectionBO bdCampusConnectionBO = new BdCampusConnectionBO();
        BeanUtils.copyBeanProp(bdCampusConnectionBO, campusConnection);
        return bdCampusConnectionBO;
    }

    /**
     * 进行分页
     *
     * @param bdCampusConnection   数据库对象 设置查询参数
     * @param bdCampusConnectionVO 入参对象
     * @param wrapper              查询参数
     * @return Page<BdCampusConnectionBO>分页好的出参
     */
    public Page<BdCampusConnectionBO> getPage(BdCampusConnection bdCampusConnection, BdCampusConnectionVO bdCampusConnectionVO, EntityWrapper<BdCampusConnection> wrapper) {
        wrapper.setEntity(bdCampusConnection);
        Page<BdCampusConnection> page = this.selectPage(new Page<>(bdCampusConnectionVO.getPageNum(), bdCampusConnectionVO.getPageSize()), wrapper);
        List<BdCampusConnectionBO> resultList = copyList(page.getRecords());

        // 重新组装page
        return new PageUtils<BdCampusConnectionBO>().getPage(resultList, this.selectCount(wrapper));
    }

    /**
     * 将数据库对象转换为BO对象
     *
     * @param bdCampusConnections 数据库对象集合
     * @return List<BdCampusConnectionBO> bo对象集合
     */
    public List<BdCampusConnectionBO> copyList(List<BdCampusConnection> bdCampusConnections) {
        List<BdCampusConnectionBO> resultList = new ArrayList<>();
        for (BdCampusConnection source : bdCampusConnections) {
            BdCampusConnectionBO BdCampusConnectionBO = new BdCampusConnectionBO();
            BeanUtils.copyBeanProp(BdCampusConnectionBO, source);
            resultList.add(BdCampusConnectionBO);
        }
        return resultList;
    }
}
