package com.zhenshu.jianxue.mec.base.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMechanismBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdMechanismVO;
import com.zhenshu.mapper.domain.base.BdMechanism;

/**
 * 机构信息Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdMechanismService extends IService<BdMechanism> {

    /**
     * 查询机构信息
     *
     * @param id 机构信息ID
     * @return 机构信息
     */
    BdMechanismBO selectBdMechanismById(Long id);

    /**
     * 分页查询机构信息列表
     *
     * @param bdMechanismVO 机构信息
     * @return 分页机构信息
     */
    Page<BdMechanismBO> selectBdMechanismList(BdMechanismVO bdMechanismVO);

    /**
     * 新增机构信息
     *
     * @param bdMechanismVO 机构信息
     * @return 结果
     */
    boolean insertBdMechanism(BdMechanismVO bdMechanismVO);

    /**
     * 修改机构信息
     *
     * @param bdMechanismVO 机构信息
     * @return 结果
     */
    boolean updateBdMechanism(BdMechanismVO bdMechanismVO);

    /**
     * 删除机构信息信息
     *
     * @param id 机构信息ID
     * @return 结果
     */
    boolean deleteBdMechanismById(Long id);

    /**
     * 根据机构名称进行查询
     */
    BdMechanismBO selectByName(String mecName);

    /**
     * 获得机构详情
     *
     * @param id 机构id
     * @Author tyh
     */
    BdMechanismBO queryMechanismInFoById(Long id);

}
