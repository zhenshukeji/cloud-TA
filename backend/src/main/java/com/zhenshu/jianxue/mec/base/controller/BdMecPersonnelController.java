package com.zhenshu.jianxue.mec.base.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.zhenshu.common.annotation.Response;
import com.zhenshu.common.constant.MecConstant;
import com.zhenshu.common.lock.LockFunction;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.framework.aspectj.lang.annotation.Log;
import com.zhenshu.framework.aspectj.lang.enums.BusinessType;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.framework.web.page.TableDataInfo;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMecPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdMecPersonnelVO;
import com.zhenshu.jianxue.mec.base.domain.vo.ResetPwdVo;
import com.zhenshu.jianxue.mec.base.manager.MecPersonnelInfoManager;
import com.zhenshu.jianxue.mec.base.service.IBdMecPersonnelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 机构人员Controller
 *
 * @author jing
 * @date 2020-10-04
 */
@Api(value = "机构人员", tags = "机构人员接口")
@RestController
@RequestMapping("/base/mec_personnel")
@PreAuthorize("@ss.hasPermi('base:mec_personnel:list')")
public class BdMecPersonnelController extends BaseController {
    @Resource
    private IBdMecPersonnelService bdMecPersonnelService;
    @Resource
    private MecPersonnelInfoManager mecPersonnelInfoManager;


    @ApiOperation(value = "查询机构人员列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdMecPersonnelVO", value = "机构人员封装类", dataType = "BdMecPersonnelVO")
    @GetMapping("/list")
    @Response
    public TableDataInfo list(BdMecPersonnelVO bdMecPersonnelVO) {
        // 根据机构id进行查询
        Long mecId = SecurityUtils.getLoginMecId();
        bdMecPersonnelVO.setMecId(mecId);
        bdMecPersonnelVO.setType(MecConstant.MEC_PERSONNEL);
        Page<BdMecPersonnelBO> page = bdMecPersonnelService.selectBdMecPersonnelList(bdMecPersonnelVO);
        if (page.getRecords() == null || page.getRecords().isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(page);
    }


    @ApiOperation(value = "获取机构人员详细信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "id", value = "机构人员id", dataType = "Long")
    @GetMapping(value = "/{id}")
    @Response
    public Result<BdMecPersonnelBO> getInfo(@PathVariable("id") Long id) {
        return new Result<BdMecPersonnelBO>().success(mecPersonnelInfoManager.getInfo(id));
    }


    @ApiOperation(value = "修改机构人员详细信息", httpMethod = "PUT")
    @Log(title = "机构人员", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(paramType = "query", name = "bdMecPersonnelVO", value = "机构人员信息VO", dataType = "BdMecPersonnelVO")
    @PutMapping
    @Response
    public Result<String> edit(@RequestBody BdMecPersonnelVO bdMecPersonnelVO) {
        mecPersonnelInfoManager.updateCampusPersonnel(bdMecPersonnelVO);
        return new Result<String>().success("修改成功");
    }


    @ApiOperation(value = "新增机构人员", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "BdMecPersonnelVO", value = "机构人员信息VO", dataType = "BdMecPersonnelVO")
    @Log(title = "机构人员", businessType = BusinessType.INSERT)
    @PostMapping
    @Response
    @LockFunction(methodName = "addMecPerson")
    public Result<String> add(@RequestBody BdMecPersonnelVO bdMecPersonnelVO) {
        bdMecPersonnelVO.setMecId(SecurityUtils.getLoginMecId());
        mecPersonnelInfoManager.insertCampusPersonnel(bdMecPersonnelVO);
        return new Result<String>().success("新增成功");
    }


    @ApiOperation(value = "删除机构人员")
    @Log(title = "机构人员", businessType = BusinessType.DELETE)
    @ApiImplicitParam(paramType = "path", name = "id", value = "删除的机构人员的id", dataType = "Long")
    @DeleteMapping("/{id}")
    @Response
    public Result<String> remove(@PathVariable Long id) {
        mecPersonnelInfoManager.deleteCampusPersonnel(id);
        return new Result<String>().success("删除成功");
    }


    @ApiOperation(value = "重置密码")
    @Log(title = "重置密码", businessType = BusinessType.DELETE)
    @ApiImplicitParam(paramType = "path", name = "resetPwdVo", value = "重置密码传参集合", dataType = "ResetPwdVo")
    @DeleteMapping("/resetPwd")
    @Response
    public Result<String> resetPwd(@RequestBody ResetPwdVo resetPwdVo) {
        mecPersonnelInfoManager.resetPwd(resetPwdVo.getId(), resetPwdVo.getPwd());
        return new Result<String>().success("修改成功");
    }


}
