package com.zhenshu.framework.security.service;

import javax.annotation.Resource;

import com.zhenshu.framework.web.exception.GlobalExceptionHandler;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.exception.user.CaptchaException;
import com.zhenshu.common.exception.user.CaptchaExpireException;
import com.zhenshu.common.exception.user.UserPasswordNotMatchException;
import com.zhenshu.common.utils.MessageUtils;
import com.zhenshu.framework.manager.AsyncManager;
import com.zhenshu.framework.manager.factory.AsyncFactory;
import com.zhenshu.framework.redis.RedisCache;
import com.zhenshu.framework.redis.RedisUtils;
import com.zhenshu.framework.security.LoginUser;

/**
 * 登录校验方法
 *
 * @author zhenshu
 */
@Component
public class SysLoginService {
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Resource
    private RedisUtils redisUtils;

    private static final Logger log = LoggerFactory.getLogger(SysLoginService.class);

    /**
     * 同一账号连续登录失败次数上限
     */
    private static final int LOGIN_FAIL_LIMIT = 5;

    /**
     * 触发失败上限后的锁定时长（秒）
     */
    private static final long LOGIN_LOCK_SECONDS = 600L;

    /**
     * 登录失败次数缓存 key 前缀
     */
    private static final String LOGIN_FAIL_KEY_PREFIX = "login_fail_count:";

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @return 结果
     */
    public String login(String username, String password) {
        String failKey = LOGIN_FAIL_KEY_PREFIX + username;
        // 登录失败次数限制：超过阈值后锁定一段时间，防止暴力破解
        if (isLocked(failKey)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, "登录失败次数过多，账号已临时锁定"));
            throw new CustomException("登录失败次数过多，请10分钟后再试");
        }
        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用 UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            // 日志中不得记录明文密码
            log.error("登录的时候出现异常 , userName ：{} ", username, e);
            recordLoginFail(failKey);
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new CustomException(e.getMessage());
            }
        }
        // 登录成功，清除失败计数
        clearLoginFail(failKey);
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 判断账号当前是否处于失败锁定状态
     */
    private boolean isLocked(String failKey) {
        String count = redisUtils.getStr(failKey);
        if (count == null || count.isEmpty()) {
            return false;
        }
        try {
            return Integer.parseInt(count) >= LOGIN_FAIL_LIMIT;
        } catch (NumberFormatException e) {
            log.warn("解析登录失败次数失败，key={}，value={}", failKey, count);
            return false;
        }
    }

    /**
     * 累计一次登录失败次数，首次失败时设置锁定时长
     */
    private void recordLoginFail(String failKey) {
        try {
            long count = redisUtils.incr(failKey, 1);
            if (count == 1L) {
                redisUtils.expire(failKey, LOGIN_LOCK_SECONDS);
            }
        } catch (Exception e) {
            log.warn("记录登录失败次数失败，key={}", failKey, e);
        }
    }

    /**
     * 清除登录失败计数
     */
    private void clearLoginFail(String failKey) {
        try {
            redisUtils.del(failKey);
        } catch (Exception e) {
            log.warn("清除登录失败次数失败，key={}", failKey, e);
        }
    }
}
