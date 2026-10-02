package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS012 最低测试集 ③：学员、教师、课程、班级 CRUD。
 *
 * <p>判据全部落在「数据库里真的变了」，而不是「接口返回 200」——
 * 新增接口只返回布尔，且部分删除是软删，必须回查落库字段：
 * 学员删除=回收站 {@code is_ordinary=1}、教师/课程删除={@code is_delete=1}、班级作废={@code class_state=1}。
 */
@DisplayName("OS012-03 学员 / 教师 / 课程 / 班级 CRUD")
class CommunityC03CrudTest extends CommunityTestBase {

    @Test
    @DisplayName("学员：新增→详情→修改→删除(回收站)→恢复")
    void studentCrud() {
        String admin = adminToken();
        String tag = UUID.randomUUID().toString().substring(0, 8);
        String name = "OS012学员-" + tag;
        String phone = freshPhone();

        Map<String, Object> create = new HashMap<>();
        create.put("studentName", name);
        create.put("studentsPhone", phone);
        create.put("parentsName", "OS012家长-" + tag);
        create.put("parentsPhone", freshPhone());
        create.put("schoolName", "OS012学校");
        create.put("grade", "三年级");
        create.put("age", "9");
        create.put("sex", 0);
        assertEquals(200, post("/educenter/student", create, admin).code(), "新增学员应成功");

        long id = idByName("bd_student", "student_name", name);
        track("DELETE FROM bd_student WHERE id = ?", id);

        // R
        ApiResult read = get("/educenter/student/base/" + id, admin);
        assertEquals(200, read.code(), "学员详情应可读：" + read);
        assertNotNull(read.data(), "学员详情 data 不应为空");

        // U
        Map<String, Object> update = new HashMap<>();
        update.put("id", id);
        update.put("studentName", name + "-改");
        update.put("studentsPhone", phone);
        assertEquals(200, put("/educenter/student", update, admin).code(), "修改学员应成功");
        assertEquals(name + "-改", jdbc.queryForObject(
                "SELECT student_name FROM bd_student WHERE id = ?", String.class, id), "学员姓名未落库");

        // D：学员「删除」= 放入回收站
        assertEquals(200, delete("/educenter/student/" + id, admin).code(), "删除学员应成功");
        assertEquals(1, jdbc.queryForObject(
                "SELECT is_ordinary FROM bd_student WHERE id = ?", Integer.class, id), "删除应落成回收站标记 is_ordinary=1");
        assertEquals(0, jdbc.queryForObject(
                "SELECT is_delete FROM bd_student WHERE id = ?", Integer.class, id), "回收站语义不是物理删除");

        // 恢复
        assertEquals(200, get("/educenter/student/recoverRec/" + id, admin).code(), "恢复学员应成功");
        assertEquals(0, jdbc.queryForObject(
                "SELECT is_ordinary FROM bd_student WHERE id = ?", Integer.class, id), "恢复后应为在读学员");
    }

    @Test
    @DisplayName("教师：新增→详情→修改→删除(软删)")
    void teacherCrud() {
        String admin = adminToken();
        String tag = UUID.randomUUID().toString().substring(0, 8);
        String name = "OS012教师-" + tag;
        String phone = freshPhone();

        Map<String, Object> create = new HashMap<>();
        create.put("name", name);
        create.put("phone", phone);
        create.put("sex", "0");
        create.put("inductionTime", new java.util.Date());
        create.put("personType", 1);
        assertEquals(200, post("/educenter/teacher", create, admin).code(), "新增教师应成功");

        long id = idByName("bd_teacher", "name", name);
        track("DELETE FROM bd_teacher WHERE id = ?", id);

        assertEquals(200, get("/educenter/teacher/" + id, admin).code(), "教师详情应可读");

        Map<String, Object> update = new HashMap<>();
        update.put("id", id);
        update.put("name", name + "-改");
        update.put("phone", phone);
        update.put("sex", "0");
        update.put("inductionTime", new java.util.Date());
        assertEquals(200, put("/educenter/teacher", update, admin).code(), "修改教师应成功");
        assertEquals(name + "-改", jdbc.queryForObject(
                "SELECT name FROM bd_teacher WHERE id = ?", String.class, id), "教师姓名未落库");

        assertEquals(200, delete("/educenter/teacher/" + id, admin).code(), "删除教师应成功");
        assertEquals(1, jdbc.queryForObject(
                "SELECT is_delete FROM bd_teacher WHERE id = ?", Integer.class, id), "教师删除应为软删 is_delete=1");
    }

    @Test
    @DisplayName("课程：新增→详情→修改→删除(软删)")
    void courseCrud() {
        String admin = adminToken();
        String tag = UUID.randomUUID().toString().substring(0, 8);
        String name = "OS012课程-" + tag;

        Map<String, Object> create = new HashMap<>();
        create.put("courseName", name);
        create.put("subject", "测试科目");
        create.put("timeNum", 20);
        create.put("isShelf", 1);
        create.put("isExperience", 1);
        create.put("introduction", "OS012 测试课程");
        create.put("amountCny", 399.00);
        assertEquals(200, post("/educenter/course", create, admin).code(), "新增课程应成功");

        long id = idByName("bd_course", "course_name", name);
        track("DELETE FROM bd_course WHERE id = ?", id);

        assertEquals(200, get("/educenter/course/" + id, admin).code(), "课程详情应可读");
        // 方案 B：金额只走 amount_cny，商业版 price 列保持占位（schema 默认 0），不参与业务
        assertEquals(399.00, jdbc.queryForObject(
                        "SELECT amount_cny FROM bd_course WHERE id = ?", java.math.BigDecimal.class, id).doubleValue(),
                0.001, "amount_cny 未落库");

        Map<String, Object> update = new HashMap<>();
        update.put("id", id);
        update.put("courseName", name + "-改");
        update.put("subject", "测试科目");
        update.put("timeNum", 24);
        update.put("isShelf", 1);
        update.put("isExperience", 1);
        update.put("introduction", "OS012 测试课程");
        assertEquals(200, put("/educenter/course", update, admin).code(), "修改课程应成功");
        assertEquals(name + "-改", jdbc.queryForObject(
                "SELECT course_name FROM bd_course WHERE id = ?", String.class, id), "课程名未落库");

        assertEquals(200, delete("/educenter/course/" + id, admin).code(), "删除课程应成功");
        assertEquals(1, jdbc.queryForObject(
                "SELECT is_delete FROM bd_course WHERE id = ?", Integer.class, id), "课程删除应为软删 is_delete=1");
    }

    @Test
    @DisplayName("班级：新增→详情→修改→作废(软删)")
    void classCrud() {
        String admin = adminToken();
        Fixture f = newCourseWithTeacher("c03cls");
        String className = f.className + "-api";

        Map<String, Object> create = new HashMap<>();
        create.put("className", className);
        create.put("classCourseId", f.courseId);
        create.put("bishopTeacherId", f.teacherId);
        create.put("sumStudents", 30);
        create.put("classType", 1);
        create.put("stuIds", new long[0]);
        ApiResult created = post("/educenter/class", create, admin);
        assertEquals(200, created.code(), "新增班级应成功：" + created);

        long classId = idByName("bd_class", "class_name", className);
        track("DELETE FROM bd_class_time WHERE class_id = ?", classId);
        track("DELETE FROM bd_class WHERE id = ?", classId);

        assertEquals(SEED_MEC_ID, jdbc.queryForObject(
                "SELECT mec_id FROM bd_class WHERE id = ?", Long.class, classId), "班级 mec_id 缺失");
        // B-8 回归：新增班级必须带上机构 / 校区归属，否则校区维度列表查不到
        assertEquals(SEED_CAMPUS_ID, jdbc.queryForObject(
                "SELECT campus_id FROM bd_class WHERE id = ?", Long.class, classId), "班级 campus_id 缺失");

        ApiResult read = get("/educenter/class/" + classId, admin);
        assertEquals(200, read.code(), "班级详情应可读：" + read);
        assertNotNull(read.data(), "班级详情 data 不应为空");

        Map<String, Object> update = new HashMap<>();
        update.put("id", classId);
        update.put("className", className + "-改");
        update.put("classCourseId", f.courseId);
        update.put("bishopTeacherId", f.teacherId);
        update.put("sumStudents", 35);
        update.put("classType", 1);
        assertEquals(200, put("/educenter/class", update, admin).code(), "修改班级应成功");
        assertEquals(className + "-改", jdbc.queryForObject(
                "SELECT class_name FROM bd_class WHERE id = ?", String.class, classId), "班级名未落库");

        assertEquals(200, delete("/educenter/class/" + classId, admin).code(), "作废班级应成功");
        assertEquals(1, jdbc.queryForObject(
                "SELECT class_state FROM bd_class WHERE id = ?", Integer.class, classId), "班级作废应为 class_state=1");

        Predicate<Integer> judgeSoftDeleted = state -> state != null && state == 1;
        assertJudge("班级作废判据", judgeSoftDeleted, 1, 0);
    }
}
