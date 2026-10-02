package com.zhenshu.common.manager.mec.base;

import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTeacherBO;
import com.zhenshu.jianxue.mec.educenter.service.IBdTeacherService;
import com.zhenshu.mapper.domain.educenter.BdTeacher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/27 13:54
 * @desc 校区人员关联老师
 */
@Service
public class CampusPerWithTeacherManager {

    @Resource
    private IBdTeacherService teacherService;

    /**
     * 通过校区人员得到老师id
     * @param campusPerson 校区人员id
     * @return 老师
     */
    public BdTeacherBO getTeacherByCampusPerson(Long campusPerson, Integer personType){
        return teacherService.getTeacherByCampusPerson(campusPerson, personType);
    }

    /**
     * 校验该电话的老师是否存在
     * @param phone 电话
     * @return 是否存在
     */
    public boolean checkTeacherExist(String phone) {
        return teacherService.checkTeacherExist(phone);
    }

    /**
     * 校区人员新增老师
     * @param teacher 老师信息
     */
    public void insertTeacher(BdTeacher teacher) {
        teacherService.insert(teacher);
    }
}
