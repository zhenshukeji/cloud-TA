package com.zhenshu.cloud.platform.statistics.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.cloud.platform.statistics.domain.vo.PlatformPipelineExportVO;
import com.zhenshu.mapper.domain.platform.BdPlatformAccountTurnover;
import com.zhenshu.cloud.platform.statistics.domain.bo.BdPlatformAccountTurnoverBO;
import com.zhenshu.cloud.platform.statistics.domain.vo.BdPlatformAccountTurnoverVO;

import java.util.List;

/**
 *
 * @author yuxi
 * @date 2020-11-23
 * @desc 平台账户流水 Service接口
 */
public interface IBdPlatformAccountTurnoverService extends IService<BdPlatformAccountTurnover> {

    /**
     * 使用pagehelper的分页
     * @param bdPlatformAccountTurnoverVO 模糊查询
     * @return 平台账户流水 集合
     */
    List<BdPlatformAccountTurnoverBO> listByPageHelper(BdPlatformAccountTurnoverVO bdPlatformAccountTurnoverVO);

    /**
     * 平台流水按月份导出数据
     *
     * @param pipelineExportVO  流水导出封装
     * @return 结果
     * @author tyh
     */
    List<BdPlatformAccountTurnoverBO> exportMonths(PlatformPipelineExportVO pipelineExportVO);

    /**
     * 平台流水按季度导出数据
     *
     * @param pipelineExportVO 流水导出封装
     * @return 结果
     */
    List<BdPlatformAccountTurnoverBO> exportQuarterly(PlatformPipelineExportVO pipelineExportVO);

}
