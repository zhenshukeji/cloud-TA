package com.zhenshu.mapper.base;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMechanismBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdMechanismVO;
import com.zhenshu.mapper.domain.base.BdMechanism;

/**
 * 机构信息Mapper接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface BdMechanismMapper extends BaseMapper<BdMechanism> {

    /**
     * @param id 机构id
     * @Author tyh
     */
    BdMechanismBO queryMechanismInFoById(Long id);

}
