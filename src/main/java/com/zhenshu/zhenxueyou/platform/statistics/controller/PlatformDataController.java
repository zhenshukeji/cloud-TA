package com.zhenshu.zhenxueyou.platform.statistics.controller;

import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.zhenxueyou.platform.statistics.domain.bo.DayDataBO;
import com.zhenshu.zhenxueyou.platform.statistics.domain.bo.YearDataBO;
import com.zhenshu.zhenxueyou.platform.statistics.manager.PlatformManager;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/12/1 18:58
 * @desc
 */
@RestController
@RequestMapping("/platform/data")
@PreAuthorize("@ss.hasPermi('platform:data:list')")
public class PlatformDataController {

    @Resource
    private PlatformManager platformManager;

    @GetMapping("/list")
    @ApiOperation(value = "列表")
    public Result<Map<String, Integer>> list() {
        return new Result<Map<String, Integer>>().success(platformManager.list());
    }

    @GetMapping("/mecNum")
    @ApiOperation(value = "机构入驻详情")
    public Result<List<DayDataBO>> mecNum(Integer day) {
        return new Result<List<DayDataBO>>().success(platformManager.mecNum(day));
    }

    @GetMapping("/minNum")
    @ApiOperation(value = "家长入驻详情")
    public Result<List<DayDataBO>> minNum(Integer day) {
        return new Result<List<DayDataBO>>().success(platformManager.minNum(day));
    }

    /**
     * 按年份统计机构入驻数
     *
     * @author tyh
     * @return 按年份统计机构入驻数
     */
    @GetMapping("/mecNumByYears")
    public Result<List<YearDataBO>> mecNumByYears(){
        return new Result<List<YearDataBO>>().success(platformManager.mecNumByYears());
    }

    /**
     * 按年份统计家长入驻数
     *
     * @author tyh
     * @return 按年份统计家长入驻数
     */
    @GetMapping("/stuNumByYears")
    public Result<List<YearDataBO>> stuNumByYears(){
        return new Result<List<YearDataBO>>().success(platformManager.stuNumByYears());
    }
}
