package com.zhenshu.jianxue.mec.educenter.controller;

import com.zhenshu.common.annotation.Response;
import com.zhenshu.jianxue.mec.educenter.domain.bo.EnrollmentStatusBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.EnrollmentStatusVO;
import com.zhenshu.jianxue.mec.educenter.service.impl.EnrollmentStatusService;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.page.TableDataInfo;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 报读情况
 *
 * @author yuxi
 * @date 2020-09-28
 */
@Api(value = "报读情况", tags = "报读情况接口")
@RestController
@RequestMapping("/educenter/enrollment")
@PreAuthorize("@ss.hasPermi('educenter:enrollment:list')")
public class BdEnrollmentStatusController extends BaseController {

    @Resource
    private EnrollmentStatusService enrollmentStatusService;

    @ApiOperation(value = "报读情况", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "enrollmentVO", value = "封装类", dataType = "EnrollmentVO")
    @GetMapping("/list")
    @Response
    public TableDataInfo list(EnrollmentStatusVO enrollmentVO) {
        startPage();
        List<EnrollmentStatusBO> list = enrollmentStatusService.selectEnrollmentList(enrollmentVO);
        if (list == null || list.isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(list);
    }

}
