package com.zhenshu.jianxue.mec.project.common;

import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.core.lang.IdUtils;
import com.zhenshu.common.utils.VerifyCodeUtils;
import com.zhenshu.common.utils.sign.Base64;
import com.zhenshu.framework.redis.RedisCache;
import com.zhenshu.framework.web.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 *
 * @author zhenshu
 */
@RestController
public class CaptchaController {

    private static final Logger log = LoggerFactory.getLogger(CaptchaController.class);

    @Autowired
    private RedisCache redisCache;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public AjaxResult getCode(HttpServletResponse response) throws IOException {
        String uuid = IdUtils.simpleUUID();

        //StopWatch stopWatch = new StopWatch("captchaImage:".concat(uuid));

        //stopWatch.start(":生成随机字串");
        // 生成随机字串
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        //stopWatch.stop();

        // 唯一标识
        //stopWatch.start(":唯一标识存redis");

        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        redisCache.setCacheObject(verifyKey, verifyCode, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        //stopWatch.stop();

        // 生成图片
        //stopWatch.start(":生成图片");
        int w = 111, h = 36;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(w, h, stream, verifyCode);
        //stopWatch.stop();
        try {
            AjaxResult ajax = AjaxResult.success();
            ajax.put("uuid", uuid);
            ajax.put("img", Base64.encode(stream.toByteArray()));

            //log.info("captchaImage:{} finished,the whole process takes time:{} millis,the details:{}",
            //       uuid, stopWatch.getTotalTimeMillis(), stopWatch.prettyPrint());
            return ajax;
        } catch (Exception e) {
            log.error("captchaImage error", e);
            return AjaxResult.error(e.getMessage());
        } finally {
            stream.close();
        }
    }
}
