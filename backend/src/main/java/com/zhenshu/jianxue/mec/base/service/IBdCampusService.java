package com.zhenshu.jianxue.mec.base.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusVO;
import com.zhenshu.mapper.domain.base.BdCampus;

import java.util.List;

/**
 * 校区Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdCampusService extends IService<BdCampus> {

    /**
     * 查询校区
     *
     * @param id 校区ID
     * @return 校区
     */
    BdCampusBO selectBdCampusById(Long id);

    /**
     * 获得校区详细信息
     *
     * @param id 校区ID
     * @return 校区
     */
    BdCampusBO selectBdCampusDetailById(Long id);

    /**
     * 新增校区
     *
     * @param bdCampusVO 校区
     * @return 结果
     */
    boolean insertBdCampus(BdCampusVO bdCampusVO);

    /**
     * 修改校区
     *
     * @param bdCampusVO 校区
     * @return 结果
     */
    boolean updateBdCampus(BdCampusVO bdCampusVO);

    /**
     * 删除校区信息
     *
     * @param id 校区ID
     * @return 结果
     */
    boolean deleteBdCampusById(Long id);

    /**
     * 根据机构id获取校区列表
     */
    List<BdCampusBO> selectBdCampusList(BdCampusVO bdCampusVO, Long mecId);

    /**
     * 获得机构的校区信息
     *
     * @param campusVO 机构id,校区名称，校区负责人，联系方式
     * @return 校区信息
     */
    List<BdCampusBO> getCampusInfoByMecId(BdCampusVO campusVO);

    /**
     * 一个机构不能拥有相同账号的校区登录账号
     * @param mgrPhone 登录账户
     * @return 是否拥有
     */
    boolean checkRepeatMgrPhone(String mgrPhone);
}
