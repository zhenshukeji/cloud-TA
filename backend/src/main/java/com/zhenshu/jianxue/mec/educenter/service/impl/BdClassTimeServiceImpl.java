package com.zhenshu.jianxue.mec.educenter.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.DayEnum;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassTimeBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassTimeVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.ClassTimeVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassTimeService;
import com.zhenshu.mapper.domain.educenter.BdClassTime;
import com.zhenshu.mapper.educenter.BdClassTimeMapper;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级上课时间 Service业务层处理
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Service
public class BdClassTimeServiceImpl extends ServiceImpl<BdClassTimeMapper, BdClassTime> implements IBdClassTimeService {

    @Resource
    BdClassTimeMapper classTimeMapper;


    /**
     * 新增班级上课时间
     *
     * @param bdClassTimeVO 班级上课时间
     * @return 结果
     */
    @Override
    public boolean insertBdClassTime(BdClassTimeVO bdClassTimeVO) {
        BdClassTime bdClassTime = VOToPO(bdClassTimeVO);
        bdClassTime.setCreateTime(DateUtils.getNowDate());
        bdClassTime.setCreateBy(SecurityUtils.getUserId().toString());
        return this.insert(bdClassTime);
    }

    /**
     * 修改班级上课时间
     *
     * @param bdClassTimeVO 班级上课时间
     * @return 结果
     */
    @Override
    public boolean updateById(BdClassTimeVO bdClassTimeVO) {
        BdClassTime bdClassTime = VOToPO(bdClassTimeVO);
        return this.updateById(bdClassTime);
    }

    /**
     * 删除班级上课时间 信息
     *
     * @param id 班级上课时间 ID
     * @return 结果
     */
    @Override
    public boolean deleteBdClassTimeById(Long id) {
        BdClassTime bdClassTime = new BdClassTime();
        bdClassTime.setIsDelete(Constants.DELETED);
        bdClassTime.setId(id);
        bdClassTime.setUpdateBy(SecurityUtils.getUserId().toString());
        bdClassTime.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(bdClassTime);
    }

    /**
     * 将数据库对象转换为BO对象
     *
     * @param bdClassTimes 数据库对象集合
     * @return List<BdClassTimeBO> bo对象集合
     */
    public List<BdClassTimeBO> copyList(List<BdClassTime> bdClassTimes) {
        List<BdClassTimeBO> resultList = new ArrayList<>();
        for (BdClassTime source : bdClassTimes) {
            BdClassTimeBO BdClassTimeBO = new BdClassTimeBO();
            BeanUtils.copyBeanProp(BdClassTimeBO, source);
            resultList.add(BdClassTimeBO);
        }
        return resultList;
    }

    /**
     * 得到所有的课时信息
     *
     * @param bdClassTimeVO 课时信息参数
     * @return List<BdClassTimeBO>
     */
    @Override
    public List<BdClassTimeBO> getAll(BdClassTimeVO bdClassTimeVO) {
        BdClassTime bdClassTime = VOToPO(bdClassTimeVO);
        EntityWrapper<BdClassTime> wrapper = new EntityWrapper<>();
        wrapper.setEntity(bdClassTime);
        List<BdClassTime> bdClassTimes = this.selectList(wrapper);
        return copyList(bdClassTimes);
    }

    /**
     * 得到某班级的所有上课时间
     *
     * @param classId 班级id
     * @return List<BdClassTimeBO>
     */
    @Override
    public List<BdClassTimeBO> getClassTimeByClassId(Long classId) {
        SysUser user = SecurityUtils.getSysUser();
        EntityWrapper<BdClassTime> wrapper = new EntityWrapper<>();
        wrapper.eq("mec_id", user.getMecId());
        wrapper.eq("campus_id", user.getCampusId());
        wrapper.eq("class_id", classId);
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        List<BdClassTime> list = this.selectList(wrapper);
        return copyList(list);
    }


    /**
     * 课表里的获取班级上课时间
     *
     * @param classTimeVO
     * @param weekDays 星期列表
     */
    @Override
    public List<BdClassTimeBO> selectByTimeTable(ClassTimeVO classTimeVO, List<Integer> weekDays) {
        classTimeVO.setWeekDayList(weekDays);
        return classTimeMapper.selectByTimeTable(classTimeVO);
    }


    /**
     * 根据班级列表id 查询课时信息列表
     *
     * @param classIds 班级ids
     * @return 根据班级id 分类的map信息
     */
    @Override
    public Map<Long, List<String>> selectListByClassIds(Long[] classIds) {
        EntityWrapper<BdClassTime> wrapper = new EntityWrapper<>();
        wrapper.in("class_id", classIds);
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        wrapper.orderBy("week_day");
        // 添加id倒序，为了避免出现同时间内出现数据自由排序的情况
        wrapper.orderBy("id", false);
        List<BdClassTime> classTimeList = this.selectList(wrapper);
        // 根据班级id 进行分类
        Map<Long, List<String>> classTimeMap = new HashMap<>();
        for (BdClassTime classTime : classTimeList) {
            // 返回格式化数据给前端
            String day = DayEnum.getDay(classTime.getWeekDay());
            String formatTime = "周" + day + " " + classTime.getClsStartTime() + "-" + classTime.getClsEndTime();
            // 根据班级id 进行分组
            if (classTimeMap.containsKey(classTime.getClassId())) {
                classTimeMap.get(classTime.getClassId()).add(formatTime);
            } else {
                List<String> list = new ArrayList<>();
                list.add(formatTime);
                classTimeMap.put(classTime.getClassId(), list);
            }
        }
        return classTimeMap;
    }


    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdClassTimeVO 入参VO
     * @return bdCallRecord
     */
    public BdClassTime VOToPO(BdClassTimeVO bdClassTimeVO) {
        BdClassTime bdClassTime = new BdClassTime();
        BeanUtils.copyBeanProp(bdClassTime, bdClassTimeVO);
        SysUser user = SecurityUtils.getSysUser();
        bdClassTime.setCampusId(user.getCampusId());
        bdClassTime.setMecId(user.getMecId());
        bdClassTime.setIsDelete(Constants.NOT_DELETED);
        return bdClassTime;
    }

}
