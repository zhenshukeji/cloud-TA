package com.zhenshu.community;

import com.zhenshu.community.support.CommunityTestBase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OS024（PRD C14 导出半边）：学员课时账户真实导出。
 *
 * <p>判据：
 * <ol>
 *   <li>导出链路真实可用：export 接口 → /common/download 下载到 xlsx 字节（PK 魔数）；</li>
 *   <li>筛选生效：按学员姓名模糊筛选，行数可复算；</li>
 *   <li>归属生效：他机构诱饵学员不得出现（OS023 守卫的导出侧延伸）；</li>
 *   <li>中文与单位：表头含「课时」「元·人民币」；中文值不乱码；</li>
 *   <li>公式注入防护：名称以 {@code =} 开头的学员在单元格中保持文本类型，不是公式；</li>
 *   <li>内容正确：报读金额、剩余课时与落库一致。</li>
 * </ol>
 */
@DisplayName("OS024 C14 学员课时账户导出")
class CommunityC14ExportTest extends CommunityTestBase {



    private byte[] exportXlsx(String token, String studentName) throws Exception {
        StringBuilder path = new StringBuilder("/educenter/student/export");
        if (studentName != null) {
            path.append("?studentName=").append(java.net.URLEncoder.encode(studentName, "UTF-8"));
        }
        ApiResult r = get(path.toString(), token);
        assertEquals(200, r.code(), "导出接口应成功：" + r);
        String filename = String.valueOf(r.data());
        assertTrue(filename.endsWith(".xlsx"), "应返回 xlsx 文件名：" + filename);

        // 下载并即时删除（服务端不留导出残留）。
        // 用 HttpURLConnection 直读：TestRestTemplate 对 multipart/form-data 的 byte[] 转换返回 null 体（实测踩坑），
        // curl 实测同一端点 200 + 完整 xlsx 字节，证明是客户端读取问题而非服务端问题。
        java.net.HttpURLConnection conn =
                (java.net.HttpURLConnection) new java.net.URL(
                        url("/common/download?fileName=" + java.net.URLEncoder.encode(filename, "UTF-8") + "&delete=true"))
                        .openConnection();
        conn.setRequestProperty("Accept", "application/octet-stream");
        assertEquals(200, conn.getResponseCode(), "下载应成功");
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        try (java.io.InputStream in = conn.getInputStream()) {
            byte[] chunk = new byte[8192];
            int n;
            while ((n = in.read(chunk)) > 0) {
                buf.write(chunk, 0, n);
            }
        }
        byte[] body = buf.toByteArray();
        assertNotNull(body, "下载体不应为空");
        assertEquals('P', (char) body[0], "应为 xlsx（zip）魔数");
        assertEquals('K', (char) body[1], "应为 xlsx（zip）魔数");
        return body;
    }

    @Test
    @DisplayName("导出：筛选、行数、中文单位、金额一致、公式注入防护、诱饵零泄露")
    void exportAccountWorkbook() throws Exception {
        String admin = adminToken();

        // 夹具：账户 12/7 + 报读 +5（199.50 元）→ 总 17 / 剩 17 / 累计 199.50 元
        Fixture f = newFixture("c14a", 12, 7);
        Map<String, Object> purchase = new HashMap<>();
        purchase.put("studentId", f.studentId);
        purchase.put("courseId", f.courseId);
        purchase.put("timeNum", 5);
        purchase.put("amountCny", 199.50);
        assertEquals(200, post("/educenter/student/addCoursePurchase", purchase, admin).code(), "报读应成功");
        // 公式注入样本学员（名字以 = 开头）
        Map<String, Object> formulaStu = new HashMap<>();
        formulaStu.put("studentName", "=1+1|OS023");
        formulaStu.put("studentsPhone", freshPhone());
        formulaStu.put("parentsName", "OS023家长");
        formulaStu.put("parentsPhone", freshPhone());
        assertEquals(200, post("/educenter/student", formulaStu, admin).code(), "建公式样本学员应成功");
        long formulaStuId = jdbc.queryForObject(
                "SELECT id FROM bd_student WHERE student_name = ?", Long.class, formulaStu.get("studentName"));
        track("DELETE FROM bd_student WHERE id = ?", formulaStuId);

        // 他机构诱饵学员（导出不得包含）
        long decoyId = insertDecoyStudent(2L, 2L, "c14");
        String decoyName = jdbc.queryForObject("SELECT student_name FROM bd_student WHERE id = ?", String.class, decoyId);


        // ---- 1) 筛选导出：只含夹具学员 ----
        byte[] filtered = exportXlsx(admin, "c14a");
        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(filtered))) {
            Sheet sheet = wb.getSheetAt(0);
            String header = headerText(sheet);
            assertTrue(header.contains("课时"), "表头应含课时单位：" + header);
            assertTrue(header.contains("元·人民币"), "表头应含人民币单位：" + header);
            assertTrue(header.contains("学员姓名"), "表头应含中文列名：" + header);

            int dataRows = countDataRows(sheet);
            assertEquals(1, dataRows, "筛选导出应恰 1 行（学员×账户）");

            Row row = sheet.getRow(1);
            assertEquals(f.studentName, row.getCell(0).getStringCellValue(), "学员姓名应一致");
            assertEquals(f.courseName, row.getCell(5).getStringCellValue(), "课程应一致");
            assertEquals(17, (int) row.getCell(6).getNumericCellValue(), "总课时 = 12 + 5");
            assertEquals(12, (int) row.getCell(7).getNumericCellValue(), "剩余课时 = 7 + 5");
            assertEquals(199.5, row.getCell(8).getNumericCellValue(), 0.001, "累计报读金额（元）");
        }

        // ---- 2) 全量导出：公式样本为文本、诱饵不出现、中文姓名可读 ----
        byte[] all = exportXlsx(admin, null);
        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(all))) {
            Sheet sheet = wb.getSheetAt(0);
            boolean sawFormula = false;
            boolean sawDecoy = false;
            int dataRows = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null) {
                    continue;
                }
                Cell nameCell = row.getCell(0);
                dataRows++;
                if ("=1+1|OS023".equals(nameCell.getStringCellValue())) {
                    sawFormula = true;
                    assertEquals(CellType.STRING, nameCell.getCellTypeEnum(),
                            "公式样式名称必须保持文本类型（防 Excel 公式注入）");
                }
                if (nameCell.getStringCellValue().equals(decoyName)) {
                    sawDecoy = true;
                }
            }
            assertTrue(sawFormula, "全量导出应包含公式样本学员");
            assertTrue(!sawDecoy, "他机构诱饵学员不得出现在导出中");
            // 行数可复算：DB 现算「学员行 + 学员×账户行」作为期望值（库中还有演示数据，不能用固定值）
            long expectedRows = countRows(
                    "SELECT (SELECT COUNT(*) FROM bd_student s WHERE s.is_delete = 0 AND s.is_ordinary = 0 "
                            + "AND s.mec_id = 1 AND s.campus_id = 1 "
                            + "AND NOT EXISTS (SELECT 1 FROM bd_course_time ct WHERE ct.student_id = s.id AND ct.is_delete = 0)) "
                            + "+ (SELECT COUNT(*) FROM bd_course_time ct JOIN bd_student s ON s.id = ct.student_id "
                            + "WHERE ct.is_delete = 0 AND s.is_delete = 0 AND s.is_ordinary = 0 AND s.mec_id = 1 AND s.campus_id = 1)");
            assertEquals(expectedRows, dataRows, "全量导出行数应与 DB 现算的「无账户学员行 + 账户行」一致");
        }
    }

    private String headerText(Sheet sheet) {
        Row header = sheet.getRow(0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < header.getLastCellNum(); i++) {
            sb.append(header.getCell(i) == null ? "" : header.getCell(i).getStringCellValue()).append('|');
        }
        return sb.toString();
    }

    private int countDataRows(Sheet sheet) {
        int n = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.getCell(0) != null
                    && row.getCell(0).getCellTypeEnum() != CellType.BLANK) {
                n++;
            }
        }
        return n;
    }
}
