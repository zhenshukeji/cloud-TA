package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS012 最低测试集 ⑧：点名事务中途失败时，上课记录、课时和流水一起回滚。
 *
 * <p>构造方式：同一个班两名学员，A 课时充足、B 课时为 0，且 A 排在 B 前面。
 * 处理到 B 时抛业务异常，整笔 {@code rollCall}（{@code @Transactional}）必须回滚 ——
 * 因此 A 的课时、流水、上课记录、课消记录都不能有残留。
 *
 * <p>这条用例是最有价值的一条：它验的是「部分成功」不会落库。
 */
@DisplayName("OS012-08 点名事务中途失败整体回滚")
class CommunityC08RollCallRollbackTest extends CommunityTestBase {

    private static final String START = "2026-10-08 10:00";
    private static final String END = "2026-10-08 11:00";

    @Test
    @DisplayName("A 课时充足 + B 课时为 0 → 整笔点名回滚，A 也不留痕")
    void partialFailureRollsBackEverything() {
        String admin = adminToken();
        Fixture f = newFixture("c08", 5, 5);
        long studentB = addStudentToClass(f, "b", 0, 0);

        // 顺序敏感：A 在前，B 在后 —— 必须让「已成功的 A」也回滚
        Map<Long, Integer> entries = new LinkedHashMap<>();
        entries.put(f.studentId, TYPE_PRESENT);
        entries.put(studentB, TYPE_PRESENT);

        ApiResult result = rollCall(admin, f.classId, START, END, entries);
        assertTrue(!Integer.valueOf(200).equals(result.code()),
                "存在课时不足的学员时，整笔点名应失败：" + result);

        int remainingA = remainingCourseTime(f.studentId, f.courseId);
        int remainingB = remainingCourseTime(studentB, f.courseId);
        long ledgerA = courseTimeLedgerNet(f.studentId, f.courseId);
        long ledgerB = courseTimeLedgerNet(studentB, f.courseId);
        long classRecords = countRows("SELECT COUNT(*) FROM bd_class_record WHERE class_id = ?", f.classId);
        long callRecords = countRows("SELECT COUNT(*) FROM bd_call_record WHERE class_id = ?", f.classId);

        Predicate<int[]> judgeAllOrNothing = v -> v[0] == 5 && v[1] == 0 && v[2] == 0;
        // 判据自检：正确样本（A 未被扣、上课记录 0、课消记录 0）判真；坏样本（A 已扣）判假
        assertJudge("整体回滚判据", judgeAllOrNothing,
                new int[]{5, 0, 0}, new int[]{4, 1, 2});

        assertEquals(5, remainingA, "A 的课时必须回滚到点名前的 5");
        assertEquals(0, remainingB, "B 的课时应保持 0，不得为负");
        assertEquals(0, ledgerA, "A 不应留下扣减流水");
        assertEquals(0, ledgerB, "B 不应留下任何流水");
        assertEquals(0, classRecords, "失败点名不应留下上课记录");
        assertEquals(0, callRecords, "失败点名不应留下课消记录");
        assertTrue(judgeAllOrNothing.test(new int[]{5, 0, 0}), "判据应判真");
    }
}
