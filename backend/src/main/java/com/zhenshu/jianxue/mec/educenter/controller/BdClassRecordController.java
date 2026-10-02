package com.zhenshu.jianxue.mec.educenter.controller;

import com.zhenshu.common.annotation.Response;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCallRecordBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassRecordBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCallRecordVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassRecordVO;
import com.zhenshu.jianxue.mec.educenter.manager.ClassRecordManager;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassRecordService;
import com.zhenshu.framework.aspectj.lang.annotation.Log;
import com.zhenshu.framework.aspectj.lang.enums.BusinessType;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.framework.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 上课记录Controller
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Api(value = "上课记录", tags = "上课记录接口")
@RestController
@RequestMapping("/educenter/record")
@PreAuthorize("@ss.hasPermi('educenter:record:list')")
public class BdClassRecordController extends BaseController {

    @Resource
    private IBdClassRecordService bdClassRecordService;

    @Resource
    private ClassRecordManager classRecordManager;

    @ApiOperation(value = "查询上课记录列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdClassRecordVO", value = "上课记录封装类", dataType = "BdClassRecordVO")
    @GetMapping("/list")
    @Response
    public TableDataInfo list(BdClassRecordVO bdClassRecordVO) {
        startPage();
        List<BdClassRecordBO> list = bdClassRecordService.selectBdClassRecordList(bdClassRecordVO);
        if (list == null || list.isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(list);
    }


    @ApiOperation(value = "获取上课记录详细信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "id", value = "获取上课记录详细信息", dataType = "Long")
    @GetMapping(value = "/{id}")
    @Response
    public Result<BdClassRecordBO> getInfo(@PathVariable("id") Long id) {
        return new Result<BdClassRecordBO>().success(classRecordManager.selectBdClassRecordDetailById(id));
    }


    @ApiOperation(value = "获取该学员的点名信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "id", value = "消课记录id", dataType = "Long")
    @GetMapping(value = "/getCallRecordInfo/{id}")
    @Response
    public Result<BdCallRecordBO> getCallRecordInfo(@PathVariable("id") Long id) {
        return new Result<BdCallRecordBO>().success(classRecordManager.getCallRecordInfo(id));
    }


    @ApiOperation(value = "修改该学员的点名信息", httpMethod = "PUT")
    @ApiImplicitParam(paramType = "query", name = "callRecordVO", value = "该学员的点名信息", dataType = "BdCallRecordVO")
    @Log(title = "修改该学员的点名信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/saveRollCall")
    @Response
    public Result<Boolean> saveRollCall(@RequestBody BdCallRecordVO callRecordVO) {
        return new Result<Boolean>().success(classRecordManager.saveRollCall(callRecordVO));
    }
}
