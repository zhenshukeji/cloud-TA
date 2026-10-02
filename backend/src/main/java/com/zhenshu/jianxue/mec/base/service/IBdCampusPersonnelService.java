package com.zhenshu.jianxue.mec.base.service;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusPersonnelVO;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusPersonnelBO;
import com.zhenshu.mapper.domain.base.BdCampusPersonnel;

/**
 * 校区人员Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdCampusPersonnelService extends IService<BdCampusPersonnel> {

    /**
     * 查询校区人员
     *
     * @param id 校区人员ID
     * @return 校区人员
     */
    BdCampusPersonnelBO selectBdCampusPersonnelById(Long id);

    /**
     * 分页查询校区人员列表
     *
     * @param bdCampusPersonnelVO 校区人员
     * @return 分页校区人员
     */
    List<BdCampusPersonnelBO> selectBdCampusPersonnelList(BdCampusPersonnelVO bdCampusPersonnelVO);


    /**
     * 新增校区人员
     *
     * @param bdCampusPersonnelVO 校区人员
     * @return 结果
     */
    boolean insertBdCampusPersonnel(BdCampusPersonnelVO bdCampusPersonnelVO);

    /**
     * 修改校区人员
     *
     * @param bdCampusPersonnelVO 校区人员
     * @return 结果
     */
    boolean updateBdCampusPersonnel(BdCampusPersonnelVO bdCampusPersonnelVO);

    /**
     * 删除校区人员信息
     *
     * @param id 校区人员ID
     * @return 结果
     */
    boolean deleteBdCampusPersonnelById(Long id);


    /**
     * 根据校区id查到校区负责人
     */
    BdCampusPersonnel selectByCampusId(Long campusId);

    /**
     * 得到校区的校区人员
     *
     * @param campusPersonnelVO 校区人员参数
     * @param campusPersonIds   已使用校区人员ids
     * @return Page<BdCampusBO>
     */
    Page<BdCampusPersonnelBO> getCamPerByCampusId(BdCampusPersonnelVO campusPersonnelVO, List<Long> campusPersonIds);
}
