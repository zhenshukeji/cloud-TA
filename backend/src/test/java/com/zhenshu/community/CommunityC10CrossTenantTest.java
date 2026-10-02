package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS012 最低测试集 ⑩：伪造其他机构 / 校区 ID 不可越权。
 *
 * <p>三条独立判据：
 * <ol>
 *   <li>请求体里伪造 {@code mecId/campusId} 时，落库归属仍必须是当前登录身份（服务端不得采信入参）；</li>
 *   <li>他机构（mec_id=2）的诱饵数据不得出现在本机构列表里（读隔离）；</li>
 *   <li>直接按 id 读他机构/不存在的校区资源应被拒（OS009 9c 同口径）。</li>
 * </ol>
 */
@DisplayName("OS012-10 跨机构 / 跨校区越权负向")
class CommunityC10CrossTenantTest extends CommunityTestBase {

    private static final String START = "2026-10-10 10:00";
    private static final String END = "2026-10-10 11:00";

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("伪造 mecId/campusId 落库仍为本机构；他机构诱饵不外泄；跨机构直读被拒")
    void forgedTenantIdIsIgnored() {
        String admin = adminToken();
        Fixture f = newFixture("c10", 10, 10);

        // ① 伪造归属：请求体与每条课消记录都塞 mecId=99 / campusId=99
        Map<String, Object> classVO = new HashMap<>();
        classVO.put("id", f.classId);
        Map<String, Object> row = new HashMap<>();
        row.put("studentId", f.studentId);
        row.put("type", TYPE_PRESENT);
        row.put("mecId", 99);
        row.put("campusId", 99);
        Map<String, Object> body = new HashMap<>();
        body.put("classVO", classVO);
        body.put("vacateStus", new ArrayList<>());
        body.put("callRecordList", Collections.singletonList(row));
        body.put("startInWeek", START);
        body.put("endInWeek", END);
        body.put("mecId", 99);
        body.put("campusId", 99);

        ApiResult result = post("/educenter/curriculum/rollCall", body, admin);
        assertEquals(200, result.code(), "点名本体应成功（越权体现在归属，而不是失败）：" + result);

        Map<String, Object> record = jdbc.queryForMap(
                "SELECT mec_id, campus_id FROM bd_class_record WHERE class_id = ? AND is_delete = 0", f.classId);
        Map<String, Object> call = jdbc.queryForMap(
                "SELECT mec_id, campus_id FROM bd_call_record WHERE class_id = ? AND is_delete = 0", f.classId);

        Predicate<Long> judgeOwnTenant = id -> id != null && id == SEED_MEC_ID;
        // 判据自检：本机构 1 判真；伪造值 99 判假
        assertJudge("归属判据", judgeOwnTenant, SEED_MEC_ID, 99L);

        assertEquals(SEED_MEC_ID, ((Number) record.get("mec_id")).longValue(), "上课记录归属被伪造入参污染");
        assertEquals(SEED_CAMPUS_ID, ((Number) record.get("campus_id")).longValue(), "上课记录校区被伪造入参污染");
        assertEquals(SEED_MEC_ID, ((Number) call.get("mec_id")).longValue(), "课消记录归属被伪造入参污染");
        assertEquals(SEED_CAMPUS_ID, ((Number) call.get("campus_id")).longValue(), "课消记录校区被伪造入参污染");

        // ② 他机构诱饵：直接 SQL 造一条 mec_id=2 / campus_id=2 的学员，本机构列表不得出现
        String decoyName = "OS012诱饵-c10";
        long decoyId = insertDecoyStudent(2L, 2L, "c10");
        String actualDecoyName = jdbc.queryForObject(
                "SELECT student_name FROM bd_student WHERE id = ?", String.class, decoyId);
        assertTrue(actualDecoyName.contains(decoyName), "诱饵学员应已写入");

        ApiResult list = get("/educenter/student/list?pageNum=1&pageSize=500", admin);
        assertEquals(200, list.code(), "学员列表应可读：" + list);
        List<Map<String, Object>> rows = (List<Map<String, Object>>) list.body.get("rows");
        long leaked = rows == null ? 0 : rows.stream()
                .filter(r -> String.valueOf(r.get("studentName")).equals(actualDecoyName))
                .count();
        assertEquals(0, leaked, "他机构学员不得出现在本机构列表（泄露 " + leaked + " 条）");

        // 正向对照：本机构自己的学员必须能查到，证明上面的 0 不是「列表恒空」
        long ownVisible = rows == null ? 0 : rows.stream()
                .filter(r -> String.valueOf(r.get("studentName")).equals(f.studentName))
                .count();
        assertEquals(1, ownVisible, "本机构学员应出现在列表中（正向对照）");
        assertFalse(rows == null || rows.isEmpty(), "列表不应为空");

        // ③ 跨机构/不存在校区的直读必须被拒
        ApiResult otherCampus = get("/base/campus/2", admin);
        assertTrue(!Integer.valueOf(200).equals(otherCampus.code()),
                "他机构校区直读应被拒：" + otherCampus);
        ApiResult ownCampus = get("/base/campus/" + SEED_CAMPUS_ID, admin);
        assertEquals(200, ownCampus.code(), "本机构校区应可读（正向对照）：" + ownCampus);
    }
}
