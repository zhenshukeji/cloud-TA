package com.zhenshu.framework.web.exception;

import com.github.binarywang.wxpay.exception.WxPayException;
import com.zhenshu.common.constant.HttpStatus;
import com.zhenshu.common.exception.BaseException;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.exception.DemoModeException;
import com.zhenshu.common.exception.file.FileException;
import com.zhenshu.common.exception.user.NotFoundInfoException;
import com.zhenshu.common.exception.user.UserException;
import com.zhenshu.common.seq.dao.SingleSequenceDao;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.framework.web.domain.AjaxResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 *
 * @author zhenshu
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Log log = LogFactory.getLog(SingleSequenceDao.class);

    /**
     * 基础异常
     */
    @ExceptionHandler(BaseException.class)
    public AjaxResult baseException(BaseException e) {
        log.error(e.getMessage());
        return AjaxResult.error("访问人数过多，请稍后重试");
    }

    /**
     * 基础异常
     */
    @ExceptionHandler(NotFoundInfoException.class)
    public AjaxResult notFoundInfoException(BaseException e) {
        log.error(e.getMessage());
        return AjaxResult.error("没有找到对应的用户信息");
    }

    /**
     * 用户异常
     */
    @ExceptionHandler(UserException.class)
    public AjaxResult userException(UserException e) {
        log.error(e.getMessage());
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(CustomException.class)
    public AjaxResult businessException(CustomException e) {
        log.error(e);
        if (StringUtils.isNull(e.getCode())) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 文件上传异常
     */
    @ExceptionHandler(FileException.class)
    public AjaxResult fileException(FileException e) {
        log.error(e.getMessage());
        if (StringUtils.isNull(e.getCode())) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public AjaxResult handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(HttpStatus.NOT_FOUND, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult handleAuthorizationException(AccessDeniedException e) {
        log.error(e.getMessage());
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    @ExceptionHandler(AccountExpiredException.class)
    public AjaxResult handleAccountExpiredException(AccountExpiredException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public AjaxResult handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error("访问人数过多，请稍后重试");
    }

    /**
     * 微信支付回调异常不捕获
     *
     * @param e 异常信息
     * @throws WxPayException 异常信息
     */
    @ExceptionHandler(WxPayException.class)
    public AjaxResult handleWxPayException(WxPayException e) throws WxPayException {
        log.error(e.getMessage(), e);
        throw new WxPayException(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult validatedBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validExceptionHandler(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public AjaxResult demoModeException(DemoModeException e) {
        return AjaxResult.error("演示模式，不允许操作");
    }
}
