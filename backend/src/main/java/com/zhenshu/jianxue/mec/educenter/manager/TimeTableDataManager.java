package com.zhenshu.jianxue.mec.educenter.manager;

import com.zhenshu.common.enums.educenter.SchoolTableTimeEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassChangeBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassTimeBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassChangeVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassTimeVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.ClassTimeVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassChangeService;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassTimeService;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

/**
 * @author jing
 * @version 1.0
 * @desc 课表
 * @date 2021/1/19 0019 14:12
 **/
@Service
public class TimeTableDataManager {
    @Resource
    private IBdClassChangeService classChangeService;
    @Resource
    private IBdClassTimeService classTimeService;
    private Log log = LogFactory.getLog(TimeTableDataManager.class);


    /**
     * 查询课表信息
     *
     * @param classTimeVO 课表信息 班级名称 课程名称 那一周
     * @return 课表信息
     */
    public List<BdClassTimeBO> selectAll(ClassTimeVO classTimeVO) {
        Map<String, String> time = DateUtils.getWeekDayByDifferentNum(classTimeVO.getNum());
        Date before = DateUtils.parseDate(time.get("before"));
        Date after = DateUtils.parseDate(time.get("after"));
        SysUser user = SecurityUtils.getSysUser();
        classTimeVO.setMecId(user.getMecId());
        classTimeVO.setCampusId(user.getCampusId());

        return getTimeTable(before, after, classTimeVO, null);
    }

    /**
     * 获得某天的课表
     *
     * @param date        时间 格式为年月日
     * @param classTimeVO 班级名称
     * @return 某天课表
     */
    public List<BdClassTimeBO> getOneDayClass(String date, ClassTimeVO classTimeVO) {
        Map<String, String> time = DateUtils.getDayTime(date);
        Date before = DateUtils.parseDate(time.get("startTimeDay"));
        Date after = DateUtils.parseDate(time.get("endTimeDay"));
        return getTimeTable(before, after, classTimeVO, null);
    }

    /**
     * 校验该时间段是否已存在课次
     *
     * @param bdClassTimeVO 班级名称
     * @return 某天课表
     */
    public boolean checkTime(BdClassTimeVO bdClassTimeVO) {
        SysUser user = SecurityUtils.getSysUser();
        bdClassTimeVO.setMecId(user.getMecId());
        bdClassTimeVO.setCampusId(user.getCampusId());
        ClassTimeVO classTimeVO = new ClassTimeVO();
        BeanUtils.copyBeanProp(classTimeVO, bdClassTimeVO);
        try {
            Date before = DateUtils.parseDate(bdClassTimeVO.getClsStartTime(), DateUtils.YYYY_MM_DD_HH_MM_SS);
            Date after = DateUtils.parseDate(bdClassTimeVO.getClsEndTime(), DateUtils.YYYY_MM_DD_HH_MM_SS);
            List<BdClassTimeBO> timeBOList = getTimeTable(before, after, classTimeVO, classTimeVO.getClassId());
            for (BdClassTimeBO classTimeBO : timeBOList) {
                // 排除冲突的是本课次的时间，只要idType 和id 有一个不对，就说明不是同一个课次
                boolean idTypeFlag = classTimeBO.getIdType().equals(bdClassTimeVO.getIdType().intValue());
                boolean idFlag = classTimeBO.getId().equals(bdClassTimeVO.getId());
                if (idTypeFlag && idFlag) {
                    continue;
                } else {
                    long clsStartTime = DateUtils.parseDate(bdClassTimeVO.getClsStartTime()).getTime();
                    long clsEndTime = DateUtils.parseDate(bdClassTimeVO.getClsEndTime()).getTime();
                    long boStartTime = DateUtils.parseDate(classTimeBO.getStartInWeek() + ":00").getTime();
                    long boEndTime = DateUtils.parseDate(classTimeBO.getEndInWeek() + ":00").getTime();

                    // 这节课开始时间 < 课次开始时间 < 这节课结束时间
                    boolean flag1 = boStartTime < clsStartTime && clsStartTime < boEndTime;
                    // 这节课开始时间 > 课次开始时间 && 这节课结束时间 < 课次结束时间
                    boolean flag2 = boStartTime > clsStartTime && boEndTime < clsEndTime;
                    // 这节课开始时间 < 这节课结束时间 < 这节课结束时间
                    boolean flag3 = boStartTime < clsEndTime && clsEndTime < boEndTime;
                    // 这节课开始时间 < 课次开始时间 && 这节课结束时间 > 课次结束时间
                    boolean flag4 = boStartTime < clsStartTime && boEndTime > clsEndTime;
                    // 起止时间完全相同
                    boolean flag5 = boStartTime == clsStartTime && boEndTime == clsEndTime;
                    if (flag1 || flag2 || flag3 || flag4 || flag5) {
                        return true;
                    }
                }
            }
            return false;
        } catch (ParseException e) {
            log.error("时间解析出现异常", e);
            throw new CustomException("时间解析出现异常");
        }
    }

    /**
     * 获取当前机构某个时间范围内的课表
     *
     * @param before  之前
     * @param after   之后
     * @param classId 班级id
     */
    private List<BdClassTimeBO> getTimeTable(Date before, Date after, ClassTimeVO classTimeVO, Long classId) {
        BdClassChangeVO changeVO = new BdClassChangeVO();
        BeanUtils.copyBeanProp(changeVO, classTimeVO);
        changeVO.setClsStartTime(before);
        changeVO.setClsEndTime(after);
        changeVO.setStartDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, before));
        changeVO.setEndDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, after));
        changeVO.setClassId(classId);

        // 1.获得该个时间段的新增课次记录，和调到这个时间段的课次, 添加到返回列表
        List<BdClassTimeBO> resultList = new ArrayList<>();
        List<BdClassChangeBO> changeToThisWeek = classChangeService.changeToThisWeek(changeVO);
        addClassChange(resultList, changeToThisWeek);

        // 2、获取该时间段的取消课次，和调到其他时间段的课次
        Map<String, List<BdClassChangeBO>> dayChangeMap = getNeedDelMap(changeVO);

        // 3、获取某个时间范围内的上课时间表
        List<String> dates = DateUtils.findDates(before, after);
        // 4、排除掉已取消的和调到其他周的课次，添加到返回数据列表
        Map<Integer, List<BdClassTimeBO>> weekClsTimeMap = getClassTimeList(before, after, dates, classId, classTimeVO);
        addClassTime(before, after, resultList, dayChangeMap, dates, weekClsTimeMap);

        // 根据weekday进行排序
        Comparator<BdClassTimeBO> weekDayComparator = Comparator.comparingInt(BdClassTimeBO::getWeekDay);

        resultList.sort(weekDayComparator);
        return resultList;
    }

    /**
     * 把班级上课时间加入返回，排除掉已调课的和已取消的课次
     */
    private void addClassTime(Date before, Date after, List<BdClassTimeBO> resultList, Map<String, List<BdClassChangeBO>> dayChangeMap, List<String> dates, Map<Integer, List<BdClassTimeBO>> weekClsTimeMap) {
        String startDate = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, before);
        String endDate = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, after);
        // 4.3 遍历范围内的每一天，获取该天的调课记录和取消课记录，然后获取每一天对应是星期几，排除掉已取消的和已调课的
        /*
         * 这里根据取消记录和调课记录来排除上课记录的思路是，假设排除的是2021-1-18的课程，1-18是星期一。
         * 1、班级上课时间里存储的是星期一的课程，如果直接根据取消课次里存储的班级上课时间id来比对的话，会把所有星期一的课程取消。
         * 2、所以我们换一个思路，遍历时间到2021-1-18日，然后根据日期的key，去拿对应的取消课次记录，然后再去比较对应的上课时间id，如果一致，取消当天的那节课。
         * 3、因为只是在1-18号进行的比较，其他星期一，比如1月25日，查不到取消课次记录，那么自然就不会被取消。
         * 调课记录同理。
         */
        for (String date : dates) {
            // 判断今天是星期几
            int day = DateUtils.getWeekDay(date);
            System.out.println(date);
            System.out.println(day);
            // 获取当天的调课记录和取消课次的列表，统计需要去除的班级上课时间id
            List<BdClassChangeBO> changeBOList = dayChangeMap.get(date);
            HashSet<Long> needRemoveIds = new HashSet<>();
            if (changeBOList != null && !changeBOList.isEmpty()) {
                for (BdClassChangeBO changeBO : changeBOList) {
                    needRemoveIds.add(changeBO.getClassTimeId());
                }
            }
            // 获取当天的课程列表
            List<BdClassTimeBO> clsTimeList = weekClsTimeMap.get(day);
            // 如果没有上课时间数据，那么跳过
            if (clsTimeList == null || clsTimeList.isEmpty()) {
                continue;
            }
            for (BdClassTimeBO classTimeBO : clsTimeList) {
                BdClassTimeBO data = new BdClassTimeBO();
                BeanUtils.copyBeanProp(data, classTimeBO);

                data.setIdType(SchoolTableTimeEnum.CLASSTIME_TYPE.getCode());
                data.setStartInWeek(date + " " + data.getClsStartTime());
                data.setEndInWeek(date + " " + data.getClsEndTime());
                data.setClsStartTime(date + " " + data.getClsStartTime());
                data.setClsEndTime(date + " " + data.getClsEndTime());
                // 如果该课次已取消, 那么不加入返回列表
                if (needRemoveIds.contains(data.getId())) {
                    continue;
                }
                // 然后再判断时间范围
                // 需要对开始时间和结束时间特殊处理
                if (startDate.equals(date) || endDate.equals(date)) {
                    // 将班级上课的小时转换为日期-小时-分
                    long clsStartTime = DateUtils.parseDate(data.getClsStartTime() + ":00").getTime();
                    long clsEndTime = DateUtils.parseDate(data.getClsEndTime() + ":00").getTime();
                    long startTimeLong = before.getTime();
                    long endTimeLong = after.getTime();

                    // 查询开始时间 < 课程开始时间 < 查询结束时间
                    boolean flag1 = startTimeLong < clsStartTime && clsStartTime < endTimeLong;
                    // 课程开始时间 < 查询开始时间 &&  课程结束时间 > 查询结束时间
                    boolean flag2 = clsStartTime < startTimeLong && clsEndTime > startTimeLong;
                    // 查询开始时间 < 课程结束时间 < 查询结束时间
                    boolean flag3 = startTimeLong < clsEndTime && clsEndTime < endTimeLong;
                    // 这节课开始时间 < 课次开始时间 && 这节课结束时间 > 课次结束时间
                    boolean flag4 = startTimeLong < clsStartTime && endTimeLong > clsEndTime;

                    // 仅当课程完全落在查询窗口之外时才跳过；flag4（课程完全覆盖窗口）应被包含，
                    // 与中间日（全部包含）保持一致，避免跨整天课次被误剔
                    if (!flag1 && !flag2 && !flag3) {
                        continue;
                    }
                }
                resultList.add(data);
            }
        }
    }

    /**
     * 获取需要取消课次，和调出去的课次，按照天进行分类
     *
     * @param changeVO 传参
     */
    @NotNull
    private Map<String, List<BdClassChangeBO>> getNeedDelMap(BdClassChangeVO changeVO) {
        List<BdClassChangeBO> needDelList = classChangeService.selectChangeToOther(changeVO);
        Map<String, List<BdClassChangeBO>> dayChangeMap = new HashMap<>(needDelList.size());
        for (BdClassChangeBO changeBO : needDelList) {
            // 这里是因为上课开始和结束时间只会再同一天，所以拿开始时间或者结束时间都可以。
            String date = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, changeBO.getOriginalStartTime());
            if (dayChangeMap.containsKey(date)) {
                dayChangeMap.get(date).add(changeBO);
            } else {
                ArrayList<BdClassChangeBO> list = new ArrayList<>();
                list.add(changeBO);
                dayChangeMap.put(date, list);
            }
        }
        return dayChangeMap;
    }


    /**
     * 查询班级上课时间列表
     */
    private Map<Integer, List<BdClassTimeBO>> getClassTimeList(Date before, Date after, List<String> dates, Long classId, ClassTimeVO classTimeVO) {
        List<Integer> weekDayList = getWeekDayList(before, after, dates);
        classTimeVO.setClassId(classId);
        List<BdClassTimeBO> classTimeList = classTimeService.selectByTimeTable(classTimeVO, weekDayList);
        Map<Integer, List<BdClassTimeBO>> weekClsTimeMap = new HashMap<>(classTimeList.size());
        for (BdClassTimeBO clsTime : classTimeList) {
            if (weekClsTimeMap.containsKey(clsTime.getWeekDay())) {
                weekClsTimeMap.get(clsTime.getWeekDay()).add(clsTime);
            } else {
                ArrayList<BdClassTimeBO> list = new ArrayList<>();
                list.add(clsTime);
                weekClsTimeMap.put(clsTime.getWeekDay(), list);
            }
        }
        return weekClsTimeMap;
    }


    /**
     * 获取这段时间涉及到的周几，比如如果是2020-1-19 2020-1-19 ，那么就只需要查周一的数据。 如果是2020-1-19 到2020-1-20 ，就需要查周一和周二的数据
     * 如果是 2020-1-19 到 2020-2-19 ，那么一周的配置都需要查询
     * 主要是避免查询所有的数据，只查对应天的班级上课数据
     *
     * @param before 开始时间
     * @param after  结束时间
     */
    private List<Integer> getWeekDayList(Date before, Date after, List<String> dates) {
        try {
            List<Integer> weekDays = new ArrayList<>();
            // 计算时间差
            int days = DateUtils.daysBetween(before, after);
            int sumWeekNum = 7;
            String beforeStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, before);
            String afterStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, after);
            // 如果是开始时间和结束时间是同一天
            if (beforeStr.equals(afterStr)) {
                int week = DateUtils.getDayOfWeek(before);
                weekDays.add(week);
                // 如果时间差小于一星期
            } else if (days < sumWeekNum) {
                // 把涉及到的每个星期几都放进去
                for (String date : dates) {
                    Date day = DateUtils.parseDate(date, DateUtils.YYYY_MM_DD);
                    int week = DateUtils.getDayOfWeek(day);
                    weekDays.add(week);
                }
            } else {
                // 星期一到星期天都要查
                for (int i = 1; i <= sumWeekNum; i++) {
                    weekDays.add(i);
                }
            }
            return weekDays;
        } catch (ParseException e) {
            log.error("计算时间差出现异常%", e);
            throw new CustomException("计算时间差时出现异常");
        }
    }


    /**
     * 新增课次和调到该周的放进去
     */
    private void addClassChange(List<BdClassTimeBO> resultList, List<BdClassChangeBO> changeToThisWeek) {
        // 获得颜色，主教老师名称，辅助老师名称，班级人数
        for (BdClassChangeBO classChange : changeToThisWeek) {
            BdClassTimeBO bdClassTimeBO = new BdClassTimeBO();
            BeanUtils.copyBeanProp(bdClassTimeBO, classChange);
            bdClassTimeBO.setWeekDay(DateUtils.getDayOfWeek(classChange.getClsStartTime()));
            bdClassTimeBO.setIdType(SchoolTableTimeEnum.CHANGE_TYPE.getCode());
            bdClassTimeBO.setStartInWeek(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM, classChange.getClsStartTime()));
            bdClassTimeBO.setEndInWeek(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM, classChange.getClsEndTime()));
            bdClassTimeBO.setClsStartTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM, classChange.getClsStartTime()));
            bdClassTimeBO.setClsEndTime(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM, classChange.getClsEndTime()));
            resultList.add(bdClassTimeBO);
        }
    }


}
