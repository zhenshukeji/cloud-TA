package com.zhenshu.cloud.platform.statistics.controller;

import com.zhenshu.common.annotation.Response;
import com.zhenshu.common.utils.poi.ExcelUtil;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.AjaxResult;
import com.zhenshu.framework.web.page.TableDataInfo;
import com.zhenshu.cloud.platform.statistics.domain.bo.BdPlatformAccountTurnoverBO;
import com.zhenshu.cloud.platform.statistics.domain.vo.BdPlatformAccountTurnoverVO;
import com.zhenshu.cloud.platform.statistics.domain.vo.PlatformPipelineExportVO;
import com.zhenshu.cloud.platform.statistics.service.IBdPlatformAccountTurnoverService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 平台账户流水 Controller
 *
 * @author yuxi
 * @date 2020-11-23
 */
@Api(value = "平台账户流水", tags = "平台账户流水接口")
@RestController
@RequestMapping("/platform/turnover")
@PreAuthorize("@ss.hasPermi('platform:turnover:list')")
public class BdPlatformAccountTurnoverController extends BaseController {

    @Resource
    private IBdPlatformAccountTurnoverService bdPlatformAccountTurnoverService;


    @ApiOperation(value = "查询平台账户流水列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "BdPlatformAccountTurnoverVO", value = "平台账户流水封装类", dataType = "BdPlatformAccountTurnoverVO")
    @GetMapping("/list")
    @Response
    public TableDataInfo listByPageHelper(BdPlatformAccountTurnoverVO bdPlatformAccountTurnoverVO) {
        startPage();
        List<BdPlatformAccountTurnoverBO> list = bdPlatformAccountTurnoverService.listByPageHelper(bdPlatformAccountTurnoverVO);
        if (list == null || list.isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(list);
    }

    /**
     * 平台流水按月份导出数据
     *
     * @param pipelineExportVO 流水导出封装
     * @return 结果
     * @author tyh
     */
    @ApiOperation(value = "按月导出平台账户流水列表", httpMethod = "POST")
    @PostMapping("/exportMonths")
    public AjaxResult turnoverByMonths(@RequestBody PlatformPipelineExportVO pipelineExportVO) {
        List<BdPlatformAccountTurnoverBO> list =
                bdPlatformAccountTurnoverService.exportMonths(pipelineExportVO);
        if (list == null || list.isEmpty()) {
            return AjaxResult.error("此月份没有对应的流水数据，无法导出数据");
        }
        ExcelUtil<BdPlatformAccountTurnoverBO> util = new ExcelUtil<>(BdPlatformAccountTurnoverBO.class);
        String sheetName = pipelineExportVO.getYears() + "Years" + pipelineExportVO.getMonths() + "MonthRecord";
        return util.exportExcel(list, sheetName);
    }

    /**
     * 平台流水按季度导出数据
     *
     * @param pipelineExportVO 流水导出封装
     * @author tyh
     * @return 结果
     */
    @ApiOperation(value = "按季度导出平台账户流水列表", httpMethod = "POST")
    @PostMapping("/exportQuarterly")
    public AjaxResult exportQuarterly(@RequestBody PlatformPipelineExportVO pipelineExportVO) {
        List<BdPlatformAccountTurnoverBO> list = bdPlatformAccountTurnoverService.exportQuarterly(pipelineExportVO);
        if (list == null || list.isEmpty()) {
            return AjaxResult.error("季度没有对应的流水数据，无法导出数据");
        }
        ExcelUtil<BdPlatformAccountTurnoverBO> util = new ExcelUtil<>(BdPlatformAccountTurnoverBO.class);
        String sheetName = pipelineExportVO.getYears() + "Year" + pipelineExportVO.getQuarterly() + "QuarterlyRecord";
        return util.exportExcel(list, sheetName);
    }
}
