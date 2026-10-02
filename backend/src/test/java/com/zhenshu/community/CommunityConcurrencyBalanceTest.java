package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS022 课时账户并发正确性矩阵（7 项）。
 *
 * <p>与 OS012 的串行用例互补：这里全部使用「两个独立 HTTP 会话 + 闭锁屏障」同时发起请求，
 * 每个请求在服务端各拿独立数据库连接与事务；断言一律落在落库数据（余额 / 流水 / 记录 / 唯一约束），
 * 不以「请求返回了」或「串行重放」冒充并发证据。
 *
 * <p>场景与续作指引 R2 矩阵一一对应：
 * 同课次双点名、余额 1 双课次同扣、双报读、报读×纠错、双重纠错、多人点名中途失败全回滚、并发首次建账户。
 */
@DisplayName("OS022 课时账户并发正确性")
class CommunityConcurrencyBalanceTest extends CommunityTestBase {

    private static final String START_A = "2026-10-22 09:00";
    private static final String END_A = "2026-10-22 10:00";
    private static final String START_B = "2026-10-22 15:00";
    private static final String END_B = "2026-10-22 16:00";

    /** 并发 trials：同一场景重复发起多轮，提高真实重叠概率；任何一轮破坏不变量即失败。 */
    private static final int TRIALS = 5;

    /**
     * 屏障并发执行：所有任务先到栅栏集合，再同时放行。
     * 每个任务在自己的线程里跑（独立的 HTTP 连接 → 服务端独立的数据库连接/事务）。
     */
    private static List<ApiResult> runRace(Supplier<ApiResult>... tasks) throws Exception {
        CountDownLatch barrier = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(tasks.length);
        try {
            List<Future<ApiResult>> futures = new ArrayList<>();
            for (Supplier<ApiResult> task : tasks) {
                futures.add(pool.submit((Callable<ApiResult>) () -> {
                    barrier.await();
                    return task.get();
                }));
            }
            barrier.countDown();
            List<ApiResult> results = new ArrayList<>();
            for (Future<ApiResult> f : futures) {
                results.add(f.get(60, TimeUnit.SECONDS));
            }
            return results;
        } finally {
            pool.shutdownNow();
        }
    }

    /** 教务主管（role 2）登录 token，与 admin 构成「两个不同授权用户」。 */
    private String academicToken(String label) {
        String pwd = "os022-" + java.util.UUID.randomUUID().toString().replace("-", "");
        String account = createCampusAccount(label, 2, 0, pwd);
        return loginCampusUser(account, pwd);
    }

    private static boolean ok(ApiResult r) {
        return r.code() != null && r.code() == 200;
    }

    /** 一致性不变量：剩余课时 == 期初 + 流水净值。 */
    private void assertInvariant(String phase, Fixture f, int baseline, int expectedRemaining) {
        Integer remaining = remainingCourseTime(f.studentId, f.courseId);
        long ledgerNet = courseTimeLedgerNet(f.studentId, f.courseId);
        assertEquals(expectedRemaining, remaining.intValue(),
                phase + "：剩余课时不符（期初 " + baseline + "）");
        assertEquals(expectedRemaining - baseline, ledgerNet,
                phase + "：流水净值与余额变动不一致（不变量被破坏）");
    }

    private long countFlow(long studentId, long courseId, int type) {
        return countRows("SELECT COUNT(*) FROM bd_course_time_update WHERE student_id = ? AND course_id = ? "
                + "AND type = ? AND is_delete = 0", studentId, courseId, type);
    }

    private long countAccounts(long studentId, long courseId) {
        return countRows("SELECT COUNT(*) FROM bd_course_time WHERE student_id = ? AND course_id = ? AND is_delete = 0",
                studentId, courseId);
    }

    private ApiResult enroll(String token, long studentId, long courseId, int timeNum) {
        Map<String, Object> body = new HashMap<>();
        body.put("studentId", studentId);
        body.put("courseId", courseId);
        body.put("timeNum", timeNum);
        body.put("amountCny", 198.00);
        return post("/educenter/student/addCoursePurchase", body, token);
    }

    private ApiResult correct(Long callRecordId, int newType, String token) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", callRecordId);
        body.put("type", newType);
        return put("/educenter/record/saveRollCall", body, token);
    }

    // ------------------------------------------------------------- 场景 1

    @Test
    @DisplayName("并发1 同课次双点名（不同用户）：只产生一份有效上课记录与一次扣减")
    void sameSlotConcurrentRollCallByDifferentUsers() throws Exception {
        String admin = adminToken();
        String academic = academicToken("race1");
        for (int trial = 1; trial <= TRIALS; trial++) {
            Fixture f = newFixture("race1", 20, 20);
            List<ApiResult> rs = runRace(
                    () -> rollCall(admin, f.classId, START_A, END_A,
                            Collections.singletonMap(f.studentId, TYPE_PRESENT)),
                    () -> rollCall(academic, f.classId, START_A, END_A,
                            Collections.singletonMap(f.studentId, TYPE_PRESENT)));
            long success = rs.stream().filter(CommunityConcurrencyBalanceTest::ok).count();
            assertTrue(success >= 1, "trial " + trial + "：至少一次点名应成功：" + rs);
            assertTrue(success <= 1, "trial " + trial + "：同一课次不允许两次点名都生效：" + rs);

            assertEquals(1, countRows("SELECT COUNT(*) FROM bd_class_record WHERE class_id = ? AND is_delete = 0", f.classId),
                    "trial " + trial + "：有效上课记录必须恰好 1 条");
            assertEquals(1, countRows("SELECT COUNT(*) FROM bd_call_record WHERE class_id = ? AND is_delete = 0", f.classId),
                    "trial " + trial + "：课消记录必须恰好 1 条");
            Long classRecordId = latestClassRecordId(f.classId);
            assertNotNull(classRecordId);
            assertEquals(1, jdbc.queryForObject(
                    "SELECT class_time_num FROM bd_call_record WHERE class_record_id = ? AND is_delete = 0",
                    Integer.class, String.valueOf(classRecordId)), "trial " + trial + "：消课课时必须为 1");
            assertInvariant("trial " + trial, f, 20, 19);

        }
    }

    // ------------------------------------------------------------- 场景 2

    @Test
    @DisplayName("并发2 余额1双课次同扣：最多一笔成功，余额不为负")
    void concurrentDeductOnDifferentSlotsWithBalanceOne() throws Exception {
        String admin = adminToken();
        String academic = academicToken("race2");
        for (int trial = 1; trial <= TRIALS; trial++) {
            Fixture f = newFixture("race2", 1, 1);
            List<ApiResult> rs = runRace(
                    () -> rollCall(admin, f.classId, START_A, END_A,
                            Collections.singletonMap(f.studentId, TYPE_PRESENT)),
                    () -> rollCall(academic, f.classId, START_B, END_B,
                            Collections.singletonMap(f.studentId, TYPE_PRESENT)));
            long success = rs.stream().filter(CommunityConcurrencyBalanceTest::ok).count();
            assertEquals(1, success, "trial " + trial + "：余额只有 1 时应恰好一笔扣减成功：" + rs);

            Integer remaining = remainingCourseTime(f.studentId, f.courseId);
            assertNotNull(remaining, "trial " + trial + "：账户行必须存在");
            assertTrue(remaining >= 0, "trial " + trial + "：余额不得为负，实际 " + remaining);
            assertEquals(0, remaining.intValue(), "trial " + trial + "：余额应为 0");
            assertEquals(1, countRows("SELECT COUNT(*) FROM bd_class_record WHERE class_id = ? AND is_delete = 0", f.classId),
                    "trial " + trial + "：失败的点名必须整体回滚（上课记录 0 条增量）");
            assertEquals(-1, courseTimeLedgerNet(f.studentId, f.courseId),
                    "trial " + trial + "：流水净值必须为 -1");

        }
    }

    // ------------------------------------------------------------- 场景 3

    @Test
    @DisplayName("并发3 两笔报读同时加 3 与 5 课时：净增 8，两份流水齐全")
    void concurrentEnrollmentsAddUpToEight() throws Exception {
        String admin = adminToken();
        String academic = academicToken("race3");
        for (int trial = 1; trial <= TRIALS; trial++) {
            Fixture f = newFixture("race3", 10, 10);
            List<ApiResult> rs = runRace(
                    () -> enroll(admin, f.studentId, f.courseId, 3),
                    () -> enroll(academic, f.studentId, f.courseId, 5));
            assertTrue(rs.stream().allMatch(CommunityConcurrencyBalanceTest::ok),
                    "trial " + trial + "：两笔报读都应成功：" + rs);
            assertEquals(2, countRows("SELECT COUNT(*) FROM bd_course_purchase WHERE student_id = ? AND course_id = ? AND is_delete = 0",
                    f.studentId, f.courseId), "trial " + trial + "：应生成 2 条报读记录");
            assertInvariant("trial " + trial, f, 10, 18);
            assertEquals(2, countFlow(f.studentId, f.courseId, 2),
                    "trial " + trial + "：应生成 2 条购买流水（type=2）");

        }
    }

    // ------------------------------------------------------------- 场景 4

    @Test
    @DisplayName("并发4 报读与纠错同时发生：最终余额等于期初加实际生效流水之和")
    void enrollmentConcurrentWithCorrection() throws Exception {
        String admin = adminToken();
        String academic = academicToken("race4");
        for (int trial = 1; trial <= TRIALS; trial++) {
            Fixture f = newFixture("race4", 20, 20);
            assertEquals(200, rollCall(admin, f.classId, START_A, END_A,
                    Collections.singletonMap(f.studentId, TYPE_PRESENT)).code(), "点名应成功");
            Long callRecordId = latestCallRecordId(f.classId);
            assertNotNull(callRecordId, "点名后应存在课消记录");

            List<ApiResult> rs = runRace(
                    () -> enroll(academic, f.studentId, f.courseId, 5),
                    () -> correct(callRecordId, TYPE_LEAVE, admin));
            assertTrue(ok(rs.get(0)), "报读应成功：" + rs.get(0));
            assertTrue(ok(rs.get(1)), "纠错应成功：" + rs.get(1));

            // 期初 20，点名 -1，报读 +5，纠错退 +1 → 25；流水净值 5
            assertInvariant("trial " + trial, f, 20, 25);
            assertEquals(1, countFlow(f.studentId, f.courseId, 6),
                    "trial " + trial + "：纠错（改请假）流水必须恰好 1 条");

        }
    }

    // ------------------------------------------------------------- 场景 5

    @Test
    @DisplayName("并发5 两次相同纠错同时执行：只生效一次，不重复退课")
    void doubleIdenticalCorrectionEffectiveOnce() throws Exception {
        String admin = adminToken();
        String academic = academicToken("race5");
        for (int trial = 1; trial <= TRIALS; trial++) {
            Fixture f = newFixture("race5", 20, 20);
            assertEquals(200, rollCall(admin, f.classId, START_A, END_A,
                    Collections.singletonMap(f.studentId, TYPE_PRESENT)).code(), "点名应成功");
            Long callRecordId = latestCallRecordId(f.classId);
            assertNotNull(callRecordId);

            List<ApiResult> rs = runRace(
                    () -> correct(callRecordId, TYPE_LEAVE, admin),
                    () -> correct(callRecordId, TYPE_LEAVE, academic));
            long success = rs.stream().filter(CommunityConcurrencyBalanceTest::ok).count();
            assertTrue(success >= 1, "trial " + trial + "：至少一次纠错应成功：" + rs);
            assertTrue(success <= 1, "trial " + trial + "：相同纠错不允许生效两次：" + rs);

            // 期初 20，点名 -1，纠错退 +1 → 20；流水净值 0
            assertInvariant("trial " + trial, f, 20, 20);
            assertEquals(1, countFlow(f.studentId, f.courseId, 6),
                    "trial " + trial + "：改请假流水必须恰好 1 条（不允许双重退课）");

        }
    }

    // ------------------------------------------------------------- 场景 6

    @Test
    @DisplayName("并发6 多学员点名中途余额不足：整单回滚，无部分扣减")
    void multiStudentRollCallPartialFailureRollsBackAll() throws Exception {
        String admin = adminToken();
        for (int trial = 1; trial <= TRIALS; trial++) {
            Fixture f = newFixture("race6", 5, 5);
            long poorStudent = addStudentToClass(f, "poor", 1, 0);

            Map<Long, Integer> entries = new LinkedHashMap<>();
            entries.put(f.studentId, TYPE_PRESENT);
            entries.put(poorStudent, TYPE_PRESENT);
            ApiResult result = rollCall(admin, f.classId, START_A, END_A, entries);
            assertTrue(!ok(result), "trial " + trial + "：余额不足的点名必须失败：" + result);

            assertEquals(5, remainingCourseTime(f.studentId, f.courseId).intValue(),
                    "trial " + trial + "：第一名学员课时不得被部分扣减");
            assertEquals(0, courseTimeLedgerNet(f.studentId, f.courseId),
                    "trial " + trial + "：第一名学员不得留下扣减流水");
            assertEquals(0, countRows("SELECT COUNT(*) FROM bd_class_record WHERE class_id = ? AND is_delete = 0", f.classId),
                    "trial " + trial + "：上课记录必须整体回滚");
            assertEquals(0, countRows("SELECT COUNT(*) FROM bd_call_record WHERE class_id = ? AND is_delete = 0", f.classId),
                    "trial " + trial + "：课消记录必须整体回滚");

        }
    }

    // ------------------------------------------------------------- 场景 7

    @Test
    @DisplayName("并发7 同一学员首次报读并发创建账户：不得产生两份有效账户")
    void concurrentFirstEnrollmentCreatesSingleAccount() throws Exception {
        String admin = adminToken();
        String academic = academicToken("race7");
        for (int trial = 1; trial <= TRIALS; trial++) {
            Fixture f = newFixture("race7", 10, 10);
            // 删除夹具课时行，模拟「首次报读，账户尚不存在」
            jdbc.update("DELETE FROM bd_course_time WHERE id = ?", f.classCourseTimeId);

            List<ApiResult> rs = runRace(
                    () -> enroll(admin, f.studentId, f.courseId, 3),
                    () -> enroll(academic, f.studentId, f.courseId, 5));
            assertTrue(rs.stream().allMatch(CommunityConcurrencyBalanceTest::ok),
                    "trial " + trial + "：两笔首次报读都应成功：" + rs);
            assertEquals(1, countAccounts(f.studentId, f.courseId),
                    "trial " + trial + "：并发首次报读不得产生两份有效账户");
            assertInvariant("trial " + trial, f, 0, 8);
            assertEquals(2, countFlow(f.studentId, f.courseId, 2),
                    "trial " + trial + "：两笔购买流水必须齐全");

        }
    }
}
