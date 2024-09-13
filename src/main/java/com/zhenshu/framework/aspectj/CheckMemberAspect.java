package com.zhenshu.framework.aspectj;

import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.cloud.mec.base.domain.bo.BdCampusBO;
import com.zhenshu.cloud.mec.base.domain.vo.BdCampusVO;
import com.zhenshu.cloud.mec.base.service.IBdCampusService;
import com.zhenshu.common.enums.base.CampusEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.framework.web.domain.ResultCode;
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
 * @date 2020/11/26 11:10
 * @desc 校验是否是会员切面类
 */
@Aspect
@Component
public class CheckMemberAspect {

    private static final Logger log = LoggerFactory.getLogger(CheckMemberAspect.class);

    @Resource
    private IBdCampusService campusService;

    /**
     * 定义切入点：
     * 注解@within作用于类上
     * 注解@annotation作用于方法上
     */
    @Pointcut("@within(com.zhenshu.framework.aspectj.lang.annotation.CheckMember) || @annotation(com.zhenshu.framework.aspectj.lang.annotation.CheckMember)")
    public void cutMethod() {
    }

    /**
     * 前置通知：在目标方法执行前调用
     */
    @Before("cutMethod()")
    public void checkIsMember() {
        Long campusId = SecurityUtils.getLoginCampusId();
        BdCampusBO campusBO = campusService.selectBdCampusById(campusId);
        if (campusBO == null) {
            throw new CustomException("未查询到该校区信息");
        }
        if (campusBO.getIsMember() == null || CampusEnum.IS_NOT_MEMBER.getCode().equals(campusBO.getIsMember())) {
            throw new CustomException(ResultCode.MEMBER_PURCHASE.message(), ResultCode.MEMBER_PURCHASE.code());
        }
        //校验会员权限设置时间是否过期
        //获取当前时间
        Date nowDate = DateUtils.getNowDate();
        //获取会员到期时间-判断会员到期
        Date memberEndData = campusBO.getMemberEndTime();
        //会员到期时间低于当前时间
        if (memberEndData.before(nowDate)) {
            BdCampusVO bdCampusVO = new BdCampusVO();
            bdCampusVO.setId(campusBO.getId());
            bdCampusVO.setIsMember(CampusEnum.IS_NOT_MEMBER.getCode());
            campusService.upIsPurchaseLiveOrIsMember(bdCampusVO);
        }
    }
}
