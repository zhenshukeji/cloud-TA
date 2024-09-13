package com.zhenshu.common.manager.platform.min;

import com.zhenshu.cloud.platform.miniapp.service.IMinLoginService;
import com.zhenshu.cloud.platform.statistics.domain.bo.DayDataBO;
import com.zhenshu.cloud.platform.statistics.domain.bo.YearDataBO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/12/2 10:09
 * @desc 平台端调用家长端manager
 */
@Service
public class PlatWithMinManager {

    @Resource
    private IMinLoginService minLoginService;

    /**
     * 获得家长数
     * @return 家长数
     */
    public Integer getMinTotal() {
        return minLoginService.getMinTotal();
    }

    /**
     * 家长数
     * @param day 时间
     * @return 家长数
     */
    public List<DayDataBO> minNum(Integer day) {
        return minLoginService.minNum(day);
    }

    /**
     * 按年份统计家长入驻数
     *
     * @return 按年份统计家长入驻数
     * @author tyh
     */
    public List<YearDataBO> stuNumByYears() {
        return minLoginService.stuNumByYears();
    }
}
