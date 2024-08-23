package com.zhenshu.zhenxueyou.platform.statistics.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.zhenxueyou.platform.statistics.domain.bo.BdStuWithdrawRecordBO;
import com.zhenshu.zhenxueyou.platform.statistics.domain.vo.StuWithdrawRecordVO;
import com.zhenshu.zhenxueyou.platform.statistics.service.IBdStuWithdrawRecordService;
import com.zhenshu.mapper.domain.platform.BdStuWithdrawRecord;
import com.zhenshu.mapper.platform.BdStuWithdrawRecordMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 提现记录 服务实现类
 * </p>
 *
 * @author jing
 * @since 2021-08-13
 */
@Service
public class BdStuWithdrawRecordServiceImpl extends ServiceImpl<BdStuWithdrawRecordMapper, BdStuWithdrawRecord> implements IBdStuWithdrawRecordService {

    /**
     * 获取提现记录
     *
     * @param stuWithdrawRecordVO 查询入参
     * @return 提现记录
     */
    @Override
    public List<BdStuWithdrawRecordBO> getWithdrawRecord(StuWithdrawRecordVO stuWithdrawRecordVO) {
        return this.baseMapper.getWithdrawRecord(stuWithdrawRecordVO);
    }
}
