package com.zhenshu.cloud.platform.statistics.controller;

import com.zhenshu.common.enums.account.OrderEnum;
import com.zhenshu.common.utils.poi.ExcelUtil;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.AjaxResult;
import com.zhenshu.framework.web.page.TableDataInfo;
import com.zhenshu.cloud.mec.account.domain.bo.BdStuAccountTurnoverBO;
import com.zhenshu.cloud.mec.account.domain.vo.BdStuAccountTurnoverVO;
import com.zhenshu.cloud.platform.statistics.domain.bo.BdStuWithdrawRecordBO;
import com.zhenshu.cloud.platform.statistics.domain.vo.StuWithdrawRecordVO;
import com.zhenshu.cloud.platform.statistics.manager.PlatformManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 家长端流水 Controller
 *
 * @author tyh
 * @date 2021-1-7
 */
@Api(value = "家长账户流水", tags = "家长流水接口")
@RestController
@RequestMapping("/platform/stuAccountTurnover")
@PreAuthorize("@ss.hasPermi('platform:stuturnover:list')")
public class BdStuAccountTurnoverController extends BaseController {

    @Resource
    private PlatformManager platformManager;

    /**
     * 查询家长充值流水
     *
     * @param bdStuAccountTurnoverVO 入参
     * @return 家长充值流水列表
     * @author tyh
     */
    @ApiOperation(value = "查询家长充值流水列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "BdStuAccountTurnoverVO", value = "机构账户流水封装类", dataType = "BdStuAccountTurnoverVO")
    @GetMapping("/rechargeList")
    public TableDataInfo rechargeList(BdStuAccountTurnoverVO bdStuAccountTurnoverVO) {
        startPage();
        // 设置学员流水业务状态为2(充值业务) 作为查询条件
        List<Integer> listBusinessTypes = new ArrayList<>();
        listBusinessTypes.add(OrderEnum.B_PURCHASE_JX_POINT.getCode());
        bdStuAccountTurnoverVO.setListBusinessType(listBusinessTypes);
        List<BdStuAccountTurnoverBO> list = platformManager.queryStuListPageHelp(bdStuAccountTurnoverVO);
        if (list == null || list.isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(list);
    }

    /**
     * 查询家长端流水-支出流水
     *
     * @param bdStuAccountTurnoverVO 入参
     * @return 家长支出水列表
     * @author tyh
     */
    @ApiOperation(value = "查询学员支出流水列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "BdStuAccountTurnoverVO", value = "学员账户流水封装类", dataType = "BdStuAccountTurnoverVO")
    @GetMapping("/outlayList")
    public TableDataInfo outlayList(BdStuAccountTurnoverVO bdStuAccountTurnoverVO) {
        startPage();
        //流水业务状态为 3消费鉴学点 4、团购购买 5、直播购买 6、录播购买、 7、分销购买,8=线下课程续费
        List<Integer> listBusinessTypes = new ArrayList<>();
        listBusinessTypes.add(OrderEnum.B_EXPENSE_JX_POINT.getCode());
        listBusinessTypes.add(OrderEnum.B_GROUP_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_LIVE_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_RECORD_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_DISTRIBUTION_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_OFFLINE_COURSE_PURCHASE.getCode());
        bdStuAccountTurnoverVO.setListBusinessType(listBusinessTypes);

        List<BdStuAccountTurnoverBO> list = platformManager.queryStuListPageHelp(bdStuAccountTurnoverVO);
        if (list == null || list.isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(list);
    }

    /**
     * 导出家长充值流水
     *
     * @param bdStuAccountTurnoverVO 封装
     * @return 结果
     */
    @ApiOperation(value = "导出家长充值流水", httpMethod = "POST")
    @GetMapping("/exportIncomeList")
    public AjaxResult exportIncomeList(BdStuAccountTurnoverVO bdStuAccountTurnoverVO) {
        List<Integer> listBusinessTypes = new ArrayList<>();
        listBusinessTypes.add(OrderEnum.B_PURCHASE_JX_POINT.getCode());
        bdStuAccountTurnoverVO.setListBusinessType(listBusinessTypes);
        List<BdStuAccountTurnoverBO> list = platformManager.queryStuListPageHelp(bdStuAccountTurnoverVO);
        if (list == null || list.isEmpty()) {
            return AjaxResult.error("没有对应的收入流水数据，无法导出数据");
        }
        ExcelUtil<BdStuAccountTurnoverBO> util = new ExcelUtil<>(BdStuAccountTurnoverBO.class);
        String sheetName = "parent_income_record";
        return util.exportExcel(list, sheetName);
    }

    /**
     * 导出支出流水列表
     *
     * @param bdStuAccountTurnoverVO 入参
     * @return 导出支出流水列表
     * @author tyh
     */
    @ApiOperation(value = "导出支出流水列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "BdStuAccountTurnoverVO", value = "学员账户流水封装类", dataType = "BdStuAccountTurnoverVO")
    @GetMapping("/exportOutlayList")
    public AjaxResult exportOutlayList(BdStuAccountTurnoverVO bdStuAccountTurnoverVO) {
        List<Integer> listBusinessTypes = new ArrayList<>();
        listBusinessTypes.add(OrderEnum.B_EXPENSE_JX_POINT.getCode());
        listBusinessTypes.add(OrderEnum.B_GROUP_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_LIVE_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_RECORD_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_DISTRIBUTION_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_OFFLINE_COURSE_PURCHASE.getCode());
        bdStuAccountTurnoverVO.setListBusinessType(listBusinessTypes);

        List<BdStuAccountTurnoverBO> list = platformManager.queryStuListPageHelp(bdStuAccountTurnoverVO);
        if (list == null || list.isEmpty()) {
            return AjaxResult.error("没有对应的支出流水数据，无法导出数据");
        }
        ExcelUtil<BdStuAccountTurnoverBO> util = new ExcelUtil<>(BdStuAccountTurnoverBO.class);
        String sheetName = "parent_outlay_record";
        return util.exportExcel(list, sheetName);
    }

    /**
     * 查询学员端提现流水
     */
    @GetMapping("/withdrawRecord")
    public TableDataInfo withdrawRecord(StuWithdrawRecordVO stuWithdrawRecordVO) {
        startPage();
        List<BdStuWithdrawRecordBO> list = platformManager.getWithdrawRecord(stuWithdrawRecordVO);
        if (list == null || list.isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(list);
    }

    /**
     * 下载提现流水
     */
    @GetMapping("/downloadWithdraw")
    public AjaxResult downloadWithdraw(StuWithdrawRecordVO stuWithdrawRecordVO) {
        List<BdStuWithdrawRecordBO> list = platformManager.getWithdrawRecord(stuWithdrawRecordVO);
        ExcelUtil<BdStuWithdrawRecordBO> util = new ExcelUtil<>(BdStuWithdrawRecordBO.class);
        String sheetName = "withdraw_record";
        return util.exportExcel(list, sheetName);
    }
}
