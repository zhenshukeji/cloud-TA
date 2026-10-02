package com.zhenshu.community;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 云朵教务社区版 · 启动面硬校验（OS011）。
 *
 * <p>目标（执行指引 §6.3 / 任务书 OS011）：社区版「只提供本地依赖即可启动」，
 * 且**未设置必需变量时必须明确失败，绝不回退到任何硬编码值**。
 *
 * <p>本守卫负责三类断言：
 * <ol>
 *   <li><b>必需变量非空</b>：{@code MYSQL_*}、{@code REDIS_*}、{@code TOKEN_SECRET}。
 *       这些变量在 {@code application-community.yml} 里写作 {@code ${VAR}}（无默认值），
 *       缺失时 Spring 会在占位符解析阶段失败；但「设置了空串」不会被拦住，
 *       因此这里专门补上空串断言。</li>
 *   <li><b>令牌密钥强度</b>：{@code TOKEN_SECRET} 是 JWT 的 HMAC 密钥，
 *       长度不足或形如示例值的密钥等于把签名权交给攻击者。</li>
 *   <li><b>不引入商业凭据</b>：若运行环境仍带着微信 / 支付 / 短信 / 地图 / OSS / 监控平台的变量，
 *       只做 WARN 登记（社区版不会去读它们），不阻断启动。</li>
 * </ol>
 *
 * <p>口令（{@code INITIAL_ADMIN_PASSWORD}）的强度校验不放在这里，而是放在
 * {@link CommunityInitializer}——它只在「管理员口令仍为空」这一条分支上才需要该变量，
 * 提前在这里拦会把「已完成初始化后的常规重启」也一起拦死。
 *
 * @author OS011
 */
@Component
@Order(1)
public class CommunityStartupGuard implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CommunityStartupGuard.class);

    /** 必需且不得为空的环境变量（顺序即报错顺序） */
    private static final String[] REQUIRED = {
            "MYSQL_HOST", "MYSQL_PORT", "MYSQL_DATABASE", "MYSQL_USER", "MYSQL_PASSWORD",
            "REDIS_HOST", "REDIS_PORT", "REDIS_PASSWORD", "TOKEN_SECRET"
    };

    /** 令牌密钥最短长度：JWT HMAC-SHA 密钥，低于 32 字节不具备实际抗爆破能力 */
    private static final int TOKEN_SECRET_MIN_LENGTH = 32;

    /** 令牌密钥至少需要的不同字符数：防止 "aaaa...a" 这类「够长但零熵」的值 */
    private static final int TOKEN_SECRET_MIN_DISTINCT = 8;

    /**
     * 一眼假的示例值（小写比较）。
     *
     * <p>口径说明：这里**只做等值匹配**，不做子串匹配 —— 「secret」「token」是正常密钥里很常见的子串，
     * 一旦用 contains 判断就会把合规的高熵密钥误杀（本机验证环境的 56 位密钥就被这样误杀过一次）。
     * 只有下面这种「整串就是占位符」的形态才用子串判定。
     */
    private static final String[] TOKEN_SECRET_BLACKLIST = {
            "changeme", "change-me", "secret", "token", "placeholder", "local-placeholder-token-secret",
            "your-token-secret", "your_secret", "test", "demo", "123456", "1234567890"
    };

    /** 整串里出现这些子串即判为占位符（等值之外的补充口径） */
    private static final String[] TOKEN_SECRET_PLACEHOLDER_MARKERS = {
            "placeholder", "changeme", "change-me", "your_token", "your-token"
    };

    /** 只登记不阻断：社区版不读这些变量，出现它们说明部署环境还带着商业配置 */
    private static final String[] COMMERCIAL_ONLY = {
            "WX_APPID", "WX_APPSECRET", "WX_PAY_MCH_ID", "WX_PAY_MCH_KEY",
            "ALIYUN_OSS_ACCESS_KEY_ID", "ALIYUN_OSS_ACCESS_KEY_SECRET",
            "ALIYUN_SMS_ACCESS_KEY_ID", "ALIYUN_SMS_ACCESS_KEY_SECRET",
            "BAIDU_AK", "SENTRY_DSN"
    };

    private final Environment environment;

    public CommunityStartupGuard(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        Map<String, String> missing = new LinkedHashMap<>();
        for (String key : REQUIRED) {
            String value = environment.getProperty(key);
            if (value == null || value.trim().isEmpty()) {
                // 只登记「缺失/空串」这件事本身，绝不把变量值写进日志
                missing.put(key, value == null ? "(未设置)" : "(空串)");
            }
        }
        if (!missing.isEmpty()) {
            fail("必需环境变量缺失或为空：" + missing.keySet()
                    + "。社区版不提供任何内置默认值，请在启动前设置它们（详见 scripts/env.local.example）。");
        }

        String secret = environment.getProperty("TOKEN_SECRET", "").trim();
        String reason = checkTokenSecret(secret);
        if (reason != null) {
            fail("TOKEN_SECRET 强度不足：" + reason
                    + "。请改用随机生成的高熵字符串（例如 `openssl rand -base64 48`）。");
        }

        StringBuilder present = new StringBuilder();
        for (String key : COMMERCIAL_ONLY) {
            String v = environment.getProperty(key);
            if (v != null && !v.trim().isEmpty()) {
                if (present.length() > 0) {
                    present.append(", ");
                }
                present.append(key);
            }
        }
        if (present.length() > 0) {
            log.warn("[社区版启动校验] 运行环境仍带有商业能力变量（{}）；社区版不会读取它们，"
                    + "建议从部署环境中移除以免误导。", present);
        }

        log.info("[社区版启动校验] 必需变量齐全且 TOKEN_SECRET 强度达标（长度 {}，不输出内容）。", secret.length());
    }

    /**
     * 校验令牌密钥强度。
     *
     * @return null 表示通过；否则返回「不通过的原因」（不返回密钥内容）
     */
    private String checkTokenSecret(String secret) {
        if (secret.length() < TOKEN_SECRET_MIN_LENGTH) {
            return "长度 " + secret.length() + " < " + TOKEN_SECRET_MIN_LENGTH;
        }
        long distinct = secret.chars().distinct().count();
        if (distinct < TOKEN_SECRET_MIN_DISTINCT) {
            return "不同字符数 " + distinct + " < " + TOKEN_SECRET_MIN_DISTINCT;
        }
        String lower = secret.toLowerCase();
        for (String weak : TOKEN_SECRET_BLACKLIST) {
            if (lower.equals(weak)) {
                return "命中弱密钥黑名单（整串等于示例值）";
            }
        }
        for (String marker : TOKEN_SECRET_PLACEHOLDER_MARKERS) {
            if (lower.contains(marker)) {
                return "命中占位符特征串「" + marker + "」";
            }
        }
        return null;
    }

    private void fail(String message) {
        log.error("**********************************************************************");
        log.error("社区版启动中止：{}", message);
        log.error("**********************************************************************");
        throw new IllegalStateException("社区版启动中止：" + message);
    }
}
