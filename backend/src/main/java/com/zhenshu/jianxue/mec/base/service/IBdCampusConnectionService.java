package com.zhenshu.jianxue.mec.base.service;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusConnectionVO;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusConnectionBO;
import com.zhenshu.mapper.domain.base.BdCampusConnection;

import java.io.Serializable;
import java.util.Collection;

/**
 * 机构人员和校区的关联 Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdCampusConnectionService extends IService<BdCampusConnection> {

    /**
     * 查询机构人员和校区的关联
     *
     * @param id 机构人员和校区的关联 ID
     * @return 机构人员和校区的关联
     */
    BdCampusConnectionBO selectBdCampusConnectionById(Long id);

    /**
     * 根据机构id查询校区列表
     */
    List<BdCampusConnectionBO> selectByMecPersonnelId(Long id);


    /**
     * 分页查询机构人员和校区的关联 列表
     *
     * @param bdCampusConnectionVO 机构人员和校区的关联
     * @return 分页机构人员和校区的关联
     */
    Page<BdCampusConnectionBO> selectBdCampusConnectionList(BdCampusConnectionVO bdCampusConnectionVO);

    /**
     * 无分页无参数机构人员和校区的关联 列表
     *
     * @return 机构人员和校区的关联 集合
     */
    List<BdCampusConnectionBO> selectBdCampusConnectionList();

    /**
     * 新增机构人员和校区的关联
     *
     * @param bdCampusConnectionVO 机构人员和校区的关联
     * @return 结果
     */
    boolean insertBdCampusConnection(BdCampusConnectionVO bdCampusConnectionVO);


    /**
     * 修改机构人员和校区的关联
     *
     * @param bdCampusConnectionVO 机构人员和校区的关联
     * @return 结果
     */
    boolean updateBdCampusConnection(BdCampusConnectionVO bdCampusConnectionVO);

    /**
     * 批量删除机构人员和校区的关联
     *
     * @param idList 需要删除的机构人员和校区的关联 ID
     * @return 结果
     */
    boolean deleteBdCampusConnectionByIds(Collection<? extends Serializable> idList);

    /**
     * 删除机构人员和校区的关联 信息
     *
     * @param id 机构人员和校区的关联 ID
     * @return 结果
     */
    boolean deleteBdCampusConnectionById(Long id);

    /**
     * 查询某个校区下的机构人员信息，包括角色信息
     */
    List<BdCampusConnectionBO> selectListByCampus(BdCampusConnectionVO connectionVO);

    /**
     * 查询所有，包括已删除的
     *
     * @param mecPersonnelId 机构人员id
     */
    BdCampusConnectionBO selectByMecPersonnelIdCampusID(Long mecPersonnelId, Long campusId);
}
