package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * OS012 最低测试集 ⑤：点名成功扣一次课。
 *
 * <p>一次点名必须在四张表上同时留下痕迹，且数值自洽：
 * 上课记录 1 条（实到 1）、课消记录 1 条、课时 -1、课时流水 1 条（扣减方向）。
 */
@DisplayName("OS012-05 点名成功扣一次课")
class CommunityC05RollCallDeductTest extends CommunityTestBase {

    private static final String START = "2026-10-05 10:00";
    private static final String END = "2026-10-05 11:00";

    @Test
    @DisplayName("点名 1 名到课学员：课时 20→19，流水 -1，上课/课消记录各 1 条")
    void rollCallDeductsExactlyOnce() {
        String admin = adminToken();
        Fixture f = newFixture("c05", 20, 20);

        ApiResult result = rollCall(admin, f.classId, START, END,
                Collections.singletonMap(f.studentId, TYPE_PRESENT));
        assertEquals(200, result.code(), "点名应成功：" + result);

        Integer remaining = remainingCourseTime(f.studentId, f.courseId);
        long ledgerNet = courseTimeLedgerNet(f.studentId, f.courseId);
        long callRecords = countRows("SELECT COUNT(*) FROM bd_call_record WHERE class_id = ? AND is_delete = 0", f.classId);
        long classRecords = countRows("SELECT COUNT(*) FROM bd_class_record WHERE class_id = ? AND is_delete = 0", f.classId);
        Long classRecordId = latestClassRecordId(f.classId);
        assertNotNull(classRecordId, "点名必须生成上课记录");

        Predicate<int[]> judgeDeductOnce = v -> v[0] == 19 && v[1] == -1;
        // 判据自检：正确样本 (19, -1) 判真；坏样本 (20, 0)（根本没扣）判假
        assertJudge("扣一次课判据", judgeDeductOnce, new int[]{19, -1}, new int[]{20, 0});

        assertEquals(19, remaining.intValue(), "剩余课时应为 20 - 1");
        assertEquals(-1, ledgerNet, "课时流水净值应为 -1");
        assertEquals(1, callRecords, "应生成 1 条课消记录");
        assertEquals(1, classRecords, "应生成 1 条上课记录");
        assertEquals(1, jdbc.queryForObject(
                "SELECT real_number FROM bd_class_record WHERE id = ?", Integer.class, classRecordId),
                "实到人数应为 1");
        assertEquals(1, jdbc.queryForObject(
                "SELECT class_time_num FROM bd_call_record WHERE class_record_id = ?", Integer.class, String.valueOf(classRecordId)),
                "课消记录的消课课时应为 1");
        assertEquals(TYPE_PRESENT, jdbc.queryForObject(
                "SELECT type FROM bd_call_record WHERE class_record_id = ?", Integer.class, String.valueOf(classRecordId)),
                "课消记录状态应为到课");
    }
}
