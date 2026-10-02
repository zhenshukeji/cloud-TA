package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTeacherBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdTeacherVO;
import com.zhenshu.mapper.domain.educenter.BdTeacher;

import java.util.List;

/**
 * 老师Service接口
 *
 * @author jing
 * @date 2020-09-25
 */
public interface IBdTeacherService extends IService<BdTeacher> {

    /**
     * 查询老师
     *
     * @param id 老师ID
     * @return 老师
     */
    BdTeacherBO selectBdTeacherById(Long id);

    /**
     * 可以查询已删除的老师的信息
     *
     * @param id 老师ID
     * @return 老师信息
     */
    BdTeacherBO selectTeacherContainIsDeletedById(Long id);

    /**
     * 查询老师列表
     *
     * @param bdTeacherVO 老师
     * @return 老师集合
     */
    Page<BdTeacherBO> selectBdTeacherList(BdTeacherVO bdTeacherVO);

    /**
     * 新增老师
     *
     * @param bdTeacherVO 老师
     * @return 结果
     */
    boolean insertBdTeacher(BdTeacherVO bdTeacherVO);

    /**
     * 修改老师
     *
     * @param bdTeacherVO 老师
     * @return 结果
     */
    boolean updateBdTeacher(BdTeacherVO bdTeacherVO);

    /**
     * 删除老师信息
     *
     * @param id 老师ID
     * @return 结果
     */
    boolean deleteBdTeacherById(Long id);

    /**
     * 修改老师已授班级数
     *
     * @param teacherId 老师id
     * @param type 是增加已授班级数，还是减少已授班级数
     * @return boolean
     */
    boolean updateTeacherTaughtCls(Long teacherId, String type);

    /**
     * 查出所有老师关联的校区人员id
     * @return 所有老师关联的校区人员id
     */
    List<Long> selectAllCampusPerson();

    /**
     * 查出所有老师关联的机构人员id
     * @return 所有老师关联的机构人员id
     */
    List<Long> selectAllMecPerson();

    /**
     * 通过机构人员得到老师id
     * @param mecPerson 机构人员id
     * @return 老师
     */
    BdTeacherBO getTeacherByMecPerson(Long mecPerson, Integer personType);

    /**
     * 通过校区人员得到老师id
     * @param campusPerson 校区人员id
     * @return 老师
     */
    BdTeacherBO getTeacherByCampusPerson(Long campusPerson, Integer personType);

    /**
     * 校验该电话的老师是否存在
     * @param phone 电话
     * @return 是否存在
     */
    boolean checkTeacherExist(String phone);

}
