package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassChangeBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassChangeVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassTimeVO;
import com.zhenshu.mapper.domain.educenter.BdClassChange;

import java.util.List;

/**
 * 上课临时更改记录 Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdClassChangeService extends IService<BdClassChange> {

    /**
     * 新增上课临时更改记录
     *
     * @param bdClassChangeVO 上课临时更改记录
     * @return 结果
     */
    boolean insertBdClassChange(BdClassChangeVO bdClassChangeVO);

    /**
     * 修改上课临时更改记录
     *
     * @param bdClassChangeVO 上课临时更改记录
     * @return 结果
     */
    boolean updateBdClassChange(BdClassChangeVO bdClassChangeVO);

    /**
     * 删除上课临时更改记录 信息
     *
     * @param id 上课临时更改记录 ID
     * @return 结果
     */
    boolean deleteBdClassChangeById(Long id);

    /**
     * 得到某一时间段内的课表
     *
     * @param vo 上课临时更改记录
     * @return List<BdClassChangeBO>
     */
    List<BdClassChangeBO> changeToThisWeek(BdClassChangeVO vo);


    /**
     * 查询本周调课调到其他周的记录
     * @param vo 调课信息
     * @return 本周调课调到其他周的记录
     */
    List<BdClassChangeBO> selectChangeToOther(BdClassChangeVO vo);

    /***
     * 通过班级上课表id将调课中大于当前系统日期的调课进行取消
     * @param classTimeId 班级上课表id
     */
    void deleteClassChangeByClassTimeId(long classTimeId);

}
