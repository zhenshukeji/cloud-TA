package com.zhenshu.cloud.platform.statistics.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.mapper.domain.platform.BdPlatformAccount;
import com.zhenshu.mapper.platform.BdPlatformAccountMapper;
import com.zhenshu.cloud.platform.statistics.domain.bo.BdPlatformAccountBO;
import com.zhenshu.cloud.platform.statistics.service.IBdPlatformAccountService;
import org.springframework.stereotype.Service;

/**
 * @author yuxi
 * @desc 平台账户 Service业务层处理
 * @date 2020-11-24
 */
@Service
public class BdPlatformAccountServiceImpl extends ServiceImpl<BdPlatformAccountMapper, BdPlatformAccount> implements IBdPlatformAccountService {

    /**
     * 查询平台账户
     * @return 平台账户
     */
    @Override
    public BdPlatformAccountBO selectBdPlatformAccountById(Long platformId) {
        BdPlatformAccount bdPlatformAccount = this.selectById(platformId);
        if (bdPlatformAccount == null) {
            return null;
        }
        BdPlatformAccountBO bdPlatformAccountBO = new BdPlatformAccountBO();
        BeanUtils.copyBeanProp(bdPlatformAccountBO, bdPlatformAccount);
        return bdPlatformAccountBO;
    }

}
