package com.zhenshu.zhenxueyou.platform.miniapp.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.common.utils.page.PageUtils;
import com.zhenshu.zhenxueyou.mec.project.system.domain.SysUser;
import com.zhenshu.zhenxueyou.platform.miniapp.domain.bo.MinLoginBO;
import com.zhenshu.zhenxueyou.platform.miniapp.domain.vo.MinLoginVO;
import com.zhenshu.zhenxueyou.platform.miniapp.service.IMinLoginService;
import com.zhenshu.zhenxueyou.platform.statistics.domain.bo.DayDataBO;
import com.zhenshu.zhenxueyou.platform.statistics.domain.bo.YearDataBO;
import com.zhenshu.mapper.domain.miniapp.MinLogin;
import com.zhenshu.mapper.miniapp.MinLoginMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jing
 * @desc 小程序登录用户 Service业务层处理
 * @date 2020-11-11
 */
@Service
public class MinLoginServiceImpl extends ServiceImpl<MinLoginMapper, MinLogin> implements IMinLoginService {

    @Resource
    private MinLoginMapper minLoginMapper;

    /**
     * 查询小程序登录用户
     *
     * @param id 小程序登录用户 ID
     * @return 小程序登录用户
     */
    @Override
    public MinLoginBO selectMinLoginById(Long id) {
        MinLogin minLogin = this.selectById(id);
        if (minLogin == null) {
            return null;
        }
        MinLoginBO minLoginBO = new MinLoginBO();
        BeanUtils.copyBeanProp(minLoginBO, minLogin);
        return minLoginBO;
    }

    /**
     * 查询小程序登录用户 列表
     *
     * @param minLoginVO 小程序登录用户
     * @return 小程序登录用户
     */
    @Override
    public Page<MinLoginBO> listByMybatisPlus(MinLoginVO minLoginVO) {
        MinLogin minLogin = new MinLogin();
        BeanUtils.copyBeanProp(minLogin, minLoginVO);
        EntityWrapper<MinLogin> wrapper = new EntityWrapper<>();
        wrapper.setEntity(minLogin);
        Page<MinLogin> page = this.selectPage(new Page<>(minLoginVO.getPageNum(), minLoginVO.getPageSize()), wrapper);
        List<MinLoginBO> resultList = new ArrayList<>();
        for (MinLogin source : page.getRecords()) {
            MinLoginBO minLoginBO = new MinLoginBO();
            BeanUtils.copyBeanProp(minLoginBO, source);
            resultList.add(minLoginBO);
        }
        // 重新组装page
        return new PageUtils<MinLoginBO>().getPage(resultList, this.selectCount(wrapper));
    }

    /**
     * 使用pagehelper的分页
     *
     * @param minLoginVO 模糊查询
     * @return 小程序登录用户 集合
     */
    @Override
    public List<MinLoginBO> listByPageHelper(MinLoginVO minLoginVO) {
        return minLoginMapper.pageQueryMinLoginByPageHelper(minLoginVO);
    }

    /**
     * 新增小程序登录用户
     *
     * @param minLoginVO 小程序登录用户
     * @return 结果
     */
    @Override
    public boolean insertMinLogin(MinLoginVO minLoginVO) {
        MinLogin minLogin = new MinLogin();
        BeanUtils.copyBeanProp(minLogin, minLoginVO);
        SysUser sysUser = SecurityUtils.getSysUser();
        minLogin.setCreateBy(sysUser.getUserId().toString());
        minLogin.setCreateTime(DateUtils.getNowDate());
        minLogin.setIsDelete(Constants.NOT_DELETED);
        boolean result = this.insert(minLogin);
        minLoginVO.setId(minLogin.getId());
        return result;
    }

    /**
     * 修改小程序登录用户
     *
     * @param minLoginVO 小程序登录用户
     * @return 结果
     */
    @Override
    public boolean updateMinLogin(MinLoginVO minLoginVO) {
        MinLogin minLogin = new MinLogin();
        BeanUtils.copyBeanProp(minLogin, minLoginVO);
        minLogin.setUpdateBy(SecurityUtils.getUserId().toString());
        minLogin.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(minLogin);
    }

    /**
     * 删除小程序登录用户 信息
     *
     * @param id 小程序登录用户 ID
     * @return 结果
     */
    @Override
    public boolean deleteMinLoginById(Long id) {
        return this.deleteById(id);
    }

    /**
     * 获得家长数
     * @return 家长数
     */
    @Override
    public Integer getMinTotal() {
        EntityWrapper<MinLogin> wrapper = new EntityWrapper<>();
        return this.selectCount(wrapper);
    }

    /**
     * 家长数
     * @param day 时间
     * @return 家长数
     */
    @Override
    public List<DayDataBO> minNum(Integer day) {
        return minLoginMapper.minNum(day);
    }

    /**
     * 得到学生账户
     * @param phone 手机号
     * @return 账户信息
     */
    @Override
    public MinLoginBO selectMinLoginByPhone(String phone) {
        EntityWrapper<MinLogin> wrapper = new EntityWrapper<>();
        wrapper.eq("phone", phone);
        MinLogin minLogin = this.selectOne(wrapper);
        if (minLogin == null){
            return null;
        }
        MinLoginBO bo = new MinLoginBO();
        BeanUtils.copyBeanProp(bo, minLogin);
        return bo;
    }

    /**
     * 按年份统计家长入驻数
     *
     * @return 按年份统计家长入驻数
     * @author tyh
     */
    @Override
    public List<YearDataBO> stuNumByYears() {
        return minLoginMapper.stuNumByYears();
    }

}
