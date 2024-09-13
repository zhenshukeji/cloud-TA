package com.zhenshu.mapper.miniapp;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.cloud.platform.miniapp.domain.bo.MinLoginBO;
import com.zhenshu.cloud.platform.miniapp.domain.vo.MinLoginVO;
import com.zhenshu.cloud.platform.statistics.domain.bo.DayDataBO;
import com.zhenshu.cloud.platform.statistics.domain.bo.YearDataBO;
import com.zhenshu.mapper.domain.miniapp.MinLogin;

import java.util.List;

/**
 * @desc 小程序登录用户 Mapper接口
 *
 * @author jing
 * @date 2020-11-11
 */
public interface MinLoginMapper extends BaseMapper<MinLogin> {

    /**
     * 分页查询小程序登录用户 列表
     * @param minLoginVO 小程序登录用户
     * @return 分页小程序登录用户
     */
    List<MinLoginBO> pageQueryMinLoginByPageHelper(MinLoginVO minLoginVO);

    /**
     * 家长数
     * @param day 时间
     * @return 家长数
     */
    List<DayDataBO> minNum(Integer day);

    /**
     * 按年份统计家长入驻数
     *
     * @return 按年份统计家长入驻数
     * @author tyh
     */
    List<YearDataBO> stuNumByYears();
}
