package com.zhenshu.cloud.platform.statistics.controller;


import com.zhenshu.common.enums.account.CampusTurnoverEnum;
import com.zhenshu.common.enums.account.OrderEnum;
import com.zhenshu.common.utils.poi.ExcelUtil;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.AjaxResult;
import com.zhenshu.framework.web.page.TableDataInfo;
import com.zhenshu.cloud.mec.account.domain.bo.BdCampusAccountTurnoverBO;
import com.zhenshu.cloud.mec.account.domain.vo.BdCampusAccountTurnoverVO;
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
 * 机构流水 Controller
 *
 * @author tyh
 * @date 2021-1-7
 */
@Api(value = "机构账户流水", tags = "机构流水接口")
@RestController
@RequestMapping("/platform/camAccountTurnover")
@PreAuthorize("@ss.hasPermi('platform:mecturnover:list')")
public class BdCampusAccountTurnoverController extends BaseController {

    @Resource
    private PlatformManager platformManager;

    /**
     * ---所有流水状态集合---
     * 2、购买鉴学点 3、消费鉴学点  4、团购购买 5、直播购买
     * 6、录播购买7、分销购买,8=线下课程续费，9=会员购买，
     * 10=直播录播购买,11、机构签到12、提现
     */

    /**
     * 查询机构充值流水
     *
     * @param bdCampusAccountTurnoverVO 入参
     * @return 机构充值流水列表
     * @author tyh
     */
    @ApiOperation(value = "查询机构充值流水列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "BdCampusAccountTurnoverVO", value = "机构账户流水封装类", dataType = "BdCampusAccountTurnoverVO")
    @GetMapping("/rechargeList")
    public TableDataInfo rechargeList(BdCampusAccountTurnoverVO bdCampusAccountTurnoverVO) {
        startPage();
        //(充值业务)2、购买鉴学点作为查询条件
        List<Integer> listBusinessTypes = new ArrayList<>();
        listBusinessTypes.add(OrderEnum.B_PURCHASE_JX_POINT.getCode());
        bdCampusAccountTurnoverVO.setListBusinessType(listBusinessTypes);
        List<BdCampusAccountTurnoverBO> list = platformManager.rechargeList(bdCampusAccountTurnoverVO);
        if (list == null || list.isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(list);
    }

    /**
     * 机构支出流水列表
     *
     * @param bdCampusAccountTurnoverVO 入参
     * @return 机构支出流水列表
     * @author tyh
     */
    @ApiOperation(value = "查询机构支出流水列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "BdCampusAccountTurnoverVO", value = "机构账户流水封装类", dataType = "BdCampusAccountTurnoverVO")
    @GetMapping("/outlayList")
    public TableDataInfo outlayList(BdCampusAccountTurnoverVO bdCampusAccountTurnoverVO) {
        startPage();
        //(支出业务) 3消费鉴学点 9=会员购买，10=直播录播购买
        List<Integer> listBusinessTypes = new ArrayList<>();
        listBusinessTypes.add(OrderEnum.B_EXPENSE_JX_POINT.getCode());
        listBusinessTypes.add(OrderEnum.B_MEMBER_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_LIVE_RECORD_PURCHASE.getCode());
        bdCampusAccountTurnoverVO.setListBusinessType(listBusinessTypes);
        List<BdCampusAccountTurnoverBO> list = platformManager.queryListPageHelper(bdCampusAccountTurnoverVO);
        if (list == null || list.isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(list);
    }

    /**
     * 机构收入流水列表
     *
     * @param bdCampusAccountTurnoverVO 入参
     * @return 机构收入流水列表
     * @author tyh
     */
    @ApiOperation(value = "机构收入流水列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "BdCampusAccountTurnoverVO", value = "机构账户流水封装类", dataType = "BdCampusAccountTurnoverVO")
    @GetMapping("/incomeList")
    public TableDataInfo incomeList(BdCampusAccountTurnoverVO bdCampusAccountTurnoverVO) {
        startPage();
        // 4=团购购买，5=直播购买 6=录播购买 7=分销购买 8= 线下课程续费(收入流水业务) 11=机构签到
        List<Integer> listBusinessTypes = new ArrayList<>();
        listBusinessTypes.add(OrderEnum.B_GROUP_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_LIVE_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_RECORD_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_DISTRIBUTION_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_OFFLINE_COURSE_PURCHASE.getCode());
        listBusinessTypes.add(CampusTurnoverEnum.SIGN.getCode());
        bdCampusAccountTurnoverVO.setListBusinessType(listBusinessTypes);

        List<BdCampusAccountTurnoverBO> list = platformManager.queryListPageHelper(bdCampusAccountTurnoverVO);
        if (list == null || list.isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(list);
    }

    /**
     * 机构提现流水列表
     *
     * @param bdCampusAccountTurnoverVO 入参
     * @return 机构提现流水列表
     * @author tyh
     */
    @ApiOperation(value = "机构提现流水列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "BdCampusAccountTurnoverVO", value = "机构账户流水封装类", dataType = "BdCampusAccountTurnoverVO")
    @GetMapping("/withdrawList")
    public TableDataInfo withdrawList(BdCampusAccountTurnoverVO bdCampusAccountTurnoverVO) {
        startPage();
        //12=提现 业务
        List<Integer> listBusinessTypes = new ArrayList<>();
        listBusinessTypes.add(CampusTurnoverEnum.WITHDRAWAL.getCode());
        bdCampusAccountTurnoverVO.setListBusinessType(listBusinessTypes);

        List<BdCampusAccountTurnoverBO> list = platformManager.withdrawList(bdCampusAccountTurnoverVO);
        if (list == null || list.isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(list);
    }


    /**
     * 导出机构收入流水
     *
     * @param bdCampusAccountTurnoverVO 流水导出封装
     * @return 结果
     * @author tyh
     */
    @ApiOperation(value = "导出机构收入流水", httpMethod = "POST")
    @GetMapping("/exportIncomeList")
    public AjaxResult exportIncomeList(BdCampusAccountTurnoverVO bdCampusAccountTurnoverVO) {
        List<Integer> listBusinessTypes = new ArrayList<>();
        listBusinessTypes.add(OrderEnum.B_GROUP_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_LIVE_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_RECORD_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_DISTRIBUTION_PURCHASE.getCode());
        listBusinessTypes.add(OrderEnum.B_OFFLINE_COURSE_PURCHASE.getCode());
        listBusinessTypes.add(CampusTurnoverEnum.SIGN.getCode());
        bdCampusAccountTurnoverVO.setListBusinessType(listBusinessTypes);

        List<BdCampusAccountTurnoverBO> list = platformManager.queryListPageHelper(bdCampusAccountTurnoverVO);
        if (list == null || list.isEmpty()) {
            return AjaxResult.error("没有对应的收入流水数据，无法导出数据");
        }
        ExcelUtil<BdCampusAccountTurnoverBO> util = new ExcelUtil<>(BdCampusAccountTurnoverBO.class);
        String sheetName = "agency_income_record";
        return util.exportExcel(list, sheetName);
    }


}
