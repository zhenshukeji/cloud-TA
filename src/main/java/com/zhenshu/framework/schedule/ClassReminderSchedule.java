package com.zhenshu.framework.schedule;

import com.zhenshu.common.enums.DayEnum;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.cloud.mec.educenter.domain.bo.BdClassTimeBO;
import com.zhenshu.cloud.mec.educenter.domain.bo.BdStudentClassBO;
import com.zhenshu.cloud.mec.educenter.domain.vo.ClassTimeVO;
import com.zhenshu.cloud.mec.educenter.manager.TemplateManager;
import com.zhenshu.cloud.mec.educenter.manager.TimeTableDataManager;
import com.zhenshu.cloud.mec.educenter.service.IBdStudentClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuxi
 * @version 1.0
 * @date 2021/1/8 10:47
 * @desc 上课提醒通知
 */
@Component
public class ClassReminderSchedule {

    private final Logger logger = LoggerFactory.getLogger(ClassReminderSchedule.class);

    @Resource
    private TimeTableDataManager timeTableDataManager;

    @Resource
    private TemplateManager templateManager;

    @Resource
    private IBdStudentClassService studentClassService;

    /**
     * 发送上课提醒 corn格式为 秒 分 时 日 月 年
     */
    @Scheduled(cron = "0 0 6 * * ? ")
    public void classReminder() {
        logger.info("发送上课提醒开始");
        String date = DateUtils.getDate();
        List<BdClassTimeBO> classTime = timeTableDataManager.getOneDayClass(date, new ClassTimeVO());
        // 循环班级
        for (BdClassTimeBO classTimeBO : classTime) {
            Long classId = classTimeBO.getClassId();
            String clsStartTime = classTimeBO.getClsStartTime().substring(11,16);
            String clsEndTime = classTimeBO.getClsEndTime().substring(11,16);
            Integer weekDay = DateUtils.getTodayOfWeek();
            String time = String.format("%s 星期%s %s-%s", date, DayEnum.getDay(weekDay), clsStartTime, clsEndTime);
            // 查询该班级上课的学生（去掉请假的学生）
            List<BdStudentClassBO> list = studentClassService.getStudentByClassIdFilterAskLeave(classId, classTimeBO.getStartInWeek().concat(":00"), classTimeBO.getEndInWeek().concat(":00"));
            for (BdStudentClassBO studentClassBO : list) {
                if (studentClassBO.getWechatSubscribeOpenid() == null) {
                    continue;
                }
                templateManager.sendClassReminderTemplateMsg(studentClassBO, time);
            }
        }
        logger.info("发送上课提醒结束");
    }
}
