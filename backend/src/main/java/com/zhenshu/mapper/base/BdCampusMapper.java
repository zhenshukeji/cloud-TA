package com.zhenshu.mapper.base;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusVO;
import com.zhenshu.mapper.domain.base.BdCampus;

import java.util.List;

/**
 * 校区Mapper接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface BdCampusMapper extends BaseMapper<BdCampus> {

    /**
     * 获得机构的校区信息
     * @param campusVO 机构id,校区名称，校区负责人，联系方式
     * @return 校区信息
     */
    List<BdCampusBO> getCampusInfoByMecId(BdCampusVO campusVO);

    /**
     * 根据机构id查询校区列表
     *
     * @param bdCampusVO 校区参数
     * @return 返回信息
     */
    List<BdCampusBO> selectBdCampusList(BdCampusVO bdCampusVO);

    /**
     * 获得校区详细信息
     * @param id 校区ID
     * @return 校区
     */
    BdCampusBO selectBdCampusDetailById(Long id);
}
