package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS012 最低测试集 ②：角色权限正向与负向。
 *
 * <p>纪律：本项目「权限拒绝」的形态是 <b>HTTP 200 + body.code=403</b>，
 * 因此判据只读 {@code body.code}；用 HTTP 状态码当判据会假通过。
 */
@DisplayName("OS012-02 角色权限正向与负向")
class CommunityC02RolePermissionTest extends CommunityTestBase {

    @Test
    @DisplayName("授课教师：菜单内接口 200，菜单外管理员接口 403；管理员正向对照 200")
    void teacherCannotReachAdminApis() {
        String teacherPassword = "os012-teacher-" + java.util.UUID.randomUUID().toString().substring(0, 10);
        String teacherAccount = createCampusAccount("permission", 3, 1, teacherPassword);
        // 校区人员用校区人员口径登录：/base/access/login 是机构人员入口，对校区人员返回「非机构人员操作」属既有行为
        String teacherToken = loginCampusUser(teacherAccount, teacherPassword);
        String admin = adminToken();

        // 正向：教师菜单内的教务接口
        ApiResult positive = get("/educenter/course/list?pageNum=1&pageSize=1", teacherToken);
        // 负向：教师菜单外的管理员接口
        ApiResult denyUser = get("/system/user/list?pageNum=1&pageSize=1", teacherToken);
        ApiResult denyRole = get("/system/role/list?pageNum=1&pageSize=1", teacherToken);
        ApiResult denyLog = get("/monitor/operlog/list?pageNum=1&pageSize=1", teacherToken);
        // 正向对照：同一接口管理员可访问，证明 403 不是「接口坏了」
        ApiResult adminUser = get("/system/user/list?pageNum=1&pageSize=1", admin);

        Predicate<ApiResult> judgeAllowed = r -> Integer.valueOf(200).equals(r.code());
        assertJudge("允许访问判据", judgeAllowed, positive, denyUser);

        assertEquals(200, positive.code(), "教师菜单内 /educenter/course/list 应可访问：" + positive);

        for (ApiResult denied : new ApiResult[]{denyUser, denyRole, denyLog}) {
            assertEquals(200, denied.httpStatus,
                    "权限拒绝在本项目里是 HTTP 200 + code=403，HTTP 状态码不是判据：" + denied);
            assertEquals(403, denied.code(), "教师越权访问管理员接口应被拒（code=403）：" + denied);
            assertFalse(judgeAllowed.test(denied), "被拒响应不得满足「允许访问判据」");
        }

        assertEquals(200, adminUser.code(), "管理员访问同一接口应为 200（正向对照）：" + adminUser);

        // 数据库侧对照：角色-菜单授权确实不同（证明差异来自授权，而不是接口偶发）
        long teacherGrants = countRows("SELECT COUNT(*) FROM sys_role_menu WHERE role_id = 3");
        long adminGrants = countRows("SELECT COUNT(*) FROM sys_role_menu WHERE role_id = 1");
        assertTrue(teacherGrants > 0, "授课教师角色应有菜单授权，实际=" + teacherGrants);
        assertTrue(adminGrants > teacherGrants,
                "管理员授权数应多于教师：" + adminGrants + " vs " + teacherGrants);
    }
}
