package com.zhenshu.mapper.base;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusConnectionBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusConnectionVO;
import com.zhenshu.mapper.domain.base.BdCampusConnection;

import java.util.List;

/**
 * 机构人员和校区的关联 Mapper接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface BdCampusConnectionMapper extends BaseMapper<BdCampusConnection> {


    List<BdCampusConnectionBO> selectListByCampus(BdCampusConnectionVO campusConnectionVO);


    List<BdCampusConnectionBO> selectByMecPersonnelId(Long mecPersonnelId);
}
