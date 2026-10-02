package com.zhenshu.community;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 社区版最小集成测试骨架（OS007 步骤 5）：登录成功 / 错误口令 / 退出后令牌失效。
 *
 * <p>存在的意义有两个：
 * <ol>
 *   <li>证明「能启动、能登录」不是主观描述，而是一条可重复执行的自动化断言；</li>
 *   <li>证明测试真的在跑 —— 本模块 pom 里带 {@code <skipTests>true</skipTests>}（打包用），
 *       因此必须用 profile 覆盖它执行：{@code mvn -o test -Pcommunity-verify -Dtest=CommunityLoginSmokeTest}。
 *       如果这个 profile 忘了带，surefire 会跳过全部用例，测试数量为 0，一眼能看出来。</li>
 * </ol>
 *
 * <p>前置条件（与 {@code scripts/smoke-test.py} 相同）：库名必须是社区版库（{@code yunduo_community*}），
 * 且已完成 001 + 002 初始化；环境变量 MYSQL_* / REDIS_* 由运行环境提供。
 *
 * <p>口令处理：本类不写死任何口令，运行时随机生成，仅用于覆盖 admin 的 BCrypt 哈希。
 * 产品级的一次性初始化流程 {@link CommunityInitializer}（OS009）会在上下文启动时读取
 * {@code INITIAL_ADMIN_PASSWORD}：为了在**空库**上也能直接跑本测试，{@link #initialAdminPassword}
 * 在运行环境未提供该变量时注入一个进程内随机值 —— 它只用于让初始化流程通过，
 * 随后被各用例的随机冒烟口令覆盖，因此不会成为任何可用的凭据，也不落盘。
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                // 与 scripts/env.local.example 一致：Redis 走社区版专用逻辑库与独立 key 前缀
                "spring.redis.database=${JX_REDIS_DB:12}",
                "spring.redis.cache_name=${JX_CACHE_NAME:yunduo_community}",
                "spring.datasource.druid.stat-view-servlet.enabled=false",
                "spring.devtools.restart.enabled=false"
        })
@ContextConfiguration(initializers = CommunityLoginSmokeTest.InitialAdminPasswordInitializer.class)
@DisplayName("社区版最小集成测试：登录 / 错误口令 / 退出失效")
class CommunityLoginSmokeTest {

    private static final String ADMIN_USER = System.getProperty("smoke.admin.user", "admin");
    private static final String ADMIN_PASSWORD = "smoke-" + UUID.randomUUID().toString().replace("-", "");

    /**
     * 给「空库首次启动」的一次性初始化流程提供一个随机口令。
     *
     * <p>该初始化器在 Spring 上下文创建**之前**执行，因此可以决定启动期的属性值。
     * 若运行环境已经设置了 {@code INITIAL_ADMIN_PASSWORD}，则尊重环境变量。
     * 无论取哪个值，本测试随后都会用 {@link #ADMIN_PASSWORD} 覆盖 admin 的口令，
     * 所以这里注入的值不会成为任何可用的凭据，也不会落盘。
     *
     * <p>不用 {@code @DynamicPropertySource}：它从 Spring Framework 5.2.5 才有，
     * 而 Spring Boot 2.2.5.RELEASE 依赖的是 5.2.4.RELEASE。
     */
    static class InitialAdminPasswordInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            String fromEnv = System.getenv("INITIAL_ADMIN_PASSWORD");
            String value = (fromEnv == null || fromEnv.trim().isEmpty())
                    ? "it-" + UUID.randomUUID() : fromEnv;
            TestPropertyValues.of("INITIAL_ADMIN_PASSWORD=" + value).applyTo(context);
        }
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private DataSource dataSource;

    @BeforeAll
    static void assertCommunityDatabase() {
        String db = System.getenv("MYSQL_DATABASE");
        assertNotNull(db, "缺少环境变量 MYSQL_DATABASE：本测试只允许跑在社区版库上");
        assertTrue(db.startsWith("yunduo_community"),
                "拒绝执行：MYSQL_DATABASE=" + db + " 不是社区版库（要求前缀 yunduo_community）");
    }

    private void prepareAdminPassword() throws Exception {
        String hash = new BCryptPasswordEncoder().encode(ADMIN_PASSWORD);
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE sys_user SET password = ? WHERE user_name = ?")) {
            ps.setString(1, hash);
            ps.setString(2, ADMIN_USER);
            assertEquals(1, ps.executeUpdate(), "admin 账号不存在或未唯一命中，种子可能没执行");
        }
    }

    private String url(String path) {
        return "http://127.0.0.1:" + port + path;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> login(String password) {
        Map<String, String> body = new HashMap<>();
        body.put("username", ADMIN_USER);
        body.put("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Map> resp = rest.postForEntity(url("/login"), new HttpEntity<>(body, headers), Map.class);
        assertNotNull(resp.getBody(), "登录接口没有返回 JSON 主体");
        return resp.getBody();
    }

    private Map<String, Object> getInfo(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        ResponseEntity<Map> resp = rest.exchange(url("/getInfo"), HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        return resp.getBody();
    }

    @Test
    @DisplayName("用例1：正确的管理员口令可以登录并拿到令牌")
    void loginSucceeds() throws Exception {
        prepareAdminPassword();
        Map<String, Object> result = login(ADMIN_PASSWORD);
        assertEquals(200, result.get("code"), "登录应返回 code=200，实际：" + result);
        Object token = result.get("token");
        assertNotNull(token, "登录成功必须返回 token");
        assertTrue(String.valueOf(token).length() > 20, "token 长度异常：" + String.valueOf(token).length());
    }

    @Test
    @DisplayName("用例2：错误口令必须被拒绝（且不返回令牌）")
    void loginWithWrongPasswordIsRejected() throws Exception {
        prepareAdminPassword();
        Map<String, Object> result = login(ADMIN_PASSWORD + "-wrong");
        assertFalse(result.containsKey("token"), "错误口令不得返回 token，实际：" + result);
        assertTrue(result.get("code") == null || !Integer.valueOf(200).equals(result.get("code")),
                "错误口令不应返回 code=200，实际：" + result);
    }

    @Test
    @DisplayName("用例3：退出登录后，原令牌立即失效")
    void logoutInvalidatesToken() throws Exception {
        prepareAdminPassword();
        Map<String, Object> loginResult = login(ADMIN_PASSWORD);
        String token = String.valueOf(loginResult.get("token"));
        assertNotNull(loginResult.get("token"));

        // 退出前令牌可用
        Map<String, Object> before = getInfo(token);
        assertNotNull(before);
        assertEquals(200, before.get("code"), "退出前 /getInfo 应当可用");

        // 退出
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        rest.exchange(url("/logout"), HttpMethod.POST, new HttpEntity<>("{}", headers), Map.class);

        // 退出后同一令牌必须失效
        Map<String, Object> after = getInfo(token);
        assertNotNull(after);
        assertEquals(401, after.get("code"), "退出后原令牌应答 401，实际：" + after);
    }
}
