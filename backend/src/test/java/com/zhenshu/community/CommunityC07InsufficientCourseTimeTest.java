package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS012 最低测试集 ⑦：剩余课时不足时不扣成负数。
 *
 * <p>关注两件事：
 * <ol>
 *   <li>课时为 0 时点名必须被拒（而不是扣成 -1）；</li>
 *   <li>被拒时整笔点名不留痕（上课记录 / 课消记录 / 流水都不应存在）。</li>
 * </ol>
 */
@DisplayName("OS012-07 课时不足不扣成负数")
class CommunityC07InsufficientCourseTimeTest extends CommunityTestBase {

    private static final String START = "2026-10-07 10:00";
    private static final String END = "2026-10-07 11:00";
    private static final String START2 = "2026-10-07 14:00";
    private static final String END2 = "2026-10-07 15:00";

    @Test
    @DisplayName("剩余 0 课时点名被拒且不留痕；剩余 1 课时恰好扣到 0")
    void insufficientCourseTimeNeverGoesNegative() {
        String admin = adminToken();

        // ① 剩余 0：点名必须被拒
        Fixture zero = newFixture("c07a", 0, 0);
        ApiResult denied = rollCall(admin, zero.classId, START, END,
                Collections.singletonMap(zero.studentId, TYPE_PRESENT));
        assertTrue(!Integer.valueOf(200).equals(denied.code()), "剩余 0 课时点名应被拒：" + denied);

        Integer zeroRemaining = remainingCourseTime(zero.studentId, zero.courseId);
        Predicate<Integer> judgeNotNegative = r -> r != null && r >= 0;
        // 判据自检：0 判真，-1 判假
        assertJudge("不为负判据", judgeNotNegative, 0, -1);

        assertEquals(0, zeroRemaining.intValue(), "课时不足时不得扣成负数");
        assertTrue(judgeNotNegative.test(zeroRemaining), "剩余课时应 >= 0");
        assertEquals(0, courseTimeLedgerNet(zero.studentId, zero.courseId), "被拒点名不应留下扣减流水");
        assertEquals(0, countRows("SELECT COUNT(*) FROM bd_call_record WHERE class_id = ?", zero.classId),
                "被拒点名不应留下课消记录");
        assertEquals(0, countRows("SELECT COUNT(*) FROM bd_class_record WHERE class_id = ?", zero.classId),
                "被拒点名不应留下上课记录");

        // ② 剩余 1：恰好扣到 0（边界不越界）
        Fixture one = newFixture("c07b", 1, 1);
        ApiResult ok = rollCall(admin, one.classId, START2, END2,
                Collections.singletonMap(one.studentId, TYPE_PRESENT));
        assertEquals(200, ok.code(), "剩余 1 课时时应可正常点名：" + ok);
        assertEquals(0, remainingCourseTime(one.studentId, one.courseId).intValue(),
                "边界：剩余 1 课时点名后应恰好为 0");

        // ③ 已扣到 0 后再点名，必须被拒（负向对照：说明 ② 的成功不是「无限可扣」）
        ApiResult afterDrained = rollCall(admin, one.classId, "2026-10-07 16:00", "2026-10-07 17:00",
                Collections.singletonMap(one.studentId, TYPE_PRESENT));
        assertTrue(!Integer.valueOf(200).equals(afterDrained.code()),
                "课时耗尽后继续点名应被拒：" + afterDrained);
        assertEquals(0, remainingCourseTime(one.studentId, one.courseId).intValue(), "仍不得为负");
    }
}
