package com.zhenshu.framework.schedule;

import com.zhenshu.common.constant.Constants;
import com.zhenshu.cloud.mec.sale.domain.bo.BdClueInfoBO;
import com.zhenshu.cloud.mec.sale.service.IBdClueInfoService;
import com.zhenshu.mapper.domain.sale.BdClueInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/12/8 14:58
 * @desc 线索定时任务
 */
@Component
public class ClueSchedule {

    private final Logger logger = LoggerFactory.getLogger(ClueSchedule.class);

    @Resource
    private IBdClueInfoService bdClueInfoService;

    /**
     * 线索定时任务 每天18点执行一次
     * 有效，未报名，没试听，没跟进，时间超过三周，已分配的线索回归到公海池
     */
    @Scheduled(cron = "0 0 18 * * ? ")
    public void orderTask() {
        logger.info("线索定时任务");
        List<BdClueInfoBO> clueList = bdClueInfoService.getNeedToSeaClue();
        for (BdClueInfoBO clueInfoBO : clueList) {
            // 回归公海池
            BdClueInfo bdClueInfo = new BdClueInfo();
            bdClueInfo.setId(clueInfoBO.getId());
            // 设置为未分配
            bdClueInfo.setIsDistribution(Constants.NO);
            // 跟进人为空
            bdClueInfo.setLoginUserId(null);
            bdClueInfoService.updateById(bdClueInfo);
        }
        logger.info("线索定时任务执行完成");
    }
}
