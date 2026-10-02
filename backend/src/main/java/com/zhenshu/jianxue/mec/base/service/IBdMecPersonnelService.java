package com.zhenshu.jianxue.mec.base.service;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.base.domain.vo.BdMecPersonnelVO;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMecPersonnelBO;
import com.zhenshu.mapper.domain.base.BdMecPersonnel;

import java.io.Serializable;
import java.util.Collection;

/**
 * 机构人员 Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdMecPersonnelService extends IService<BdMecPersonnel>
{

    /**
     * 查询机构人员
     * @param id 机构人员 ID
     * @return 机构人员
     */
     BdMecPersonnelBO selectBdMecPersonnelById(Long id);

    /**
     * 分页查询机构人员 列表
     * @param bdMecPersonnelVO 机构人员
     * @return 分页机构人员
     */
     Page<BdMecPersonnelBO> selectBdMecPersonnelList(BdMecPersonnelVO bdMecPersonnelVO);

     /**
     * 无分页无参数机构人员 列表
     * @return 机构人员 集合
     */
     List<BdMecPersonnelBO> selectBdMecPersonnelList();

    /**
     * 新增机构人员
     * @param bdMecPersonnelVO 机构人员
     * @return 结果
     */
     boolean insertBdMecPersonnel(BdMecPersonnelVO bdMecPersonnelVO);

    /**
     * 修改机构人员
     * @param bdMecPersonnelVO 机构人员
     * @return 结果
     */
     boolean updateBdMecPersonnel(BdMecPersonnelVO bdMecPersonnelVO);

    /**
     * 批量删除机构人员
     * @param idList 需要删除的机构人员 ID
     * @return 结果
     */
     boolean deleteBdMecPersonnelByIds(Collection<? extends Serializable> idList);

    /**
     * 删除机构人员 信息
     * @param id 机构人员 ID
     * @return 结果
     */
     boolean deleteBdMecPersonnelById(Long id);

    /**
     * 查询机构负责人
     */
    BdMecPersonnel selectByMecId(Long mecId);

    /**
     * 获得校区人员集合
     * @param mecPersonnel 机构人员查询信息
     * @param mecPersonIds 已使用过的机构人员ids
     * @return 校区人员集合
     */
    Page<BdMecPersonnelBO> getMecPerByMecId(BdMecPersonnelVO mecPersonnel, List<Long> mecPersonIds);
}
