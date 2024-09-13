package com.zhenshu.cloud.platform.statistics.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.mapper.domain.platform.BdPlatformAccount;
import com.zhenshu.cloud.platform.statistics.domain.bo.BdPlatformAccountBO;

/**
 * @author yuxi
 * @date 2020-11-24
 * @desc 平台账户 Service接口
 */
public interface IBdPlatformAccountService extends IService<BdPlatformAccount> {

    /**
     * 根据id查询平台账户
     *
     * @return 平台账户 信息
     */
    BdPlatformAccountBO selectBdPlatformAccountById(Long platformId);

}
