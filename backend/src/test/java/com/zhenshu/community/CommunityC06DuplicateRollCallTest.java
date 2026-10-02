package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS012 最低测试集 ⑥：同一班级同一时段重复点名不重复扣课。
 *
 * <p>两层防线都要验：
 * <ol>
 *   <li>业务层 —— {@code checkWhetherTheRollCall} 抛「不可重复点名」；</li>
 *   <li>结构层 —— {@code bd_class_record} 上的唯一键 {@code uk_bd_class_record_active_slot}
 *       兜住并发/绕过业务层的重复写入。</li>
 * </ol>
 */
@DisplayName("OS012-06 同时段重复点名不重复扣课")
class CommunityC06DuplicateRollCallTest extends CommunityTestBase {

    private static final String START = "2026-10-06 10:00";
    private static final String END = "2026-10-06 11:00";

    @Test
    @DisplayName("同班级同时段第二次点名被拒，课时仍只扣一次")
    void duplicateRollCallDoesNotDeductTwice() {
        String admin = adminToken();
        Fixture f = newFixture("c06", 20, 20);

        ApiResult first = rollCall(admin, f.classId, START, END,
                Collections.singletonMap(f.studentId, TYPE_PRESENT));
        assertEquals(200, first.code(), "首次点名应成功：" + first);
        int afterFirst = remainingCourseTime(f.studentId, f.courseId);

        ApiResult second = rollCall(admin, f.classId, START, END,
                Collections.singletonMap(f.studentId, TYPE_PRESENT));
        assertTrue(!Integer.valueOf(200).equals(second.code()),
                "同时段重复点名应被拒绝，实际：" + second);
        assertTrue(second.msg() != null && second.msg().contains("不可重复点名"),
                "拒绝原因应为「不可重复点名」，实际：" + second.msg());

        int afterSecond = remainingCourseTime(f.studentId, f.courseId);
        long ledgerNet = courseTimeLedgerNet(f.studentId, f.courseId);
        long callRecords = countRows("SELECT COUNT(*) FROM bd_call_record WHERE class_id = ? AND is_delete = 0", f.classId);
        long classRecords = countRows("SELECT COUNT(*) FROM bd_class_record WHERE class_id = ? AND is_delete = 0", f.classId);

        Predicate<int[]> judgeSingleDeduction = v -> v[0] == afterFirst && v[1] == 1;
        // 判据自检：正确样本 (课时不变, 记录 1 条) 判真；坏样本 (再扣一次 → 记录 2 条) 判假
        assertJudge("不重复扣课判据", judgeSingleDeduction,
                new int[]{afterFirst, 1}, new int[]{afterFirst - 1, 2});

        assertEquals(19, afterFirst, "首次点名后应为 19");
        assertEquals(19, afterSecond, "重复点名被拒后课时不得再变");
        assertEquals(-1, ledgerNet, "课时流水只应有 1 笔扣减");
        assertEquals(1, callRecords, "课消记录不应重复生成");
        assertEquals(1, classRecords, "上课记录不应重复生成");
        assertTrue(judgeSingleDeduction.test(new int[]{afterFirst, 1}), "判据应判真");

        // 结构层防线确实存在
        long uniqueIndex = countRows(
                "SELECT COUNT(DISTINCT INDEX_NAME) FROM information_schema.STATISTICS "
                        + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'bd_class_record' "
                        + "AND INDEX_NAME = 'uk_bd_class_record_active_slot' AND NON_UNIQUE = 0");
        assertEquals(1, uniqueIndex, "bd_class_record 应有唯一键 uk_bd_class_record_active_slot");
    }
}
