package com.zhenshu.common.manager.mec.educenter;

import com.baomidou.mybatisplus.plugins.Page;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusPersonnelVO;
import com.zhenshu.jianxue.mec.base.service.IBdCampusPersonnelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/22 16:58
 * @desc 老师关联校区人员
 */
@Service
public class TeacherWithCampusPerManager {

    @Resource
    private IBdCampusPersonnelService campusPersonnelService;

    /**
     * 通过老师获得老师绑定的校区人员的信息
     * @param campusPerson 老师绑定的校区人员id
     * @return 老师绑定的校区人员信息
     */
    public BdCampusPersonnelBO getCampusPersonByTeacher(Long campusPerson){
       return campusPersonnelService.selectBdCampusPersonnelById(campusPerson);
    }

    /**
     * 获得校区人员集合
     * @param campusPersonnelVO 校区人员查询信息
     * @param campusPersonIds 已使用过的校区人员ids
     * @return 校区人员集合
     */
    public Page<BdCampusPersonnelBO> getAllCampusPerson(BdCampusPersonnelVO campusPersonnelVO, List<Long> campusPersonIds) {
        return campusPersonnelService.getCamPerByCampusId(campusPersonnelVO, campusPersonIds);
    }
}
