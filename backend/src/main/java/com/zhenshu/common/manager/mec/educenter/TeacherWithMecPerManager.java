package com.zhenshu.common.manager.mec.educenter;

import com.baomidou.mybatisplus.plugins.Page;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMecPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdMecPersonnelVO;
import com.zhenshu.jianxue.mec.base.service.IBdMecPersonnelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/27 11:36
 * @desc 老师关联机构人员
 */
@Service
public class TeacherWithMecPerManager {

    @Resource
    private IBdMecPersonnelService mecPersonnelService;

    /**
     * 通过老师获得老师绑定的校区人员的信息
     * @param mecPerson 老师绑定的校区人员id
     * @return 老师绑定的校区人员信息
     */
    public BdMecPersonnelBO getMecPersonByTeacher(Long mecPerson){
        return mecPersonnelService.selectBdMecPersonnelById(mecPerson);
    }

    /**
     * 获得校区人员集合
     * @param mecPersonnel 机构人员查询信息
     * @param mecPersonIds 已使用过的机构人员ids
     * @return 校区人员集合
     */
    public Page<BdMecPersonnelBO> getAllMecPerson(BdMecPersonnelVO mecPersonnel, List<Long> mecPersonIds) {
        return mecPersonnelService.getMecPerByMecId(mecPersonnel, mecPersonIds);
    }

}
