package com.zhenshu.zhenxueyou.platform.statistics.manager;

import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.base.PayEnum;
import com.zhenshu.common.manager.platform.mec.PlatWithMechanismManager;
import com.zhenshu.common.manager.platform.min.PlatWithMinManager;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.zhenxueyou.mec.account.domain.bo.BdCampusAccountTurnoverBO;
import com.zhenshu.zhenxueyou.mec.account.domain.bo.BdStuAccountTurnoverBO;
import com.zhenshu.zhenxueyou.mec.account.domain.vo.BdCampusAccountTurnoverVO;
import com.zhenshu.zhenxueyou.mec.account.domain.vo.BdStuAccountTurnoverVO;
import com.zhenshu.zhenxueyou.mec.account.service.IBdOrderService;
import com.zhenshu.zhenxueyou.platform.statistics.domain.bo.BdPlatformAccountBO;
import com.zhenshu.zhenxueyou.platform.statistics.domain.bo.BdStuWithdrawRecordBO;
import com.zhenshu.zhenxueyou.platform.statistics.domain.bo.DayDataBO;
import com.zhenshu.zhenxueyou.platform.statistics.domain.bo.YearDataBO;
import com.zhenshu.zhenxueyou.platform.statistics.domain.vo.StuWithdrawRecordVO;
import com.zhenshu.zhenxueyou.platform.statistics.service.IBdStuWithdrawRecordService;
import com.zhenshu.zhenxueyou.platform.statistics.service.impl.BdPlatformAccountServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/11/23 15:43
 * @desc
 */
@Service
public class PlatformManager {

    @Resource
    private PlatWithMechanismManager platWithMechanismManager;
    @Resource
    private PlatWithMinManager platWithMinManager;
    @Resource
    private BdPlatformAccountServiceImpl bdPlatformAccountService;
    @Resource
    private IBdOrderService orderService;
    @Resource
    private IBdStuWithdrawRecordService withdrawRecordService;


    /**
     * 获取机构数，家长数
     */
    public Map<String, Integer> list() {
        Integer mecTotal = platWithMechanismManager.getMechanismTotal();
        Integer parentTotal = platWithMinManager.getMinTotal();

        Map<String, Integer> map = new HashMap<>(2);
        map.put("mecTotal", mecTotal);
        map.put("parentTotal", parentTotal);
        return map;
    }

    /**
     * 机构数
     *
     * @param day 时间
     * @return 机构数
     */
    public List<DayDataBO> mecNum(Integer day) {
        List<DayDataBO> list = platWithMechanismManager.mecNum(day);
        return formatData(day, list);
    }

    /**
     * 家长数
     *
     * @param day 时间
     * @return 家长数
     */
    public List<DayDataBO> minNum(Integer day) {
        List<DayDataBO> list = platWithMinManager.minNum(day);
        return formatData(day, list);
    }

    /**
     * 格式化日期，无数据补0
     *
     * @param day 日期
     * @param bos 格式
     */
    private List<DayDataBO> formatData(Integer day, List<DayDataBO> bos) {

        HashMap<String, DayDataBO> dataMap = new HashMap<>();
        for (DayDataBO bo : bos) {
            dataMap.put(bo.getDate(), bo);
        }

        List<String> dayList = DateUtils.getSubDayList(day);
        ArrayList<DayDataBO> result = new ArrayList<>();
        for (String date : dayList) {
            if (dataMap.containsKey(date)) {
                DayDataBO bo = dataMap.get(date);
                if (bo.getNum() == null) {
                    bo.setNum(Constants.INITIAL_VALUE);
                }
                result.add(bo);
            } else {
                DayDataBO bo = new DayDataBO();
                bo.setDate(date);
                bo.setNum(Constants.INITIAL_VALUE);
                result.add(bo);
            }
        }
        return result;
    }

    /**
     * 按年份获得机构入驻数
     *
     * @return 年份获得机构入驻数
     * @author tyh
     */
    public List<YearDataBO> mecNumByYears() {
        return platWithMechanismManager.mecNumByYears();
    }

    /**
     * 按年份统计家长入驻数
     *
     * @return 按年份统计家长入驻数
     * @author tyh
     */
    public List<YearDataBO> stuNumByYears() {
        return platWithMinManager.stuNumByYears();
    }

    /**
     * 家长端充值+支出流水列表查询
     *
     * @param bdStuAccountTurnoverVO 入参
     * @return 家长充值流水列表
     */
    public List<BdStuAccountTurnoverBO> queryStuListPageHelp(BdStuAccountTurnoverVO bdStuAccountTurnoverVO) {
        //设置固定支付类型
        List<BdStuAccountTurnoverBO> list = platWithMechanismManager.queryStuListPageHelp(bdStuAccountTurnoverVO);
        for (BdStuAccountTurnoverBO bo : list) {
            bo.setPayType(PayEnum.PAY_WX.getCode());
        }
        return list;
    }

    /**
     * 查询机构流水列表{支出，充值，收入
     *
     * @param bdCampusAccountTurnoverVO 入参
     * @return 机构充值流水列表
     * @author tyh
     */
    public List<BdCampusAccountTurnoverBO> queryListPageHelper(BdCampusAccountTurnoverVO bdCampusAccountTurnoverVO) {
        return platWithMechanismManager.queryListPageHelper(bdCampusAccountTurnoverVO);
    }

    /**
     * 机构提现流水列表
     *
     * @param bdCampusAccountTurnoverVO 入参
     * @return 机构提现流水列表
     * @author tyh
     */
    public List<BdCampusAccountTurnoverBO> withdrawList(BdCampusAccountTurnoverVO bdCampusAccountTurnoverVO) {
        return platWithMechanismManager.withdrawList(bdCampusAccountTurnoverVO);
    }

    /**
     * 机构充值流水
     */
    public List<BdCampusAccountTurnoverBO> rechargeList(BdCampusAccountTurnoverVO bdCampusAccountTurnoverVO) {
        List<BdCampusAccountTurnoverBO> list = platWithMechanismManager.queryListPageHelper(bdCampusAccountTurnoverVO);
        // 暂时设置机构充值流水的支付类型全部都为微信支付
        for (BdCampusAccountTurnoverBO bo:list ) {
            bo.setPayType(PayEnum.PAY_WX.getCode());
        }
        return list;
    }


    public BdPlatformAccountBO selectBdPlatformAccountById(Long platformId) {
        BdPlatformAccountBO bdPlatformAccountBO = bdPlatformAccountService.selectBdPlatformAccountById(platformId);
        // 获得平台待结算金额
        BigDecimal toBeSettledAmount = orderService.getToBeSettledAmountByPlatform(bdPlatformAccountBO.getId());
        bdPlatformAccountBO.setToBeSettledAmount(toBeSettledAmount);
        return bdPlatformAccountBO;
    }

    /**
     * 获取提现记录
     * @param stuWithdrawRecordVO 提现记录查询如擦
     * @return 提现记录列表
     */
    public List<BdStuWithdrawRecordBO> getWithdrawRecord(StuWithdrawRecordVO stuWithdrawRecordVO) {
        return withdrawRecordService.getWithdrawRecord(stuWithdrawRecordVO);
    }
}
