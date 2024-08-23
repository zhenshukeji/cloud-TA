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

    private static final Logger log = LoggerFactory.getLogger(SysLoginService.class);

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * 1@param code     验证码
     * 1@param uuid     唯一标识
     * @return 结果
     */
    public String login(String username, String password) {
         /*
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
            throw new CaptchaException();
        }*/
        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用 UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            log.error("登录的时候出现异常 , userName ：{} , passWord :{}  ", username, password, e);
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new CustomException(e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 生成token
        return tokenService.createToken(loginUser);
    }
}
