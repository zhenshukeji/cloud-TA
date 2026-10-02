package com.zhenshu.jianxue.mec.educenter.manager;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.common.utils.page.PageUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCourseBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCoursePurchaseBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCoursePurchaseVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdCoursePurchaseService;
import com.zhenshu.jianxue.mec.educenter.service.IBdCourseService;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.mapper.domain.educenter.BdCoursePurchase;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuxi
 * @version 1.0
 * @desc 课程购买记录调用其他表
 * @date
 */
@Service
public class CoursePurchaseManager {

    @Resource
    private IBdCourseService bdCourseService;

    @Resource
    private IBdCoursePurchaseService coursePurchaseService;


    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdCoursePurchaseVO 入参VO
     * @return bdCallRecord
     */
    public BdCoursePurchase voToPo(BdCoursePurchaseVO bdCoursePurchaseVO) {
        BdCoursePurchase bdCoursePurchase = new BdCoursePurchase();
        BeanUtils.copyBeanProp(bdCoursePurchase, bdCoursePurchaseVO);
        SysUser user = SecurityUtils.getSysUser();
        bdCoursePurchase.setCampusId(user.getCampusId());
        bdCoursePurchase.setMecId(user.getMecId());
        bdCoursePurchase.setIsDelete(Constants.NOT_DELETED);
        return bdCoursePurchase;
    }

    /**
     * 无分页无参数列表
     */
    public List<BdCoursePurchaseBO> getAll(BdCoursePurchaseVO bdCoursePurchaseVO) {
        BdCoursePurchase bdCoursePurchase = voToPo(bdCoursePurchaseVO);
        EntityWrapper<BdCoursePurchase> wrapper = new EntityWrapper<>();
        wrapper.setEntity(bdCoursePurchase);
        return copyList(coursePurchaseService.selectList(wrapper));
    }


    /**
     * 进行分页
     *
     * @param bdCoursePurchase   数据库对象 设置查询参数
     * @param bdCoursePurchaseVO 入参对象
     * @param wrapper            查询参数
     * @return Page<BdCoursePurchaseBO>分页好的出参
     */
    public Page<BdCoursePurchaseBO> getPage(BdCoursePurchase bdCoursePurchase, BdCoursePurchaseVO bdCoursePurchaseVO, EntityWrapper<BdCoursePurchase> wrapper) {
        wrapper.setEntity(bdCoursePurchase);
        Page<BdCoursePurchase> page = coursePurchaseService.selectPage(new Page<>(bdCoursePurchaseVO.getPageNum(), bdCoursePurchaseVO.getPageSize()), wrapper);
        List<BdCoursePurchaseBO> resultList = copyList(page.getRecords());

        // 重新组装page
        return new PageUtils<BdCoursePurchaseBO>().getPage(resultList, coursePurchaseService.selectCount(wrapper));
    }


    /**
     * 将数据库对象转换为BO对象
     *
     * @param bdCoursePurchases 数据库对象集合
     * @return List<BdCoursePurchaseBO> bo对象集合
     */
    public List<BdCoursePurchaseBO> copyList(List<BdCoursePurchase> bdCoursePurchases) {
        List<BdCoursePurchaseBO> resultList = new ArrayList<>();
        for (BdCoursePurchase source : bdCoursePurchases) {
            BdCoursePurchaseBO bdCoursePurchaseBO = new BdCoursePurchaseBO();
            BeanUtils.copyBeanProp(bdCoursePurchaseBO, source);
            //查找课程名称
            BdCourseBO courseBO = bdCourseService.selectBdCourseById(bdCoursePurchaseBO.getCourseId());
            bdCoursePurchaseBO.setCourseName(courseBO.getCourseName());
            resultList.add(bdCoursePurchaseBO);
        }
        return resultList;
    }

}
