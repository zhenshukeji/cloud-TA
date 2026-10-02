package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * OS012 最低测试集 ④：报读增加课时，且课时流水的合计与剩余课时一致。
 *
 * <p>判据不是「接口返回 200」，而是：{@code 剩余课时 == 期初 + 流水净值}，
 * 且购买记录（bd_course_purchase）与流水（bd_course_time_update）同时存在、数值互相吻合。
 */
@DisplayName("OS012-04 报读增加课时与流水一致")
class CommunityC04EnrollmentLedgerTest extends CommunityTestBase {

    private static final int ENROLL_TIME_NUM = 10;
    private static final double ENROLL_AMOUNT_CNY = 199.00;

    @Test
    @DisplayName("线下报读登记：课时 +10、流水 +10、购买记录一致")
    void enrollmentKeepsLedgerConsistent() {
        String admin = adminToken();
        Fixture f = newFixture("c04", 0, 0);
        int baseline = 0;

        Map<String, Object> body = new HashMap<>();
        body.put("studentId", f.studentId);
        body.put("courseId", f.courseId);
        body.put("timeNum", ENROLL_TIME_NUM);
        body.put("amountCny", new BigDecimal("199.00"));
        ApiResult enrolled = post("/educenter/student/addCoursePurchase", body, admin);
        assertEquals(200, enrolled.code(), "报读（新增购买记录）应成功：" + enrolled);

        Integer remaining = remainingCourseTime(f.studentId, f.courseId);
        long ledgerNet = courseTimeLedgerNet(f.studentId, f.courseId);
        Integer total = jdbc.queryForObject(
                "SELECT total_number FROM bd_course_time WHERE id = ?", Integer.class, f.classCourseTimeId);

        Predicate<int[]> judgeConsistent = v -> v[0] == baseline + v[1] && v[0] == ENROLL_TIME_NUM;
        // 判据自检：正确样本 (剩余=10, 流水=10) 判真；坏样本 (剩余=0, 流水=10) 判假
        assertJudge("报读一致性判据", judgeConsistent, new int[]{10, 10}, new int[]{0, 10});

        assertEquals(ENROLL_TIME_NUM, remaining, "剩余课时应为期初 + 报读课时");
        assertEquals(ENROLL_TIME_NUM, total, "总课时应同步增加");
        assertEquals(baseline + ledgerNet, remaining.intValue(), "剩余课时与课时流水净值不一致");
        assertEquals(ENROLL_TIME_NUM, ledgerNet, "课时流水净值应等于报读课时");

        // 购买记录本身：课时数、金额、状态、未删除
        Map<String, Object> purchase = jdbc.queryForMap(
                "SELECT time_num, amount_cny, status, is_delete FROM bd_course_purchase "
                        + "WHERE student_id = ? AND course_id = ? AND is_delete = 0", f.studentId, f.courseId);
        assertEquals(ENROLL_TIME_NUM, ((Number) purchase.get("time_num")).intValue(), "购买记录课时数不符");
        assertEquals(ENROLL_AMOUNT_CNY,
                ((BigDecimal) purchase.get("amount_cny")).doubleValue(), 0.001, "购买记录金额不符");
        assertEquals(1, ((Number) purchase.get("status")).intValue(), "购买记录状态应为有效");

        // 流水类型应为「课时购买记录」且为增加方向（change_type=0）
        Map<String, Object> flow = jdbc.queryForMap(
                "SELECT num, change_type, type FROM bd_course_time_update "
                        + "WHERE student_id = ? AND course_id = ? AND is_delete = 0 ORDER BY id DESC LIMIT 1",
                f.studentId, f.courseId);
        assertEquals(ENROLL_TIME_NUM, ((Number) flow.get("num")).intValue(), "流水数量不符");
        assertEquals(0, ((Number) flow.get("change_type")).intValue(), "报读流水应为增加方向 change_type=0");
        assertEquals(2, ((Number) flow.get("type")).intValue(), "报读流水类型应为 2（课时购买记录）");

        // 二次报读：课时与流水继续同步累加（不是覆盖）
        body.put("timeNum", 5);
        assertEquals(200, post("/educenter/student/addCoursePurchase", body, admin).code());
        assertEquals(ENROLL_TIME_NUM + 5, remainingCourseTime(f.studentId, f.courseId).intValue(),
                "二次报读后剩余课时应累加");
        assertEquals(ENROLL_TIME_NUM + 5, courseTimeLedgerNet(f.studentId, f.courseId),
                "二次报读后流水净值应累加");
    }
}
