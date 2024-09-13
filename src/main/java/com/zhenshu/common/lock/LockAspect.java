package com.zhenshu.common.lock;

import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.framework.redis.RedisUtils;
import com.zhenshu.cloud.mec.project.system.domain.SysUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author jing
 * @version 1.0
 * @desc 防重点的切片拦截
 * @date 2021/2/19 0019 11:05
 **/
@Aspect
@Service
public class LockAspect {


    @Pointcut("@annotation(com.zhenshu.common.lock.LockFunction)")
    private void lockMethod() {
    }

    @Resource
    private RedisUtils cache;
    @Value("${spring.redis.cache_name}")
    public String cacheName;

    private final Log logger = LogFactory.getLog(LockAspect.class);


    /**
     * 使用@around 环绕通知=前置+目标方法执行+后置通知
     */
    @Around("lockMethod()")
    public Object getCache(ProceedingJoinPoint joinPoint) {
        // 获取方法参数值数组
        Object[] args = joinPoint.getArgs();
        // 增加访问次数
        String cacheKey = addIntoTimes(joinPoint);

        // 方法结束后，删除缓存
        Object object;
        try {
            object = joinPoint.proceed(args);
        } catch (Throwable throwable) {
            logger.error(throwable);
            throw new CustomException(throwable.getMessage());
            // 写在finally 的原因是改方法可能会抛出异常
        } finally {
            cache.del(cacheKey);
        }
        return object;
    }

    /**
     * 前置方法，记录访问次数
     *
     * @param joinPoint 切点
     * @return 缓存key
     */
    private String addIntoTimes(ProceedingJoinPoint joinPoint) {
        // 获取注解上的传参
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LockFunction annotation = signature.getMethod().getAnnotation(LockFunction.class);
        String methodName = annotation.methodName();

        // 获得机构id与校区id
        SysUser sysUser = SecurityUtils.getSysUser();
        Long mecId = sysUser.getMecId();
        Long campusId = sysUser.getCampusId();
        // 自增缓存，查看是否有同时操作的用户
        Long userId = sysUser.getUserId();
        String cacheKey;
        // 需要对校区id进行判空，因为登录成功可能是机构端
        if (campusId == null) {
            cacheKey = String.format("%s_lock_mec_user_method_%s_%s_%s", cacheName, mecId, userId, methodName);
        } else {
            cacheKey = String.format("%s_lock_mec_campus_user_method_%s_%s_%s_%s", cacheName, mecId, campusId, userId, methodName);
        }
        long inviteNum = cache.incr(cacheKey, 1);
        if (inviteNum > 1) {
            throw new CustomException("您操作的太快了，请稍后重试", 40312);
        }
        // 设置3s的超时时间，避免出现问题后一直无法操作
        cache.expire(cacheKey, 3);
        return cacheKey;
    }

}
