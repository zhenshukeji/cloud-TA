package com.zhenshu.mapper.educenter;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.mapper.domain.educenter.BdTeacher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 老师Mapper接口
 *
 * @author jing
 * @date 2020-09-25
 */
public interface BdTeacherMapper extends BaseMapper<BdTeacher> {

    /**
     * 查出所有老师关联的校区人员id
     *
     * @param mecId    机构id
     * @param campusId 校区id
     * @return 所有老师关联的校区人员id
     */
    List<Long> selectAllCampusPerson(@Param("mecId") Long mecId, @Param("campusId") Long campusId);

    /**
     * 查出所有老师关联的校区人员id
     *
     * @param mecId    机构id
     * @param campusId 校区id
     * @return 所有老师关联的校区人员id
     */
    List<Long> selectAllMecPerson(@Param("mecId") Long mecId, @Param("campusId") Long campusId);
}
