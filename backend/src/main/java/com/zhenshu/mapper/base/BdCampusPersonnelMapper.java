package com.zhenshu.mapper.base;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusPersonnelVO;
import com.zhenshu.mapper.domain.base.BdCampusPersonnel;

import java.util.List;

/**
 * 校区人员Mapper接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface BdCampusPersonnelMapper extends BaseMapper<BdCampusPersonnel> {

    /**
     * 查询校区人员列表
     *
     * @param bdCampusPersonnelVO 校区人员
     * @return 校区人员
     */
    List<BdCampusPersonnelBO> getDetailsList(BdCampusPersonnelVO bdCampusPersonnelVO);
}
