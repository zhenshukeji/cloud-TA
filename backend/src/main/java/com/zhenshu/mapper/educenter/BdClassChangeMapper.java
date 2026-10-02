package com.zhenshu.mapper.educenter;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassChangeBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassChangeVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassTimeVO;
import com.zhenshu.mapper.domain.educenter.BdClassChange;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 上课临时更改记录 Mapper接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface BdClassChangeMapper extends BaseMapper<BdClassChange> {

    /***
     * 通过班级上课表id将调课中大于当前系统日期的调课进行取消
     * @param id 班级上课表id
     * @return boolean
     */
    boolean deleteClassChangeByClassTimeId(@Param("id") Long id);

    /***
     * 通过班级上课表id将调课表中上课开始时间大于当前系统日期的课查出
     * @param classTimeId 班级上课表id
     * @return boolean
     */
    List<BdClassChangeBO> selectByClassTimeIdAndAfterNow(long classTimeId);

    /**
     * 得到调课调到本周的记录
     *
     * @param vo 上课临时更改记录
     * @return List<BdClassChangeBO>
     */
    List<BdClassChangeBO> changeToThisWeek(BdClassChangeVO vo);


    /**
     * 得到本周调到其他周的记录
     *
     * @param vo 上课临时更改记录
     * @return List<BdClassChangeBO>
     */
    List<BdClassChangeBO> selectChangeToOther(BdClassChangeVO vo);


}
