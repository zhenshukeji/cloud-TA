package com.zhenshu.cloud.platform.miniapp.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.cloud.platform.miniapp.domain.bo.MinLoginBO;
import com.zhenshu.cloud.platform.miniapp.domain.vo.MinLoginVO;
import com.zhenshu.cloud.platform.statistics.domain.bo.DayDataBO;
import com.zhenshu.cloud.platform.statistics.domain.bo.YearDataBO;
import com.zhenshu.mapper.domain.miniapp.MinLogin;

import java.util.List;

/**
 *
 * @author jing
 * @date 2020-11-11
 * @desc 小程序登录用户 Service接口
 */
public interface IMinLoginService extends IService<MinLogin> {

    /**
     * 根据id查询小程序登录用户
     * @param id 小程序登录用户 id
     * @return 小程序登录用户 信息
     */
    MinLoginBO selectMinLoginById(Long id);

    /**
     * 分页查询小程序登录用户 列表
     * @param minLoginVO 小程序登录用户
     * @return 分页小程序登录用户
     */
    Page<MinLoginBO> listByMybatisPlus(MinLoginVO minLoginVO);

    /**
     * 使用pagehelper的分页
     * @param minLoginVO 模糊查询
     * @retur 小程序登录用户 集合
     */
    List<MinLoginBO> listByPageHelper(MinLoginVO minLoginVO);

    /**
     * 新增小程序登录用户
     * @param minLoginVO 小程序登录用户
     * @return 结果
     */
    boolean insertMinLogin(MinLoginVO minLoginVO);

    /**
     * 修改小程序登录用户
     * @param minLoginVO 小程序登录用户
     * @return 结果
     */
    boolean updateMinLogin(MinLoginVO minLoginVO);

    /**
     * 删除小程序登录用户 信息
     * @param id 小程序登录用户 ID
     * @return 结果
     */
    boolean deleteMinLoginById(Long id);

    /**
     * 获得家长数
     * @return 家长数
     */
    Integer getMinTotal();

    /**
     * 家长数
     * @param day 时间
     * @return 家长数
     */
    List<DayDataBO> minNum(Integer day);

    /**
     * 得到学生账户
     * @param phone 手机号
     * @return 账户信息
     */
    MinLoginBO selectMinLoginByPhone(String phone);

    /**
     * 按年份统计家长入驻数
     *
     * @return 按年份统计家长入驻数
     * @author tyh
     */
    List<YearDataBO> stuNumByYears();
}
