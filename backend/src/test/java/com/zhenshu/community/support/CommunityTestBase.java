package com.zhenshu.community.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS012 核心自动化测试的公共基类。
 *
 * <p>职责边界：
 * <ol>
 *   <li><b>环境守卫</b>：只允许跑在社区版库（{@code MYSQL_DATABASE} 前缀 {@code yunduo_community}），
 *       避免误连开发库；</li>
 *   <li><b>夹具</b>：用 SQL 直接造「教师 / 课程 / 班级 / 学员 / 课时」这五张最小闭环表，
 *       保证用例聚焦在被测行为上，而不是被上游接口的校验规则牵着走；</li>
 *   <li><b>HTTP</b>：{@link #call} 统一返回 {@link ApiResult}（HTTP 状态 + body.code/msg/data），
 *       判据一律读 {@code body.code} —— 本项目权限拒绝的形态是 HTTP 200 + {@code code=403}，
 *       只看 HTTP 状态码会假通过；</li>
 *   <li><b>清理</b>：{@link #cleanup()} 按「学员 / 班级」两条主线回收所有派生行，
 *       保证同一隔离库可重复执行、用例之间不串味；</li>
 *   <li><b>判据自检</b>：{@link #assertJudge} 要求同一条判据必须对「正确样本」判真、
 *       对「坏样本」判假。只报「测试通过」是没有说服力的——判据写瞎了也是通过。</li>
 * </ol>
 *
 * <p>本类不写死任何口令：admin 口令在运行期随机生成并只用于覆盖 BCrypt 哈希，不落盘、不打印。
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
@ContextConfiguration(initializers = CommunityTestBase.InitialAdminPassword.class)
public abstract class CommunityTestBase {

    /** 种子机构 / 校区 id（002_seed_system.sql 固定为 1 / 1）。 */
    protected static final long SEED_MEC_ID = 1L;
    protected static final long SEED_CAMPUS_ID = 1L;

    protected static final String ADMIN_USER = "admin";
    /** 运行期随机口令；只用于覆盖 admin 的 BCrypt 哈希，不落盘、不写日志。 */
    protected static final String ADMIN_PASSWORD = "os012-" + UUID.randomUUID().toString().replace("-", "");

    /** 课消状态：0 到课 / 1 迟到 / 2 请假 / 3 未到 / 4 提前请假（见 CallRecordEnum）。 */
    protected static final int TYPE_PRESENT = 0;
    protected static final int TYPE_LEAVE = 2;
    protected static final int TYPE_ABSENT = 3;

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate rest;

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected JdbcTemplate jdbc;

    @Autowired
    protected org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    /** 本用例创建过夹具的登记表，用于收尾清理。 */
    private final List<Object[]> cleanupRegistry = new ArrayList<>();
    /** 本用例创建过的登录账号（用于清理 sys_user 与 Redis 失败计数）。 */
    private final List<String> createdAccounts = new ArrayList<>();

    /**
     * 给「空库首次启动」的一次性初始化流程提供一个随机口令。
     *
     * <p>{@link CommunityInitializer}（OS009）在上下文启动时读取
     * {@code INITIAL_ADMIN_PASSWORD}：为了在空库上也能直接跑测试，这里在运行环境未提供该变量时
     * 注入一个进程内随机值。该值随后会被 {@link #resetAdminPassword()} 覆盖，因此不会成为
     * 任何可用凭据，也不会落盘。
     *
     * <p>不用 {@code @DynamicPropertySource}：它从 Spring Framework 5.2.5 才有，
     * 而 Spring Boot 2.2.5.RELEASE 依赖的是 5.2.4.RELEASE。
     */
    public static class InitialAdminPassword implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            String fromEnv = System.getenv("INITIAL_ADMIN_PASSWORD");
            String value = (fromEnv == null || fromEnv.trim().isEmpty())
                    ? "it-" + UUID.randomUUID() : fromEnv;
            TestPropertyValues.of("INITIAL_ADMIN_PASSWORD=" + value).applyTo(context);
        }
    }

    @BeforeAll
    static void assertCommunityDatabase() {
        String db = System.getenv("MYSQL_DATABASE");
        assertNotNull(db, "缺少环境变量 MYSQL_DATABASE：本测试只允许跑在社区版库上");
        assertTrue(db.startsWith("yunduo_community"),
                "拒绝执行：MYSQL_DATABASE=" + db + " 不是社区版库（要求前缀 yunduo_community）");
    }

    @AfterEach
    protected void cleanup() {
        for (String account : createdAccounts) {
            jdbc.update("DELETE FROM sys_user_role WHERE user_id IN (SELECT user_id FROM sys_user WHERE user_name = ?)", account);
            jdbc.update("DELETE FROM sys_user WHERE user_name = ?", account);
            jdbc.update("DELETE FROM bd_campus_personnel WHERE phone = ?", account);
            jdbc.update("DELETE FROM bd_mec_personnel WHERE phone = ?", account);
            // 登录失败计数会跨用例残留，必须显式清掉，否则后续用例会被「临时锁定」误伤
            redisTemplate.delete("login_fail_count:" + account);
        }
        createdAccounts.clear();
        // C01 会用错误口令冲一次 admin 的失败计数，同样要清掉
        redisTemplate.delete("login_fail_count:" + ADMIN_USER);

        // 逆序回收；按「学员 / 班级」两条主线把所有派生行删干净（含被测接口产生的行）
        for (int i = cleanupRegistry.size() - 1; i >= 0; i--) {
            Object[] entry = cleanupRegistry.get(i);
            jdbc.update(String.valueOf(entry[0]), entry[1]);
        }
        cleanupRegistry.clear();
    }

    // ------------------------------------------------------------------ HTTP

    /** 统一返回结构：HTTP 状态 + body 里的 code / msg / data。 */
    protected static final class ApiResult {
        public final int httpStatus;
        public final Map<String, Object> body;

        ApiResult(int httpStatus, Map<String, Object> body) {
            this.httpStatus = httpStatus;
            this.body = body == null ? Collections.<String, Object>emptyMap() : body;
        }

        public Integer code() {
            Object v = body.get("code");
            return v instanceof Number ? ((Number) v).intValue() : null;
        }

        public String msg() {
            Object v = body.get("msg");
            return v == null ? "" : String.valueOf(v);
        }

        @SuppressWarnings("unchecked")
        public Object data() {
            return body.get("data");
        }

        public String token() {
            Object v = body.get("token");
            return v == null ? null : String.valueOf(v);
        }

        @Override
        public String toString() {
            return "HTTP=" + httpStatus + " code=" + code() + " msg=" + msg();
        }
    }

    protected String url(String path) {
        return "http://127.0.0.1:" + port + path;
    }

    protected ApiResult call(String method, String path, Object body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> resp = rest.exchange(url(path), HttpMethod.resolve(method), entity, Map.class);
        return new ApiResult(resp.getStatusCodeValue(), resp.getBody());
    }

    protected ApiResult get(String path, String token) {
        return call("GET", path, null, token);
    }

    protected ApiResult post(String path, Object body, String token) {
        return call("POST", path, body, token);
    }

    protected ApiResult put(String path, Object body, String token) {
        return call("PUT", path, body, token);
    }

    protected ApiResult delete(String path, String token) {
        return call("DELETE", path, null, token);
    }

    // -------------------------------------------------------------- 登录会话

    protected ApiResult login(String username, String password) {
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        return post("/login", body, null);
    }

    /** 登录并进入种子校区（机构管理员口径：进入校区必须成功）。 */
    protected String loginAndEnterCampus(String username, String password) {
        ApiResult r = login(username, password);
        assertEquals(200, r.code(), "登录失败：" + r);
        String token = r.token();
        assertNotNull(token, "登录成功必须返回 token");
        ApiResult enter = get("/base/access/login?campusId=" + SEED_CAMPUS_ID, token);
        assertEquals(200, enter.code(), "进入校区失败：" + enter);
        return token;
    }

    /**
     * 校区人员（教务主管 / 授课教师）登录。
     *
     * <p>与机构人员不同：校区人员的校区在账号上已经固定，
     * {@code /base/access/login} 是「机构人员选择要进入哪个校区」的入口，
     * 对校区人员返回 {@code code=500 非机构人员操作} 属既有产品行为。
     * 因此这里登录后只「尽力」进入校区，不把该拒绝当作失败；判据落在登录本身。
     */
    protected String loginCampusUser(String username, String password) {
        ApiResult r = login(username, password);
        assertEquals(200, r.code(), "校区人员登录失败：" + r);
        String token = r.token();
        assertNotNull(token, "登录成功必须返回 token");
        get("/base/access/login?campusId=" + SEED_CAMPUS_ID, token);
        return token;
    }

    protected String adminToken() {
        resetAdminPassword();
        return loginAndEnterCampus(ADMIN_USER, ADMIN_PASSWORD);
    }

    /** 把 admin 的口令覆盖成本次运行期随机值（不落盘、不打印）。 */
    protected void resetAdminPassword() {
        String hash = new BCryptPasswordEncoder().encode(ADMIN_PASSWORD);
        int rows = jdbc.update("UPDATE sys_user SET password = ? WHERE user_name = ?", hash, ADMIN_USER);
        assertEquals(1, rows, "admin 账号不存在或未唯一命中，种子可能没执行");
    }

    // ------------------------------------------------------------- 夹具(Fixture)

    /** 一次最小闭环夹具：一位教师 + 一门课程 + 一个班级 + 一名学员 + 一条课时记录。 */
    protected static final class Fixture {
        public String tag;
        public long teacherId;
        public long courseId;
        public long classId;
        public long studentId;
        public long classCourseTimeId;
        public String courseName;
        public String className;
        public String studentName;
    }

    /**
     * 只造「一位教师 + 一门课程」，供需要自己走接口建班级 / 学员的用例使用。
     */
    protected Fixture newCourseWithTeacher(String label) {
        String tag = "os012" + label.toLowerCase() + "-" + UUID.randomUUID().toString().substring(0, 8);
        Fixture f = new Fixture();
        f.tag = tag;
        f.courseName = "OS012课程-" + tag;
        f.teacherId = insertTeacher(tag);
        f.courseId = insertCourse(tag, f.courseName);
        return f;
    }

    /**
     * 造一套「教师 / 课程 / 班级 / 学员 / 课时」夹具。
     *
     * @param label           用例标签，用于拼出可识别的唯一名字（便于人工排查残留）
     * @param totalCourseTime 总课时
     * @param remainingTime   剩余课时
     */
    protected Fixture newFixture(String label, int totalCourseTime, int remainingTime) {
        String tag = "os012" + label.toLowerCase() + "-" + UUID.randomUUID().toString().substring(0, 8);
        Fixture f = new Fixture();
        f.tag = tag;
        f.courseName = "OS012课程-" + tag;
        f.className = "OS012班级-" + tag;
        f.studentName = "OS012学员-" + tag;

        f.teacherId = insertTeacher(tag);
        f.courseId = insertCourse(tag, f.courseName);
        f.classId = insertClass(tag, f.className, f.courseId, f.teacherId);
        f.studentId = insertStudent(tag, f.studentName);
        linkStudentToClass(f.studentId, f.classId);
        f.classCourseTimeId = insertCourseTime(f.studentId, f.courseId, totalCourseTime, remainingTime);
        return f;
    }

    private long insertTeacher(String tag) {
        long id = insert("INSERT INTO bd_teacher (mec_id, campus_id, name, phone, sex, induction_time, "
                        + "taught_num, is_departure, version, is_delete, create_time, create_by) "
                        + "VALUES (?,?,?,?,'0',NOW(),0,0,0,0,NOW(),1)",
                SEED_MEC_ID, SEED_CAMPUS_ID, "OS012教师-" + tag, freshPhone());
        register("DELETE FROM bd_teacher WHERE id = ?", id);
        return id;
    }

    private long insertCourse(String tag, String courseName) {
        long id = insert("INSERT INTO bd_course (mec_id, campus_id, price, paid_price, time_num, course_name, "
                        + "subject, is_shelf, is_experience, introduction, is_delete, create_time, create_by, amount_cny) "
                        + "VALUES (?,?,?,?,?,?,?,1,1,'OS012 测试课程',0,NOW(),1,?)",
                SEED_MEC_ID, SEED_CAMPUS_ID, 0, 0, 20, courseName, "测试科目", 0);
        register("DELETE FROM bd_course WHERE id = ?", id);
        return id;
    }

    private long insertClass(String tag, String className, long courseId, long teacherId) {
        long id = insert("INSERT INTO bd_class (mec_id, campus_id, has_class, have_class, bishop_teacher_id, "
                        + "sum_students, exist_students, class_name, class_course_id, version, is_delete, "
                        + "class_type, class_state, create_time, create_by) "
                        + "VALUES (?,?,0,0,?,30,0,?,?,0,0,1,0,NOW(),1)",
                SEED_MEC_ID, SEED_CAMPUS_ID, teacherId, className, courseId);
        register("DELETE FROM bd_class_record WHERE class_id = ?", id);
        register("DELETE FROM bd_call_record WHERE class_id = ?", id);
        register("DELETE FROM bd_class_time WHERE class_id = ?", id);
        register("DELETE FROM bd_class_change WHERE class_id = ?", id);
        register("DELETE FROM bd_class WHERE id = ?", id);
        return id;
    }

    private long insertStudent(String tag, String studentName) {
        long id = insert("INSERT INTO bd_student (mec_id, campus_id, student_name, students_phone, sex, "
                        + "parents_name, parents_phone, register_time, is_delete, is_ordinary, create_time, create_by) "
                        + "VALUES (?,?,?,?,0,?,?,NOW(),0,0,NOW(),1)",
                SEED_MEC_ID, SEED_CAMPUS_ID, studentName, freshPhone(), "OS012家长-" + tag, freshPhone());
        register("DELETE FROM bd_ask_leave WHERE student_id = ?", id);
        register("DELETE FROM bd_course_time_update WHERE student_id = ?", id);
        register("DELETE FROM bd_course_time WHERE student_id = ?", id);
        register("DELETE FROM bd_course_purchase WHERE student_id = ?", id);
        register("DELETE FROM bd_student_class WHERE student_id = ?", id);
        register("DELETE FROM bd_student WHERE id = ?", id);
        return id;
    }

    protected void linkStudentToClass(long studentId, long classId) {
        jdbc.update("INSERT INTO bd_student_class (mec_id, campus_id, student_id, class_id, have_time, "
                        + "is_delete, create_time, create_by) VALUES (?,?,?,?,NOW(),0,NOW(),1)",
                SEED_MEC_ID, SEED_CAMPUS_ID, studentId, classId);
    }

    /** 往已有班级里再加一名学员（含该课程的课时记录），用于多点名的回滚 / 一致性用例。 */
    protected long addStudentToClass(Fixture f, String label, int total, int remaining) {
        String tag = f.tag + "-" + label;
        long studentId = insertStudent(tag, "OS012学员-" + tag);
        linkStudentToClass(studentId, f.classId);
        insertCourseTime(studentId, f.courseId, total, remaining);
        return studentId;
    }

    /** 取某班级最新一条上课记录 id（没有则返回 null）。 */
    protected Long latestClassRecordId(long classId) {
        List<Long> ids = jdbc.queryForList(
                "SELECT id FROM bd_class_record WHERE class_id = ? AND is_delete = 0 ORDER BY id DESC LIMIT 1",
                Long.class, classId);
        return ids.isEmpty() ? null : ids.get(0);
    }

    /** 取某班级最新一条课消记录 id（没有则返回 null）。 */
    protected Long latestCallRecordId(long classId) {
        List<Long> ids = jdbc.queryForList(
                "SELECT id FROM bd_call_record WHERE class_id = ? AND is_delete = 0 ORDER BY id DESC LIMIT 1",
                Long.class, classId);
        return ids.isEmpty() ? null : ids.get(0);
    }

    protected long insertCourseTime(long studentId, long courseId, int total, int remaining) {
        long id = insert("INSERT INTO bd_course_time (mec_id, campus_id, student_id, course_id, total_number, "
                        + "remaining_number, is_delete, create_time, create_by, version) "
                        + "VALUES (?,?,?,?,?,?,0,NOW(),1,0)",
                SEED_MEC_ID, SEED_CAMPUS_ID, studentId, courseId, total, remaining);
        return id;
    }

    /** 生成一个保留测试号段（199 段）的虚构手机号。 */
    protected static String freshPhone() {
        return "199" + String.format("%08d", ThreadLocalRandom.current().nextInt(100000000));
    }

    /** 按名字在夹具表里找回接口创建的 id（新增接口只返回布尔，不返回主键）。 */
    protected long idByName(String table, String nameColumn, String name) {
        List<Long> ids = jdbc.queryForList(
                "SELECT id FROM " + table + " WHERE " + nameColumn + " = ? ORDER BY id DESC LIMIT 1",
                Long.class, name);
        assertFalse(ids.isEmpty(), "接口应已写入 " + table + "." + nameColumn + " = " + name);
        return ids.get(0);
    }

    /** 登记一条「按 id 删除」的清理动作，供接口创建的实体复用。 */
    protected void track(String deleteSql, long id) {
        cleanupRegistry.add(new Object[]{deleteSql, id});
    }

    /** 把一个直接 SQL 造的诱饵学员（用于跨机构/跨校区越权负向对照）纳入清理。 */
    protected long insertDecoyStudent(long mecId, long campusId, String label) {
        String name = "OS012诱饵-" + label + "-" + UUID.randomUUID().toString().substring(0, 8);
        long id = insertStudentRow(mecId, campusId, name, freshPhone());
        track("DELETE FROM bd_student_class WHERE student_id = ?", id);
        track("DELETE FROM bd_student WHERE id = ?", id);
        return id;
    }

    private long insertStudentRow(long mecId, long campusId, String name, String phone) {
        return insert("INSERT INTO bd_student (mec_id, campus_id, student_name, students_phone, sex, "
                        + "parents_name, parents_phone, register_time, is_delete, is_ordinary, create_time, create_by) "
                        + "VALUES (?,?,?,?,0,'OS012诱饵家长',?,NOW(),0,0,NOW(),1)",
                mecId, campusId, name, phone, phone);
    }

    /**
     * 用随机账号创建一名校区人员（管理员接口），返回登录账号。
     *
     * @param roleId    2=教务主管 3=授课教师（见 002_seed_system.sql）
     * @param isTeacher 是否同时建教师档案（0/1）
     * @param password  该账号的登录口令（运行期随机生成，不落盘）
     */
    protected String createCampusAccount(String label, int roleId, int isTeacher, String password) {
        String account = "1990" + String.format("%07d", ThreadLocalRandom.current().nextInt(10000000));
        Map<String, Object> body = new HashMap<>();
        body.put("name", "OS012账号-" + label);
        body.put("phone", account);
        body.put("contactNumber", account);
        body.put("password", password);
        body.put("roleIds", new int[]{roleId});
        body.put("isTeacher", isTeacher);
        ApiResult r = post("/base/campus_personnel", body, adminToken());
        assertEquals(200, r.code(), "创建校区账号失败：" + r);
        createdAccounts.add(account);
        return account;
    }

    // ------------------------------------------------------------ 业务动作封装

    /**
     * 点名（POST /educenter/curriculum/rollCall）。
     *
     * @param classId     班级 id
     * @param startInWeek 开始时间，格式 yyyy-MM-dd HH:mm
     * @param endInWeek   结束时间，格式 yyyy-MM-dd HH:mm
     * @param entries     studentId → 课消状态
     */
    protected ApiResult rollCall(String token, long classId, String startInWeek, String endInWeek,
                                 Map<Long, Integer> entries) {
        Map<String, Object> classVO = new HashMap<>();
        classVO.put("id", classId);
        List<Map<String, Object>> callRecords = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : entries.entrySet()) {
            Map<String, Object> row = new HashMap<>();
            row.put("studentId", e.getKey());
            row.put("type", e.getValue());
            callRecords.add(row);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("classVO", classVO);
        body.put("vacateStus", new ArrayList<>());
        body.put("callRecordList", callRecords);
        body.put("startInWeek", startInWeek);
        body.put("endInWeek", endInWeek);
        return post("/educenter/curriculum/rollCall", body, token);
    }

    /** 读取某学员某课程的剩余课时；没有记录时返回 null。 */
    protected Integer remainingCourseTime(long studentId, long courseId) {
        List<Integer> list = jdbc.queryForList(
                "SELECT remaining_number FROM bd_course_time WHERE student_id = ? AND course_id = ? AND is_delete = 0",
                Integer.class, studentId, courseId);
        return list.isEmpty() ? null : list.get(0);
    }

    /** 课时流水净值：增加为正、扣减为负，按 bd_course_time_update 复算。 */
    protected long courseTimeLedgerNet(long studentId, long courseId) {
        Long sum = jdbc.queryForObject(
                "SELECT COALESCE(SUM(CASE WHEN change_type = 0 THEN num ELSE -num END), 0) "
                        + "FROM bd_course_time_update WHERE student_id = ? AND course_id = ? AND is_delete = 0",
                Long.class, studentId, courseId);
        return sum == null ? 0L : sum;
    }

    protected long countRows(String sql, Object... args) {
        Long n = jdbc.queryForObject(sql, Long.class, args);
        return n == null ? 0L : n;
    }

    // ------------------------------------------------------------------ 自检

    /**
     * 判据自检（负向对照）：同一条判据必须对「正确样本」判真、对「坏样本」判假。
     *
     * <p>只写「断言通过」是不够的 —— 判据写瞎了（例如恒真）也会通过。
     * 因此每个用例的核心判据都要在这里先做一次正/负样本校准。
     *
     * @param name      判据名称
     * @param judge     判据（输入观察值，输出是否满足）
     * @param goodValue 应当被判定为「满足」的样本
     * @param badValue  应当被判定为「不满足」的样本
     */
    protected <T> void assertJudge(String name, java.util.function.Predicate<T> judge, T goodValue, T badValue) {
        assertTrue(judge.test(goodValue), name + " 判据自检失败：对正确样本判为「不满足」");
        assertFalse(judge.test(badValue), name + " 判据自检失败：对坏样本判为「满足」，说明判据恒真、无效");
    }

    // ---------------------------------------------------------------- 内部工具

    protected long insert(String sql, Object... args) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Date) {
                    ps.setTimestamp(i + 1, new Timestamp(((Date) arg).getTime()));
                } else {
                    ps.setObject(i + 1, arg);
                }
            }
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        assertNotNull(key, "夹具写入未返回自增主键：" + sql);
        return key.longValue();
    }

    private void register(String deleteSql, long id) {
        cleanupRegistry.add(new Object[]{deleteSql, id});
    }
}
