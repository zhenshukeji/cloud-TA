package com.zhenshu.common.manager.mec.base;

import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTeacherBO;
import com.zhenshu.jianxue.mec.educenter.service.IBdTeacherService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/27 14:07
 * @desc 机构人员关联教师
 */
@Service
public class MecPerWithTeacherManager {

    @Resource
    private IBdTeacherService teacherService;

    /**
     * 通过机构人员得到老师id
     * @param mecPerson 机构人员id
     * @return 老师
     */
    public BdTeacherBO getTeacherByMecPerson(Long mecPerson, Integer personType){
        return teacherService.getTeacherByMecPerson(mecPerson, personType);
    }
}
