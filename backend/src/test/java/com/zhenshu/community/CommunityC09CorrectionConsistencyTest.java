package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS012 最低测试集 ⑨：纠错后课时和明细一致。
 *
 * <p>点名后把状态改成「请假」应退还课时，再改回「到课」应重新扣课；
 * 每一步都必须满足同一条不变量：<b>剩余课时 == 期初 + 课时流水净值</b>。
 * 这条不变量就是「纠错之后账还对得上」的形式化表达。
 */
@DisplayName("OS012-09 点名纠错后课时与明细一致")
class CommunityC09CorrectionConsistencyTest extends CommunityTestBase {

    private static final int BASELINE = 20;
    private static final String START = "2026-10-09 10:00";
    private static final String END = "2026-10-09 11:00";

    @Test
    @DisplayName("到课→请假（退课）→回到课（再扣），每步都满足「课时 == 期初 + 流水净值」")
    void correctionKeepsLedgerConsistent() {
        String admin = adminToken();
        Fixture f = newFixture("c09", BASELINE, BASELINE);

        assertEquals(200, rollCall(admin, f.classId, START, END,
                Collections.singletonMap(f.studentId, TYPE_PRESENT)).code(), "点名应成功");
        assertConsistent(f, "点名到课");

        Long callRecordId = latestCallRecordId(f.classId);
        assertNotNull(callRecordId, "点名后应存在课消记录");

        // 纠错①：到课 → 请假（应退还 1 课时）
        Map<String, Object> toLeave = new HashMap<>();
        toLeave.put("id", callRecordId);
        toLeave.put("type", TYPE_LEAVE);
        ApiResult leave = put("/educenter/record/saveRollCall", toLeave, admin);
        assertEquals(200, leave.code(), "改为请假应成功：" + leave);
        assertConsistent(f, "改请假");
        assertEquals(BASELINE, remainingCourseTime(f.studentId, f.courseId).intValue(), "请假应退还课时");
        assertEquals(0, courseTimeLedgerNet(f.studentId, f.courseId), "流水净值应回到 0");
        assertEquals(0, jdbc.queryForObject(
                "SELECT class_time_num FROM bd_call_record WHERE id = ?", Integer.class, callRecordId),
                "请假记录的消课课时应为 0");

        // 负向对照：把状态改成与当前相同，应被拒绝（否则「纠错」会重复退/扣课时）
        ApiResult sameType = put("/educenter/record/saveRollCall", toLeave, admin);
        assertTrue(!Integer.valueOf(200).equals(sameType.code()),
                "状态相同时的修改应被拒绝：" + sameType);
        assertConsistent(f, "同状态重复提交被拒后");

        // 纠错②：请假 → 到课（应重新扣 1 课时）
        Map<String, Object> toPresent = new HashMap<>();
        toPresent.put("id", callRecordId);
        toPresent.put("type", TYPE_PRESENT);
        ApiResult present = put("/educenter/record/saveRollCall", toPresent, admin);
        assertEquals(200, present.code(), "改回到课应成功：" + present);
        assertConsistent(f, "改回到课");

        assertEquals(BASELINE - 1, remainingCourseTime(f.studentId, f.courseId).intValue(),
                "改回到课后应重新扣掉 1 课时");
        assertEquals(-1, courseTimeLedgerNet(f.studentId, f.courseId), "流水净值应回到 -1");
        assertEquals(1, jdbc.queryForObject(
                "SELECT class_time_num FROM bd_call_record WHERE id = ?", Integer.class, callRecordId),
                "到课记录的消课课时应为 1");
        // 纠错不产生重复的课消记录
        assertEquals(1, countRows("SELECT COUNT(*) FROM bd_call_record WHERE class_id = ? AND is_delete = 0", f.classId),
                "纠错不应产生重复课消记录");
        // 实到人数随状态同步
        assertEquals(1, jdbc.queryForObject(
                "SELECT real_number FROM bd_class_record WHERE class_id = ? AND is_delete = 0", Integer.class, f.classId),
                "纠错后实到人数应为 1");
    }

    /** 不变量：剩余课时 == 期初 + 流水净值。 */
    private void assertConsistent(Fixture f, String stage) {
        int remaining = remainingCourseTime(f.studentId, f.courseId);
        long net = courseTimeLedgerNet(f.studentId, f.courseId);
        Predicate<int[]> judgeBalance = v -> v[0] == BASELINE + v[1];
        assertJudge("课时-流水一致性判据（" + stage + "）", judgeBalance,
                new int[]{19, -1}, new int[]{20, -1});
        assertEquals(BASELINE + net, remaining,
                "[" + stage + "] 剩余课时与课时流水净值不一致：remaining=" + remaining + " net=" + net);
    }
}
