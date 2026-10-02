package com.zhenshu.jianxue.mec.statistics.controller;

import com.zhenshu.common.manager.mec.statistics.ClassDetailManager;
import com.zhenshu.jianxue.mec.statistics.domain.bo.ClassDetailDataBO;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.page.TableDataInfo;
import com.zhenshu.jianxue.mec.statistics.domain.vo.BdClassDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jing
 * @version 1.0
 * @desc 班级明细报表
 * @date 2020/11/12 0012 16:26
 **/
@RestController
@Api(value = "班级明细报表 ", tags = "班级明细报表 接口")
@RequestMapping("/statistics/class")
@PreAuthorize("@ss.hasPermi('statistics:class:list')")
public class ClassDetailController extends BaseController {
    @Resource
    private ClassDetailManager classDetailManager;


    @ApiOperation(value = "班级明细报表")
    @GetMapping("/list")
    public TableDataInfo getClassDetail(BdClassDetailVO bdClassDetailVO) {
        startPage();
        List<ClassDetailDataBO> list = classDetailManager.getClassData(bdClassDetailVO);
        if (list == null || list.isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(list);
    }


}
