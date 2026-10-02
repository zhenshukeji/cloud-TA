package com.zhenshu.jianxue.mec.educenter.manager;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.educenter.ClassEnum;
import com.zhenshu.common.enums.educenter.TeacherEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.manager.mec.educenter.TeacherWithCampusPerManager;
import com.zhenshu.common.manager.mec.educenter.TeacherWithMecPerManager;
import com.zhenshu.framework.web.domain.ResultCode;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMecPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusPersonnelVO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdMecPersonnelVO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTeacherBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdTeacherVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassService;
import com.zhenshu.jianxue.mec.educenter.service.IBdTeacherService;
import com.zhenshu.mapper.domain.educenter.BdClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师调用其他表的service
 *
 * @author yuxi
 * @version 1.0
 * @date 2020-09-29
 */
@Service
public class TeacherManager {

    private static final Logger log = LoggerFactory.getLogger(TeacherManager.class);

    @Resource
    private IBdClassService classService;

    @Resource
    private IBdTeacherService teacherService;

    @Resource
    private TeacherWithCampusPerManager teacherWithCamPerManager;

    @Resource
    private TeacherWithMecPerManager teacherWithMecPerManager;

    /**
     * 获得老师详情
     *
     * @param id 老师id
     * @return 老师信息
     */
    public BdTeacherBO selectBdTeacherById(Long id) {
        BdTeacherBO teacherBO = teacherService.selectBdTeacherById(id);
        if (teacherBO == null) {
            log.warn(String.format("老师id为%s未查询到", id));
            throw new CustomException("为查询到该老师的信息");
        }
        // 获得主教班级
        String bishopClasses = classService.getBishopClasses(teacherBO.getId());
        teacherBO.setBishopClasses(bishopClasses);
        // 获得辅助班级
        String helpClasses = classService.getHelpClasses(teacherBO.getId());
        teacherBO.setAssistantClasses(helpClasses);
        // 获得关联人员
        // 如果是校区人员
        if (teacherBO.getPersonType() != null) {
            if (TeacherEnum.PERSON_CAMPUS.getCode() == teacherBO.getPersonType()) {
                BdCampusPersonnelBO campusPersonnelBO = teacherWithCamPerManager.getCampusPersonByTeacher(teacherBO.getAssociationPerson());
                if (campusPersonnelBO != null){
                    teacherBO.setAssociationPersonName(campusPersonnelBO.getName());
                }
                // 如果是机构人员
            } else if (TeacherEnum.PERSON_MEC.getCode() == teacherBO.getPersonType()) {
                BdMecPersonnelBO mecPersonnelBO = teacherWithMecPerManager.getMecPersonByTeacher(teacherBO.getAssociationPerson());
                if (mecPersonnelBO != null){
                    teacherBO.setAssociationPersonName(mecPersonnelBO.getName());
                }
            }
            // 设置离职时间
            if (teacherBO.getIsDeparture() == TeacherEnum.ON_JOB.getCode()) {
                teacherBO.setDepartureTime(null);
            }
        }
        return teacherBO;
    }

    /**
     * 获取机构人员
     *
     * @return 机构人员分页集合
     */
    public Page<BdMecPersonnelBO> getAllMecPerson(BdMecPersonnelVO mecPersonnelVO) {
        // 查询出被关联的机构人员id
        List<Long> mecPersonIds = teacherService.selectAllMecPerson();
        return teacherWithMecPerManager.getAllMecPerson(mecPersonnelVO, mecPersonIds);
    }

    /**
     * 获取校区人员
     *
     * @return 校区人员分页集合
     */
    public Page<BdCampusPersonnelBO> getAllCampusPerson(BdCampusPersonnelVO campusPersonnelVO) {
        // 查询出被关联的校区人员id
        List<Long> campusPersonIds = teacherService.selectAllCampusPerson();
        return teacherWithCamPerManager.getAllCampusPerson(campusPersonnelVO, campusPersonIds);
    }

    /**
     * 删除老师
     *
     * @param teacherIds 老师id
     */
    public void checkIsDelete(Long[] teacherIds) {
        // 主教班级
        BdClass bishopClass = new BdClass();
        // 助教班级
        BdClass helpClass = new BdClass();
        bishopClass.setIsDelete(Constants.NOT_DELETED);
        bishopClass.setClassState(ClassEnum.NORMAL_STATUS.getCode());

        helpClass.setIsDelete(Constants.NOT_DELETED);
        helpClass.setClassState(ClassEnum.NORMAL_STATUS.getCode());

        for (Long teacherId : teacherIds) {
            BdTeacherBO bdTeacherBO = teacherService.selectBdTeacherById(teacherId);
            if (bdTeacherBO == null){
                throw new CustomException("未查询到该教师，删除失败");
            }
            bishopClass.setBishopTeacherId(teacherId);
            List<BdClass> bishop = getAllCount(bishopClass);
            String message;
            // 判断是否还有主教班级
            if (bishop.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (BdClass bdClass : bishop) {
                    builder.append(bdClass.getClassName()).append(",");
                }
                message = "已有班级的教师不可删除，请更换班级对应的主教老师在删除! 班级为：";
                throw new CustomException(message.concat(builder.substring(0, builder.length() - 1)),
                        ResultCode.DELETE_TEACHER.code());
            }

            helpClass.setHelpTeacherId(teacherId);
            List<BdClass> help = getAllCount(helpClass);
            // 判断该老师是否还有辅助班级
            if (help.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (BdClass bdClass : help) {
                    builder.append(bdClass.getClassName()).append(",");
                }
                message = "已有班级的教师不可删除，请更换班级对应的辅助老师在删除! 班级为：";
                throw new CustomException(message.concat(builder.substring(0, builder.length() - 1)), ResultCode.DELETE_TEACHER.code());
            }
            teacherService.deleteBdTeacherById(teacherId);
        }
    }

    /**
     * 获取全部数据
     *
     * @param bdClass 根据条件进行筛选，获得数量
     * @return 班级数量
     */
    public List<BdClass> getAllCount(BdClass bdClass) {
        EntityWrapper<BdClass> wrapper = new EntityWrapper<>();
        wrapper.setEntity(bdClass);
        return classService.selectList(wrapper);
    }

    /**
     * 得到老师所教授的班级
     * @param teacherList 老师集合
     */
    public void getClasses(List<BdTeacherBO> teacherList) {
        List<Long> idList = new ArrayList<>(teacherList.size());
        for (BdTeacherBO teacherBO : teacherList){
            idList.add(teacherBO.getId());
        }
        List<BdTeacherBO> list = classService.getClassesByTeacher(idList);
        Map<Long, BdTeacherBO> map = new HashMap<>(list.size());
        for (BdTeacherBO bo : list){
            map.put(bo.getId(), bo);
        }

        for (BdTeacherBO teacherBO : teacherList){
            if (map.containsKey(teacherBO.getId())){
                BdTeacherBO bo = map.get(teacherBO.getId());
                teacherBO.setBishopClasses(bo.getBishopClasses());
            }
        }
    }

    /**
     * 更新老师
     * @param bdTeacherVO 老师信息
     * @return 是否更新成功
     */
    public Boolean updateBdTeacher(BdTeacherVO bdTeacherVO) {
        BdTeacherBO teacherBO = teacherService.selectBdTeacherById(bdTeacherVO.getId());
        if (teacherBO == null) {
            log.warn(String.format("老师id为%s未查询到", bdTeacherVO.getId()));
            throw new CustomException("未查询到该老师的信息");
        }
        if (!teacherBO.getPhone().equals(bdTeacherVO.getPhone())){
            if (teacherService.checkTeacherExist(bdTeacherVO.getPhone())){
                throw new CustomException("该老师的电话号码已存在，请重新输入");
            }
        }
        return teacherService.updateBdTeacher(bdTeacherVO);
    }
}
