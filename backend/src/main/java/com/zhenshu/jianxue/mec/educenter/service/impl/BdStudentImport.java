package com.zhenshu.jianxue.mec.educenter.service.impl;

import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.SexEnums;
import com.zhenshu.common.enums.educenter.StudentEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.RegexUtil;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.jianxue.mec.educenter.domain.vo.ExportStudentVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdStudentService;
import com.zhenshu.mapper.domain.educenter.BdStudent;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.print.DocFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/22 17:57
 * @desc 学生导入
 */
@Service
public class BdStudentImport {

    private static final String XLS = "xls";

    private static final String XLSX = "xlsx";

    private static final Integer MAX_NUM = 2000;

    private static final Integer INSERT_NUM = 200;

    @Resource
    private IBdStudentService studentService;


    @Transactional(rollbackFor = CustomException.class)
    public String importStudents(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        //判断是不是excel文件
        if (fileName != null && !fileName.endsWith(XLS) && !fileName.endsWith(XLSX)) {
            throw new CustomException("文件类型错误");
        }
        //记录错误信息
        StringBuilder builder = new StringBuilder();
        try {
            //获得Workbook工作薄对象
            Workbook workbook = getWorkBook(file);
            if (workbook == null) {
                throw new CustomException("文件读取错误");
            }
            // 只拿第一个工作表的
            Sheet sheet = workbook.getSheetAt(0);
            //获得当前sheet有效行数与有效手机集合
            ExportStudentVO studentVO = getValidNumberOfRow(sheet);
            List<Integer> rowNumList = studentVO.getRowNumList();

            ArrayList<BdStudent> addStudentList = new ArrayList<>(INSERT_NUM);
            HashSet<String> hashSet = new HashSet<>();
            // 判断同一个excel 里是否有重复的电话号码
            for (Integer rowNum : rowNumList) {
                Row row = sheet.getRow(rowNum);
                BdStudent student = changeToStudent(row, rowNum, builder);
                // 如果该电话号码已存在，说明该excel 里有重复的电话号码
                if(hashSet.contains(student.getStudentsPhone())){
                    continue;
                }
                hashSet.add(student.getStudentsPhone());
                addStudentList.add(student);
            }
            // 批处理新增学员
            studentService.insertBatch(addStudentList, INSERT_NUM);
            workbook.close();
        } catch (IOException e) {
            throw new CustomException("文件错误");
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
        if (StringUtils.isEmpty(builder.toString())) {
            return "导入成功";
        }
        return builder.toString();
    }

    /**
     * 判断该工作表中有效行数
     *
     * @return long
     */
    public ExportStudentVO getValidNumberOfRow(Sheet sheet) {
        // 手机号查询存储
        TreeSet<String> phoneSet = new TreeSet<>();
        // 存储有效的行数编号
        List<Integer> rowNumList = new ArrayList<>();

        long num = 0;
        long lastRowNum = sheet.getLastRowNum();
        for (int rowNum = 3; rowNum <= lastRowNum; rowNum++) {
            // 获取行数
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            Cell cell = row.getCell(StudentEnum.NAME.getCode());
            if (cell == null) {
                continue;
            }
            cell.setCellType(CellType.STRING);
            //如果学生姓名和手机号都为空，说明该行无效
            Cell nameCell = row.getCell(StudentEnum.NAME.getCode());
            Cell phoneCell = row.getCell(StudentEnum.PHONE.getCode());
            if (nameCell == null || phoneCell == null) {
                // 跳过无效行
                continue;
            }
            //学员手机号
            phoneCell.setCellType(CellType.STRING);
            String phoneValue = phoneCell.getStringCellValue().trim();
            //将值设置为字符串类型
            nameCell.setCellType(CellType.STRING);
            String nameValue = nameCell.getStringCellValue().trim();
            if (StringUtils.isEmpty(phoneValue) && StringUtils.isEmpty(nameValue)) {
                // 跳过无效行
                continue;
            }
            if (cell.getStringCellValue() != null && !StringUtils.isEmpty(cell.getStringCellValue())) {
                num++;
                if (num > MAX_NUM) {
                    throw new CustomException("上传学生行数最多2000条");
                }
            }
            //学员手机号不能为空
            if (StringUtils.isEmpty(phoneValue)) {
                throw new CustomException(String.format("第%s行的学生手机号不能为空", rowNum + 1));
            }
            // 手机号校验
            if (!RegexUtil.isMobile(phoneValue.trim())) {
                throw new CustomException(String.format("第%s行的学生手机号不正确请重新输入", rowNum + 1));
            }
            //学员姓名不能为空
            if (StringUtils.isEmpty(nameCell.getStringCellValue())) {
                throw new CustomException(String.format("第%s行的学生姓名不能为空", rowNum + 1));
            }
            //手机号为学生的唯一标识使用sql来进行判断
            phoneSet.add(phoneValue);
            rowNumList.add(rowNum);
        }
        // 判断电话号码是否存在于数据库
        checkStudentPhoneExist(phoneSet);
        ExportStudentVO studentVO = new ExportStudentVO();
        studentVO.setRowNumList(rowNumList);
        return studentVO;
    }

    /**
     * 判断学生手机号是否存在
     *
     * @param phoneSet 手机号
     */
    private void checkStudentPhoneExist(TreeSet<String> phoneSet) {
        // 判断数据是否已存在
        List<BdStudent> studentList = studentService.selectStudentByPhones(phoneSet);
        if (studentList != null && !studentList.isEmpty()) {
            int size = 11;
            int count = 0;
            StringBuilder stringJoiner = new StringBuilder();
            for (BdStudent bdStudent : studentList) {
                if (phoneSet.contains(bdStudent.getStudentsPhone())) {
                    stringJoiner.append(bdStudent.getStudentsPhone()).append(",");
                }
                count++;
                // 每十一个的时候进行换行，优化展示效果
                if (count % size == 0) {
                    stringJoiner.append("\n");
                }
            }
            throw new CustomException(String.format("学生/家长手机号%s已存在", stringJoiner.toString().substring(0, stringJoiner.length() - 1)));
        }
    }

    /**
     * 学生导入方法
     *
     * @param row     行的数组
     * @param rowNum  当前第几行
     * @param builder 错误信息
     */
    public BdStudent changeToStudent(Row row, int rowNum, StringBuilder builder) {
        BdStudent vo = new BdStudent();
        //学员手机号
        Cell phone = row.getCell(StudentEnum.PHONE.getCode());
        phone.setCellType(CellType.STRING);
        String phoneValue = phone.getStringCellValue();
        //学员手机号不能为空
        if (StringUtils.isEmpty(phoneValue)) {
            builder.append("第").append(rowNum + 1).append("行的学生手机号不能为空").append("\n");
            throw new CustomException(builder.toString());
        }
        vo.setStudentsPhone(phoneValue.trim());
        Cell name = row.getCell(StudentEnum.NAME.getCode());
        //将值设置为字符串类型
        name.setCellType(CellType.STRING);
        //学员姓名与学员手机号不能为空
        if (StringUtils.isEmpty(name.getStringCellValue())) {
            builder.append("第").append(rowNum + 1).append("行的学生姓名不能为空").append("\n");
            throw new CustomException(builder.toString());
        }
        //学员姓名
        vo.setStudentName(name.getStringCellValue());
        //年纪
        Cell grade = row.getCell(StudentEnum.GRACE.getCode());
        if (grade != null) {
            grade.setCellType(CellType.STRING);
            vo.setGrade(grade.getStringCellValue());
        }
        //性别
        Cell sex = row.getCell(StudentEnum.SEX.getCode());
        if (sex != null) {
            sex.setCellType(CellType.STRING);
            vo.setSex(SexEnums.getCodeByInfo(sex.getStringCellValue()));
        }
        //年龄
        Cell age = row.getCell(StudentEnum.AGE.getCode());
        if (age != null) {
            age.setCellType(CellType.STRING);
            vo.setAge(age.getStringCellValue());
        }
        //学校/幼儿园
        Cell school = row.getCell(StudentEnum.SCHOOL.getCode());
        if (school != null) {
            school.setCellType(CellType.STRING);
            vo.setSchoolName(school.getStringCellValue());
        }
        //地址
        Cell address = row.getCell(StudentEnum.ADDRESS.getCode());
        if (address != null) {
            address.setCellType(CellType.STRING);
            vo.setAddress(address.getStringCellValue());
        }
        //关系
        Cell relation = row.getCell(StudentEnum.RELATION.getCode());
        if (relation != null) {
            relation.setCellType(CellType.STRING);
            vo.setAssociated(StudentEnum.getRelation(relation.getStringCellValue()));
        }
        //家长姓名
        Cell parentName = row.getCell(StudentEnum.RELATION_NAME.getCode());
        if (parentName != null) {
            parentName.setCellType(CellType.STRING);
            vo.setParentsName(parentName.getStringCellValue());
        }
        //家长电话号码
        Cell parentPhone = row.getCell(StudentEnum.RELATION_PHONE.getCode());
        if (parentPhone != null) {
            parentPhone.setCellType(CellType.STRING);
            vo.setParentsPhone(parentPhone.getStringCellValue());
        }
        //设置机构id，校区id
        vo.setCreateTime(DateUtils.getNowDate());
        vo.setCreateBy(SecurityUtils.getUserId().toString());
        vo.setMecId(SecurityUtils.getLoginMecId());
        vo.setCampusId(SecurityUtils.getLoginCampusId());
        vo.setIsDelete(Constants.NOT_DELETED);
        // 设置为在读学员
        vo.setIsOrdinary(StudentEnum.NORMAL_STU.getCode());
        vo.setRegisterTime(DateUtils.getNowDate());
        return vo;
    }

    /**
     * 获得Workbook工作薄对象
     *
     * @param file 上传的文件
     * @return Workbook
     */
    public Workbook getWorkBook(MultipartFile file) {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream is = file.getInputStream();
            if (!StringUtils.isEmpty(fileName)) {
                //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
                assert fileName != null;
                if (fileName.endsWith(XLS)) {
                    //2003
                    workbook = new HSSFWorkbook(is);
                } else if (fileName.endsWith(XLSX)) {
                    //2007
                    workbook = new XSSFWorkbook(is);
                } else {
                    throw new CustomException("文件类型错误");
                }
            }
        } catch (IOException e) {
            throw new CustomException("文件读取错误");
        }
        return workbook;
    }
}
