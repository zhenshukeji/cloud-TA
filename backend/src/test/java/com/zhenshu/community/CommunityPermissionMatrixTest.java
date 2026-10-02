package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS023 权限矩阵与服务端数据归属。
 *
 * <p>两个维度：
 * <ol>
 *   <li><b>角色 × 操作面</b>：机构管理员 / 教务主管 / 授课教师对「列表、详情、写入、点名、纠错」
 *       的正反例。预期全部<b>由种子授权推导</b>（sys_role_menu 实际行），不自行扩大或收缩；
 *       管理员独占的 system/monitor 面对低权限角色必须 code=403，
 *       同归属正向请求必须成功（避免「全部拒绝」的假安全）。</li>
 *   <li><b>归属诱饵</b>：mec_id=2 / campus_id=2 的诱饵机构数据——本机构请求
 *       伪造对象 ID 指向诱饵时，不得泄露内容、不得改变数据、不得留下流水；
 *       请求体伪造 mecId/campusId 不得改变落库归属。</li>
 * </ol>
 *
 * <p>判据全部读 body.code（本项目权限拒绝 = HTTP 200 + code=403）或落库内容，
 * 不以 HTTP 状态码、隐藏菜单或前端行为代替服务端鉴权。
 */
@DisplayName("OS023 权限矩阵与服务端数据归属")
class CommunityPermissionMatrixTest extends CommunityTestBase {

    private static final String START = "2026-10-23 09:00";
    private static final String END = "2026-10-23 10:00";

    private String academicToken(String label) {
        String pwd = "os023-" + java.util.UUID.randomUUID().toString().replace("-", "");
        return loginCampusUser(createCampusAccount(label, 2, 0, pwd), pwd);
    }

    private String teacherToken(String label) {
        String pwd = "os023-" + java.util.UUID.randomUUID().toString().replace("-", "");
        return loginCampusUser(createCampusAccount(label, 3, 1, pwd), pwd);
    }

    private void assertAllowed(ApiResult r, String what) {
        assertEquals(200, r.code(), "应被允许：" + what + " → " + r);
    }

    private void assertDenied(ApiResult r, String what) {
        assertEquals(200, r.httpStatus, "本项目权限拒绝是 HTTP 200 + code=403：" + what);
        assertEquals(403, r.code(), "应被拒绝（code=403）：" + what + " → " + r);
    }

    // ------------------------------------------------------- 矩阵一：角色×操作面

    @Test
    @DisplayName("矩阵A 三角色 × 列表/详情/写入/点名/纠错：按种子授权放行或 403")
    void roleMatrixOnCoreOperations() {
        String admin = adminToken();
        String academic = academicToken("mxe");
        String teacher = teacherToken("mxt");

        // 种子授权事实（判据来源，不凭记忆）：三角色都有 educenter:*:list；
        // system:user:list 仅管理员；statistics:class:list 管理员+教务
        assertEquals(1, countRows("SELECT COUNT(*) FROM sys_role_menu rm JOIN sys_menu m ON m.menu_id = rm.menu_id "
                + "JOIN sys_role r ON r.role_id = rm.role_id WHERE r.role_key = 'CAMPUS_LECTURER_1' "
                + "AND m.perms = 'educenter:student:list'"), "种子应给教师授予 educenter:student:list");
        assertEquals(0, countRows("SELECT COUNT(*) FROM sys_role_menu rm JOIN sys_menu m ON m.menu_id = rm.menu_id "
                + "JOIN sys_role r ON r.role_id = rm.role_id WHERE r.role_key = 'CAMPUS_LECTURER_1' "
                + "AND m.perms = 'system:user:list'"), "种子不应给教师授予 system:user:list");

        // ---- 列表：三角色都放行 ----
        for (String token : new String[]{admin, academic, teacher}) {
            assertAllowed(get("/educenter/student/list?pageNum=1&pageSize=1", token), "学员列表");
            assertAllowed(get("/educenter/course/list?pageNum=1&pageSize=1", token), "课程列表");
            assertAllowed(get("/educenter/class/list?pageNum=1&pageSize=1", token), "班级列表");
            assertAllowed(get("/educenter/teacher/list?pageNum=1&pageSize=1", token), "教师列表");
            assertAllowed(get("/educenter/record/list?pageNum=1&pageSize=1", token), "上课记录列表");
        }

        // ---- 管理员面：教师/教务必须 403，管理员 200（正向对照）----
        assertDenied(get("/system/user/list?pageNum=1&pageSize=1", teacher), "教师→system:user:list");
        assertDenied(get("/system/user/list?pageNum=1&pageSize=1", academic), "教务→system:user:list");
        assertAllowed(get("/system/user/list?pageNum=1&pageSize=1", admin), "管理员→system:user:list");
        assertDenied(get("/system/role/list?pageNum=1&pageSize=1", teacher), "教师→system:role:list");
        assertAllowed(get("/system/role/list?pageNum=1&pageSize=1", admin), "管理员→system:role:list");
        assertDenied(get("/monitor/operlog/list?pageNum=1&pageSize=1", teacher), "教师→monitor:operlog:list");
        assertAllowed(get("/monitor/operlog/list?pageNum=1&pageSize=1", admin), "管理员→monitor:operlog:list");

        // ---- 统计面：管理员+教务放行，教师 403 ----
        assertAllowed(get("/statistics/class/list?pageNum=1&pageSize=1", academic), "教务→statistics:class:list");
        assertDenied(get("/statistics/class/list?pageNum=1&pageSize=1", teacher), "教师→statistics:class:list");
        assertAllowed(get("/statistics/class/list?pageNum=1&pageSize=1", admin), "管理员→statistics:class:list");

        // ---- 写入/点名/纠错：三角色同归属正向都必须成功 ----
        for (String role : new String[]{"admin", "academic", "teacher"}) {
            String token = role.equals("admin") ? admin : role.equals("academic") ? academic : teacher;
            Fixture f = newFixture("w" + role.substring(0, 1), 10, 10);
            // 写入（新增学员）
            Map<String, Object> stu = new HashMap<>();
            stu.put("studentName", "OS023学员-" + role + "-" + f.tag);
            stu.put("studentsPhone", freshPhone());
            stu.put("parentsName", "OS023家长");
            stu.put("parentsPhone", freshPhone());
            assertAllowed(post("/educenter/student", stu, token), role + " 新增学员");
            // 写入（手动调整课时）
            Map<String, Object> adj = new HashMap<>();
            adj.put("courseTimeId", f.classCourseTimeId);
            adj.put("changeType", 0);
            adj.put("num", 2);
            adj.put("reason", "OS023 矩阵正向");
            assertAllowed(post("/educenter/student/updateCourseTime", adj, token), role + " 手动调增课时");
            // 点名
            assertAllowed(rollCall(token, f.classId, START, END,
                    Collections.singletonMap(f.studentId, TYPE_PRESENT)), role + " 点名");
            // 纠错
            Long callId = latestCallRecordId(f.classId);
            assertNotNull(callId, role + " 点名应生成课消记录");
            Map<String, Object> corr = new HashMap<>();
            corr.put("id", callId);
            corr.put("type", TYPE_LEAVE);
            assertAllowed(put("/educenter/record/saveRollCall", corr, token), role + " 纠错改请假");
            cleanup();
        }
    }

    // ------------------------------------------------------- 矩阵二：归属诱饵

    /** 造一套诱饵机构（mec 2 / campus 2）的最小数据：课程+班级+学员+课时。返回关键字段 id。 */
    private Map<String, Long> createDecoyWorld(String label) {
        Map<String, Long> d = new HashMap<>();
        String tag = "decoy-" + label + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        d.put("courseId", insert("INSERT INTO bd_course (mec_id, campus_id, price, paid_price, time_num, course_name, "
                + "subject, is_shelf, is_experience, introduction, is_delete, create_time, create_by, amount_cny) "
                + "VALUES (2,2,0,0,20,?,'科目',1,1,'诱饵',0,NOW(),1,0)", "OS023诱饵课程-" + tag));
        track("DELETE FROM bd_course WHERE id = ?", d.get("courseId"));
        d.put("classId", insert("INSERT INTO bd_class (mec_id, campus_id, has_class, have_class, bishop_teacher_id, "
                + "sum_students, exist_students, class_name, class_course_id, version, is_delete, class_type, class_state, "
                + "create_time, create_by) VALUES (2,2,0,0,0,30,0,?,?,0,0,1,0,NOW(),1)", "OS023诱饵班级-" + tag, d.get("courseId")));
        track("DELETE FROM bd_class_record WHERE class_id = ?", d.get("classId"));
        track("DELETE FROM bd_call_record WHERE class_id = ?", d.get("classId"));
        track("DELETE FROM bd_class_time WHERE class_id = ?", d.get("classId"));
        track("DELETE FROM bd_class WHERE id = ?", d.get("classId"));
        d.put("studentId", insertDecoyStudent(2L, 2L, label));
        d.put("courseTimeId", insert("INSERT INTO bd_course_time (mec_id, campus_id, student_id, course_id, total_number, "
                + "remaining_number, is_delete, create_time, create_by, version) VALUES (2,2,?,?,7,7,0,NOW(),1,0)",
                d.get("studentId"), d.get("courseId")));
        track("DELETE FROM bd_course_time_update WHERE course_time_id = ?", d.get("courseTimeId"));
        track("DELETE FROM bd_course_time WHERE id = ?", d.get("courseTimeId"));
        // 诱饵课消记录（供纠错越权用）
        d.put("callRecordId", insert("INSERT INTO bd_call_record (mec_id, campus_id, class_id, student_id, class_record_id, "
                + "class_time_num, type, is_delete, create_time, create_by) VALUES (2,2,?,?,?,1,'0',0,NOW(),1)",
                d.get("classId"), d.get("studentId"), "decoy-cr-" + tag));
        track("DELETE FROM bd_call_record WHERE id = ?", d.get("callRecordId"));
        return d;
    }

    private String decoyName(Map<String, Long> d) {
        return jdbc.queryForObject("SELECT student_name FROM bd_student WHERE id = ?", String.class, d.get("studentId"));
    }

    @Test
    @DisplayName("矩阵B1 诱饵详情/记录不得泄露内容，伪造归属字段不得污染落库")
    void decoyDetailNotLeaked() {
        String admin = adminToken();
        Map<String, Long> d = createDecoyWorld("b1");
        String name = decoyName(d);

        // 详情直读诱饵学员：响应不得包含诱饵姓名（无论接口选择 403/500/空）
        ApiResult detail = get("/educenter/student/base/" + d.get("studentId"), admin);
        String bodyText = String.valueOf(detail.body);
        assertFalse(bodyText.contains(name), "诱饵学员详情被泄露：" + detail);

        // 诱饵课消记录详情：同样不得泄露
        ApiResult callInfo = get("/educenter/record/getCallRecordInfo/" + d.get("callRecordId"), admin);
        assertFalse(String.valueOf(callInfo.body).contains(name), "诱饵课消记录详情被泄露：" + callInfo);

        // 伪造归属字段：新增学员带 mecId=99/campusId=99，落库必须仍是会话归属
        Map<String, Object> stu = new HashMap<>();
        stu.put("studentName", "OS023伪造归属-" + System.nanoTime());
        stu.put("studentsPhone", freshPhone());
        stu.put("parentsName", "OS023家长");
        stu.put("parentsPhone", freshPhone());
        stu.put("mecId", 99);
        stu.put("campusId", 99);
        assertAllowed(post("/educenter/student", stu, admin), "伪造归属新增学员");
        Map<String, Object> row = jdbc.queryForMap(
                "SELECT id, mec_id, campus_id FROM bd_student WHERE student_name = ?", stu.get("studentName"));
        assertEquals(SEED_MEC_ID, ((Number) row.get("mec_id")).longValue(), "学员归属被伪造入参污染");
        assertEquals(SEED_CAMPUS_ID, ((Number) row.get("campus_id")).longValue(), "学员校区被伪造入参污染");
        track("DELETE FROM bd_student WHERE id = ?", ((Number) row.get("id")).longValue());
        cleanup();
    }

    @Test
    @DisplayName("矩阵B2 跨机构写入（改学员/报读/调课时）不得改变诱饵数据或留下流水")
    void decoyWriteTamperBlocked() {
        String admin = adminToken();
        Fixture f = newFixture("b2", 10, 10);
        Map<String, Long> d = createDecoyWorld("b2");
        String name = decoyName(d);

        // ① 改诱饵学员：提供完整合法请求体（绕过校验崩路径），落库必须不变
        Map<String, Object> edit = new HashMap<>();
        edit.put("id", d.get("studentId"));
        edit.put("studentName", "OS023被篡改-" + System.nanoTime());
        edit.put("studentsPhone", jdbc.queryForObject(
                "SELECT students_phone FROM bd_student WHERE id = ?", String.class, d.get("studentId")));
        edit.put("parentsName", "OS023篡改家长");
        edit.put("parentsPhone", freshPhone());
        edit.put("mecId", 2);
        edit.put("campusId", 2);
        put("/educenter/student", edit, admin);
        String nameAfter = jdbc.queryForObject(
                "SELECT student_name FROM bd_student WHERE id = ?", String.class, d.get("studentId"));
        assertEquals(name, nameAfter, "诱饵学员资料被跨机构修改");

        // ② 给诱饵学员报读：不得给诱饵产生购买记录或课时账户
        ApiResult enroll = post("/educenter/student/addCoursePurchase", mapOf(
                "studentId", d.get("studentId"), "courseId", f.courseId, "timeNum", 3, "amountCny", 99.0), admin);
        assertEquals(0, countRows("SELECT COUNT(*) FROM bd_course_purchase WHERE student_id = ?", d.get("studentId")),
                "诱饵学员被跨机构报读（产生购买记录）：" + enroll);
        assertEquals(0, countRows("SELECT COUNT(*) FROM bd_course_time WHERE student_id = ? AND course_id = ?",
                d.get("studentId"), f.courseId), "诱饵学员被跨机构建立课时账户：" + enroll);

        // ③ 调诱饵课时：余额与流水都必须不变
        ApiResult adj = post("/educenter/student/updateCourseTime", mapOf(
                "courseTimeId", d.get("courseTimeId"), "changeType", 0, "num", 5, "reason", "越权调增"), admin);
        Integer remaining = jdbc.queryForObject(
                "SELECT remaining_number FROM bd_course_time WHERE id = ?", Integer.class, d.get("courseTimeId"));
        assertEquals(7, remaining.intValue(), "诱饵课时被跨机构调增：" + adj);
        assertEquals(0, countRows("SELECT COUNT(*) FROM bd_course_time_update WHERE course_time_id = ?", d.get("courseTimeId")),
                "诱饵课时被跨机构留下调整流水");
        cleanup();
    }

    @Test
    @DisplayName("矩阵B3 跨机构点名/纠错不得改变诱饵班级、课消与课时")
    void decoyRollCallAndCorrectionBlocked() {
        String admin = adminToken();
        Map<String, Long> d = createDecoyWorld("b3");

        // ① 对诱饵班级点名：不得产生上课/课消记录（以点名前基线为参照：夹具自带一条诱饵课消记录），诱饵课时不得被扣
        long classRecordBase = countRows("SELECT COUNT(*) FROM bd_class_record WHERE class_id = ? AND is_delete = 0", d.get("classId"));
        long callRecordBase = countRows("SELECT COUNT(*) FROM bd_call_record WHERE class_id = ? AND is_delete = 0", d.get("classId"));
        ApiResult roll = rollCall(admin, d.get("classId"), START, END,
                Collections.singletonMap(d.get("studentId"), TYPE_PRESENT));
        assertEquals(classRecordBase, countRows("SELECT COUNT(*) FROM bd_class_record WHERE class_id = ? AND is_delete = 0", d.get("classId")),
                "诱饵班级被跨机构点名（产生上课记录）：" + roll);
        assertEquals(callRecordBase, countRows("SELECT COUNT(*) FROM bd_call_record WHERE class_id = ? AND is_delete = 0", d.get("classId")),
                "诱饵班级被跨机构点名（产生课消记录）");
        Integer remaining = jdbc.queryForObject(
                "SELECT remaining_number FROM bd_course_time WHERE id = ?", Integer.class, d.get("courseTimeId"));
        assertEquals(7, remaining.intValue(), "诱饵课时被跨机构点名扣减");

        // ② 对诱饵课消记录纠错：记录状态与课时都必须不变
        ApiResult corr = put("/educenter/record/saveRollCall", mapOf("id", d.get("callRecordId"), "type", TYPE_LEAVE), admin);
        String typeAfter = jdbc.queryForObject(
                "SELECT type FROM bd_call_record WHERE id = ?", String.class, d.get("callRecordId"));
        assertEquals("0", typeAfter, "诱饵课消记录被跨机构纠错：" + corr);
        remaining = jdbc.queryForObject(
                "SELECT remaining_number FROM bd_course_time WHERE id = ?", Integer.class, d.get("courseTimeId"));
        assertEquals(7, remaining.intValue(), "诱饵课时被跨机构纠错退回");
        assertEquals(0, countRows("SELECT COUNT(*) FROM bd_course_time_update WHERE student_id = ?", d.get("studentId")),
                "诱饵学员被跨机构留下课时流水");
        cleanup();
    }

    @Test
    @DisplayName("矩阵B4 学员列表对本机构会话零泄露；正向对照本机构学员可见")
    void studentListNoDecoyLeak() {
        String admin = adminToken();
        Fixture f = newFixture("b4", 10, 10);
        Map<String, Long> d = createDecoyWorld("b4");
        String name = decoyName(d);
        for (String token : new String[]{admin, academicToken("b4a"), teacherToken("b4t")}) {
            ApiResult list = get("/educenter/student/list?pageNum=1&pageSize=500", token);
            assertAllowed(list, "学员列表");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rows = (List<Map<String, Object>>) list.body.get("rows");
            assertNotNull(rows, "列表应有 rows");
            long leaked = rows.stream().filter(r -> String.valueOf(r.get("studentName")).equals(name)).count();
            assertEquals(0, leaked, "诱饵学员出现在本机构列表中");
        }
        // 正向对照：本机构学员可见（同一个教务会话）
        String academic = academicToken("b4p");
        ApiResult list = get("/educenter/student/list?pageNum=1&pageSize=500", academic);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) list.body.get("rows");
        long ownVisible = rows.stream().filter(r -> String.valueOf(r.get("studentName")).equals(f.studentName)).count();
        assertEquals(1, ownVisible, "本机构学员应可见（避免假安全）");
        cleanup();
    }

    @Test
    @DisplayName("矩阵B5 导出端点（OS024 已实现 C14 最小导出）：同归属可导出并返回文件名")
    void exportEndpointState() {
        // OS023 登记时端点尚不存在；OS024 实现 C14 最小导出后，此处更新为正向断言。
        // 权限语义不变：类级 @PreAuthorize('educenter:student:list')，三角色按种子均可导出。
        String admin = adminToken();
        ApiResult r = get("/educenter/student/export?pageNum=1&pageSize=10", admin);
        assertEquals(200, r.code(), "导出接口应成功：" + r);
        assertTrue(String.valueOf(r.data()).endsWith(".xlsx"), "应返回 xlsx 文件名：" + r);
        cleanup();
    }

    private static Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> m = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return m;
    }
}
