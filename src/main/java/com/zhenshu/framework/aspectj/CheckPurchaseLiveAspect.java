package com.zhenshu.framework.aspectj;

import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.cloud.mec.base.domain.bo.BdCampusBO;
import com.zhenshu.cloud.mec.base.domain.vo.BdCampusVO;
import com.zhenshu.cloud.mec.base.service.IBdCampusService;
import com.zhenshu.common.enums.base.CampusEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.framework.web.domain.ResultCode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/11/26 9:51
 * @desc 校验是否购买直播录播功能切面类
 */
@Aspect
@Component
public class CheckPurchaseLiveAspect {

    private static final Logger log = LoggerFactory.getLogger(CheckPurchaseLiveAspect.class);

    private static final String GET_CAMPUS_ACCOUNT = "getCampusAccount";

    private static final String PAY_LIVE = "pay";

    @Resource
    private IBdCampusService campusService;

    /**
     * 定义切入点：
     * 注解@within作用于类上
     * 注解@annotation作用于方法上
     */
    @Pointcut("@within(com.zhenshu.framework.aspectj.lang.annotation.CheckPurchaseLive)")
    public void cutMethod() {
    }

    /**
     * 前置通知：在目标方法执行前调用
     */
    @Before("cutMethod()")
    public void checkIsLive(JoinPoint joinPoint) {
        Long campusId = SecurityUtils.getLoginCampusId();
        BdCampusBO campusBO = campusService.selectBdCampusById(campusId);
        if (campusBO == null){
            throw new CustomException("未查询到该校区信息");
        }

        // 判断执行的方法是什么，如果查询账户或者是购买直播购买的功能则给过
        String methodName = joinPoint.getSignature().getName();
        if (GET_CAMPUS_ACCOUNT.equals(methodName)){
            return;
        }
        if (PAY_LIVE.equals(methodName)){
            return;
        }
        if (campusBO.getIsPurchaseLive() == null || CampusEnum.IS_NOT_PURCHASE_LIVE.getCode().equals(campusBO.getIsPurchaseLive())){
            throw new CustomException(ResultCode.PURCHASE_LIVE.message(), ResultCode.PURCHASE_LIVE.code());
        }
        //校验直播权限设置时间是否过期
        //获取当前时间
        Date nowDate = DateUtils.getNowDate();
        //获取直播到期时间-判断直播到期时间
        Date liveEndDate = campusBO.getLiveEndTime();
        //直播到期时间小于当前时间
        if (liveEndDate.before(nowDate)){
            BdCampusVO bdCampusVO = new BdCampusVO();
            bdCampusVO.setId(campusBO.getId());
            bdCampusVO.setIsPurchaseLive(CampusEnum.IS_NOT_PURCHASE_LIVE.getCode());
            campusService.upIsPurchaseLiveOrIsMember(bdCampusVO);
        }
    }
}
