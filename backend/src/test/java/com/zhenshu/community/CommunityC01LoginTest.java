package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS012 最低测试集 ①：登录成功、错误口令、退出后令牌失效。
 *
 * <p>负向对照：{@code 登录成功判据} 必须对「成功响应」判真、对「错误口令响应」判假；
 * 否则判据恒真，测试通过也说明不了问题。
 */
@DisplayName("OS012-01 登录成功 / 错误口令 / 退出失效")
class CommunityC01LoginTest extends CommunityTestBase {

    @Test
    @DisplayName("正确口令→200+令牌；错误口令→拒绝且无令牌；退出后原令牌→401")
    void loginThreeStates() {
        resetAdminPassword();

        ApiResult success = login(ADMIN_USER, ADMIN_PASSWORD);
        ApiResult wrongPwd = login(ADMIN_USER, ADMIN_PASSWORD + "-wrong");

        Predicate<ApiResult> judgeLoginOk = r -> Integer.valueOf(200).equals(r.code())
                && r.token() != null && r.token().length() > 20;
        // 判据自检：成功样本必须判真、错误口令样本必须判假
        assertJudge("登录成功判据", judgeLoginOk, success, wrongPwd);

        assertEquals(200, success.code(), "正确口令应登录成功：" + success);
        assertNotNull(success.token(), "登录成功必须返回 token");

        assertTrue(!judgeLoginOk.test(wrongPwd), "错误口令不得满足登录成功判据：" + wrongPwd);
        assertFalse(Integer.valueOf(200).equals(wrongPwd.code()), "错误口令不得返回 code=200");
        assertEquals(null, wrongPwd.token(), "错误口令不得返回 token");

        // 退出后同一令牌立即失效（读 body.code，不读 HTTP 状态码：本项目令牌失效是 HTTP 200 + code=401）
        String token = success.token();
        ApiResult before = get("/getInfo", token);
        assertEquals(200, before.code(), "退出前 /getInfo 应可用：" + before);

        ApiResult logout = post("/logout", "{}", token);
        assertEquals(200, logout.code(), "/logout 应成功：" + logout);

        ApiResult after = get("/getInfo", token);
        assertEquals(401, after.code(), "退出后原令牌必须失效（code=401）：" + after);

        Predicate<ApiResult> judgeTokenAlive = r -> Integer.valueOf(200).equals(r.code());
        assertJudge("令牌有效判据", judgeTokenAlive, before, after);
    }
}
