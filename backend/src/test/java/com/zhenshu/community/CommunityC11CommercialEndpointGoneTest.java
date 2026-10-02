package com.zhenshu.community;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhenshu.community.support.CommunityTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS012 最低测试集 ⑪：商业接口不存在。
 *
 * <p>数据驱动：逐条调用 OS008 冻结的「应 404 商业接口」清单（{@code commercial-endpoints.json}，
 * 265 条），要求每一条都真的 404。
 *
 * <p>为什么必须配正向对照：`/{id}` 这类通配路由会吃住被删路径，
 * 而泛型异常兜底又会把「方法不支持」变成 200（PITFALLS §25.17）。
 * 只测 404 而不证明「存活接口确实不是 404」，就可能把「路由整体坏掉」误判成通过。
 */
@DisplayName("OS012-11 商业接口不存在（265 条逐条 404）")
class CommunityC11CommercialEndpointGoneTest extends CommunityTestBase {

    /** 抽样比对的正向对照：这些接口在社区版存活，必须不是 404。 */
    private static final String[] LIVE_ENDPOINTS = {
            "GET /educenter/course/list?pageNum=1&pageSize=1",
            "GET /educenter/student/list?pageNum=1&pageSize=1",
            "GET /educenter/class/list?pageNum=1&pageSize=1",
            "GET /educenter/teacher/list?pageNum=1&pageSize=1",
            "GET /system/user/list?pageNum=1&pageSize=1",
            "GET /base/campus/list?pageNum=1&pageSize=1",
    };

    @Test
    @DisplayName("265 条商业接口全部 404，且存活接口不是 404（正/负向对照）")
    void excludedCommercialEndpointsAreReallyGone() throws Exception {
        String admin = adminToken();

        List<String[]> endpoints = loadEndpoints();
        assertEquals(265, endpoints.size(), "商业接口清单规模应与 OS008 冻结值一致");

        List<String> notGone = new ArrayList<>();
        for (String[] ep : endpoints) {
            ApiResult r = call(ep[0], normalize(ep[1]), null, admin);
            if (r.httpStatus != 404) {
                notGone.add(ep[0] + " " + ep[1] + " → HTTP " + r.httpStatus + " code=" + r.code());
            }
        }
        Predicate<Integer> judgeGone = status -> status != null && status == 404;
        // 判据自检：404 判真、200 判假
        assertJudge("接口已移除判据", judgeGone, 404, 200);

        assertTrue(notGone.isEmpty(), "以下已排除接口没有真正 404（共 " + notGone.size() + " 条）："
                + String.join(" | ", notGone));

        // 正向对照：存活接口不得是 404
        for (String probe : LIVE_ENDPOINTS) {
            int sp = probe.indexOf(' ');
            ApiResult alive = call(probe.substring(0, sp), probe.substring(sp + 1), null, admin);
            assertTrue(alive.httpStatus != 404, "存活接口不应 404：" + probe + " → HTTP " + alive.httpStatus);
            assertEquals(200, alive.code(), "存活接口应正常返回 code=200：" + probe + " → " + alive);
        }
    }

    private List<String[]> loadEndpoints() throws Exception {
        InputStream in = getClass().getResourceAsStream("/community/commercial-endpoints.json");
        assertNotNull(in, "缺少测试资源 community/commercial-endpoints.json");
        JsonNode root = new ObjectMapper().readTree(in);
        List<String[]> out = new ArrayList<>();
        for (JsonNode node : root.get("endpoints")) {
            out.add(new String[]{node.get("http").asText(), node.get("path").asText()});
        }
        return out;
    }

    /** 把路径模板 {@code /platform/role/{roleId}} 的具体值替换成 1：路由已被整段删除，取不到值不影响判据。 */
    private String normalize(String path) {
        return path.replaceAll("\\{[^}]*}", "1");
    }
}
