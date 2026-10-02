package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS012 最低测试集 ⑫：统计数据与明细一致。
 *
 * <p>做法：造一个「1 到课 / 1 请假 / 1 未到」的班级，然后
 * <ol>
 *   <li>用班级明细报表（{@code /statistics/class/list}）取回四类人数，
 *       与按 {@code bd_call_record} 明细复算的结果逐项对比；</li>
 *   <li>用学员课时统计（{@code /statistics/classTime/detail}）取回课时，
 *       与 {@code bd_course_time} 落库值对比；</li>
 *   <li>再做一次纠错（未到 → 迟到），确认统计随明细同步变化 ——
 *       这一条把「统计是实时算的、不是缓存/快照」也一并验了。</li>
 * </ol>
 *
 * <p>说明：本用例的数据集是测试自造的（OS013 的 {@code 003_demo_data.sql} 尚未落地）；
 * 待演示数据交付后，同一套判据可直接套用到演示数据集上（任务书第 12 项口径一致）。
 */
@DisplayName("OS012-12 统计与明细一致")
class CommunityC12StatisticsConsistencyTest extends CommunityTestBase {

    private static final String START = "2026-10-12 10:00";
    private static final String END = "2026-10-12 11:00";

    @Test
    @DisplayName("班级明细报表 & 学员课时统计 == 按明细表复算")
    @SuppressWarnings("unchecked")
    void statisticsMatchDetails() {
        String admin = adminToken();
        Fixture f = newFixture("c12", 20, 20);
        long studentLeave = addStudentToClass(f, "leave", 20, 20);
        long studentAbsent = addStudentToClass(f, "absent", 20, 20);

        Map<Long, Integer> entries = new LinkedHashMap<>();
        entries.put(f.studentId, TYPE_PRESENT);      // 到课
        entries.put(studentLeave, TYPE_LEAVE);       // 请假（不扣课时）
        entries.put(studentAbsent, TYPE_ABSENT);     // 未到（扣课时）
        assertEquals(200, rollCall(admin, f.classId, START, END, entries).code(), "点名应成功");

        // ---- ① 班级明细报表 vs 明细复算
        ApiResult classReport = get("/statistics/class/list?pageNum=1&pageSize=500", admin);
        assertEquals(200, classReport.code(), "班级明细报表应可读：" + classReport);
        Map<String, Object> row = findRow((List<Map<String, Object>>) classReport.body.get("rows"),
                "className", f.className);
        assertNotNull(row, "班级明细报表应包含本次造的班级：" + f.className);

        assertEquals(countCallRecords(f.classId, 0), intOf(row, "actuallyNum", "actually_num"), "实到人数不一致");
        assertEquals(countCallRecords(f.classId, 1), intOf(row, "lateNum", "late_num"), "迟到人数不一致");
        assertEquals(countCallRecords(f.classId, 2), intOf(row, "leaveNum", "leave_num"), "请假人数不一致");
        assertEquals(countCallRecords(f.classId, 3), intOf(row, "notArrived", "not_arrived"), "未到人数不一致");

        Predicate<long[]> judgeReportEqualsDetail = v -> v[0] == v[1];
        // 判据自检：一致样本判真；不一致样本判假
        assertJudge("统计=明细判据", judgeReportEqualsDetail, new long[]{3, 3}, new long[]{3, 2});

        // ---- ② 学员课时统计 vs 落库课时
        ApiResult detail = get("/statistics/classTime/detail?studentId=" + f.studentId, admin);
        assertEquals(200, detail.code(), "学员课时统计应可读：" + detail);
        Map<String, Object> courseRow = findRow((List<Map<String, Object>>) detail.data(), "courseName", f.courseName);
        assertNotNull(courseRow, "课时统计应包含本次造的课程：" + f.courseName);
        assertEquals(remainingCourseTime(f.studentId, f.courseId).intValue(),
                intOf(courseRow, "remainingNumber", "remaining_number"), "统计的剩余课时与落库值不一致");
        assertEquals(20, intOf(courseRow, "totalNumber", "total_number"), "统计的总课时与落库值不一致");

        // ---- ③ 纠错后统计随明细同步（证明统计不是缓存的旧值）
        Long absentCallId = jdbc.queryForObject(
                "SELECT id FROM bd_call_record WHERE class_id = ? AND student_id = ? AND is_delete = 0",
                Long.class, f.classId, studentAbsent);
        assertNotNull(absentCallId, "应存在未到学员的课消记录");
        Map<String, Object> toLate = new java.util.HashMap<>();
        toLate.put("id", absentCallId);
        toLate.put("type", 1);
        assertEquals(200, put("/educenter/record/saveRollCall", toLate, admin).code(), "改为迟到应成功");

        ApiResult afterFix = get("/statistics/class/list?pageNum=1&pageSize=500", admin);
        Map<String, Object> fixedRow = findRow((List<Map<String, Object>>) afterFix.body.get("rows"),
                "className", f.className);
        assertNotNull(fixedRow, "纠错后班级明细报表应仍可读");
        assertEquals(countCallRecords(f.classId, 3), intOf(fixedRow, "notArrived", "not_arrived"),
                "纠错后未到人数应同步归零");
        assertEquals(countCallRecords(f.classId, 1), intOf(fixedRow, "lateNum", "late_num"),
                "纠错后迟到人数应同步为 1");
        assertTrue(countCallRecords(f.classId, 1) == 1, "明细侧口径应同步");
    }

    private long countCallRecords(long classId, int type) {
        return countRows("SELECT COUNT(*) FROM bd_call_record WHERE class_id = ? AND type = ? AND is_delete = 0",
                classId, type);
    }

    private Map<String, Object> findRow(List<Map<String, Object>> rows, String key, String value) {
        if (rows == null) {
            return null;
        }
        for (Map<String, Object> row : rows) {
            Object actual = row.containsKey(key) ? row.get(key) : row.get(toSnake(key));
            if (value.equals(String.valueOf(actual))) {
                return row;
            }
        }
        return null;
    }

    /** 兼容驼峰 / 下划线两种列名映射口径。 */
    private int intOf(Map<String, Object> row, String camel, String snake) {
        Object v = row.containsKey(camel) ? row.get(camel) : row.get(snake);
        assertNotNull(v, "报表缺少字段：" + camel + " / " + snake + "，实际字段=" + row.keySet());
        return ((Number) v).intValue();
    }

    private String toSnake(String camel) {
        return camel.replaceAll("([A-Z])", "_$1").toLowerCase();
    }
}
