package com.zhenshu.zhenxueyou.platform.statistics.service;


import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.zhenxueyou.platform.statistics.domain.bo.BdStuWithdrawRecordBO;
import com.zhenshu.zhenxueyou.platform.statistics.domain.vo.StuWithdrawRecordVO;
import com.zhenshu.mapper.domain.platform.BdStuWithdrawRecord;

import java.util.List;

/**
 * <p>
 * 提现记录 服务类
 * </p>
 *
 * @author jing
 * @since 2021-08-13
 */
public interface IBdStuWithdrawRecordService extends IService<BdStuWithdrawRecord> {

    /**
     * 获取提现记录
     * @param stuWithdrawRecordVO 查询入参
     * @return 提现记录
     */
    List<BdStuWithdrawRecordBO> getWithdrawRecord(StuWithdrawRecordVO stuWithdrawRecordVO);
}
