package com.zhenshu.zhenxueyou.platform.statistics.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.zhenxueyou.platform.statistics.domain.vo.PlatformPipelineExportVO;
import com.zhenshu.mapper.domain.platform.BdPlatformAccountTurnover;
import com.zhenshu.mapper.platform.BdPlatformAccountTurnoverMapper;
import com.zhenshu.zhenxueyou.platform.statistics.domain.bo.BdPlatformAccountTurnoverBO;
import com.zhenshu.zhenxueyou.platform.statistics.domain.vo.BdPlatformAccountTurnoverVO;
import com.zhenshu.zhenxueyou.platform.statistics.service.IBdPlatformAccountTurnoverService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @desc 平台账户流水 Service业务层处理
 *
 * @author yuxi
 * @date 2020-11-23
 */
@Service
public class BdPlatformAccountTurnoverServiceImpl extends ServiceImpl<BdPlatformAccountTurnoverMapper, BdPlatformAccountTurnover> implements IBdPlatformAccountTurnoverService {

    @Resource
    private BdPlatformAccountTurnoverMapper bdPlatformAccountTurnoverMapper;

    /**
     * 使用page helper的分页
     * @param bdPlatformAccountTurnoverVO 模糊查询
     * @return 平台账户流水 集合
     */
    @Override
    public List<BdPlatformAccountTurnoverBO> listByPageHelper(BdPlatformAccountTurnoverVO bdPlatformAccountTurnoverVO){
        return bdPlatformAccountTurnoverMapper.queryByPageHelper(bdPlatformAccountTurnoverVO);
    }

    /**
     * 平台流水按月份导出数据
     *
     * @param pipelineExportVO 流水导出封装
     * @return 结果
     * @author tyh
     */
    @Override
    public List<BdPlatformAccountTurnoverBO> exportMonths(PlatformPipelineExportVO pipelineExportVO) {
        return bdPlatformAccountTurnoverMapper.exportMonths(pipelineExportVO);
    }

    /**
     * 平台流水按季度导出数据
     *
     * @param pipelineExportVO 流水导出封装
     * @return 结果
     */
    @Override
    public List<BdPlatformAccountTurnoverBO> exportQuarterly(PlatformPipelineExportVO pipelineExportVO) {
        return bdPlatformAccountTurnoverMapper.exportQuarterly(pipelineExportVO);
    }

}
