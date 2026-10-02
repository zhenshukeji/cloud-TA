package com.zhenshu.jianxue.mec.base.controller;

import com.zhenshu.common.lock.LockFunction;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusPersonnelVO;
import com.zhenshu.jianxue.mec.base.domain.vo.ResetPwdVo;
import com.zhenshu.jianxue.mec.base.manager.CampusPersonnelInfoManager;
import com.zhenshu.jianxue.mec.base.service.IBdCampusPersonnelService;
import com.zhenshu.common.annotation.Response;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.framework.aspectj.lang.annotation.Log;
import com.zhenshu.framework.aspectj.lang.enums.BusinessType;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.AjaxResult;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.framework.web.page.TableDataInfo;
import com.zhenshu.jianxue.mec.project.system.domain.SysRole;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.zhenshu.common.manager.mec.base.LoginUserManager;

import javax.annotation.Resource;
import java.util.List;

/**
 * 校区人员Controller
 *
 * @author jing
 * @date 2020-10-04
 */
@Api(value = "校区人员", tags = "校区人员接口")
@RestController
@RequestMapping("/base/campus_personnel")
@PreAuthorize("@ss.hasPermi('base:campus_personnel:list')")
public class BdCampusPersonnelController extends BaseController {

    @Resource
    private IBdCampusPersonnelService bdCampusPersonnelService;
    @Resource
    private CampusPersonnelInfoManager campusPersonnelInfoManager;
    @Resource
    private LoginUserManager loginUserManager;


    @ApiOperation(value = "查询校区人员列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdCampusPersonnelVO", value = "校区人员封装类", dataType = "BdCampusPersonnelVO")
    @GetMapping("/list")
    @Response
    public TableDataInfo list(BdCampusPersonnelVO bdCampusPersonnelVO) {
        // 根据校区id进行筛选
        startPage();
        List<BdCampusPersonnelBO> list = bdCampusPersonnelService.selectBdCampusPersonnelList(bdCampusPersonnelVO);
        if (list == null || list.isEmpty()){
            return new TableDataInfo();
        }

        return getDataTable(list);
    }


    @ApiOperation(value = "获取校区人员详细信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "BdCampusPersonnelVO", value = "校区人员id", dataType = "Long")
    @GetMapping(value = "/{id}")
    @Response
    public Result<BdCampusPersonnelBO> getInfo(@PathVariable("id") Long id) {
        BdCampusPersonnelBO bdCampusPersonnelBO = campusPersonnelInfoManager.getRoleListById(id);
        return new Result<BdCampusPersonnelBO>().success(bdCampusPersonnelBO);
    }


    @ApiOperation(value = "修改校区人员详细信息", httpMethod = "PUT")
    @Log(title = "校区人员", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(paramType = "query", name = "BdCampusPersonnelVO", value = "校区人员封装类", dataType = "BdCampusPersonnelVO")
    @PutMapping
    @Response
    public Result<String> edit(@RequestBody BdCampusPersonnelVO bdCampusPersonnelVO) {
        campusPersonnelInfoManager.updateCampusPersonnel(bdCampusPersonnelVO);
        return new Result<String>().success();
    }


    @ApiOperation(value = "新增校区人员", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "BdCampusPersonnelVO", value = "校区人员封装类", dataType = "BdCampusPersonnelVO")
    @Log(title = "校区人员", businessType = BusinessType.INSERT)
    @PostMapping
    @Response
    @LockFunction(methodName = "addCampusPerson")
    public Result<String> add(@RequestBody BdCampusPersonnelVO bdCampusPersonnelVO) {
        if (StringUtils.isEmpty(bdCampusPersonnelVO.getPhone()) || StringUtils.isEmpty(bdCampusPersonnelVO.getName()) || StringUtils.isEmpty(bdCampusPersonnelVO.getPassword())) {
            return new Result<String>().fail("参数不能为空");
        }
        SysUser user = SecurityUtils.getSysUser();
        bdCampusPersonnelVO.setMecId(user.getMecId());
        bdCampusPersonnelVO.setCampusId(user.getCampusId());
        bdCampusPersonnelVO.setCampusName(user.getCampusName());
        campusPersonnelInfoManager.insertCampusPersonnel(bdCampusPersonnelVO);
        return new Result<String>().success();
    }


    @ApiOperation(value = "删除校区人员")
    @Log(title = "校区人员", businessType = BusinessType.DELETE)
    @ApiImplicitParam(paramType = "path", name = "id", value = "删除的校区人员的id", dataType = "Long")
    @DeleteMapping("/{id}")
    @Response
    public Result<Boolean> remove(@PathVariable Long id) {
        campusPersonnelInfoManager.deleteCampusPersonnel(id);
        return new Result<Boolean>().success();
    }


    @ApiOperation(value = "重置密码")
    @Log(title = "重置密码", businessType = BusinessType.DELETE)
    @DeleteMapping("/resetPwd")
    @Response
    public Result<String> resetPwd(@RequestBody ResetPwdVo resetPwdVo) {
        campusPersonnelInfoManager.resetPwd(resetPwdVo.getId(), resetPwdVo.getPwd());
        return new Result<String>().success();
    }


    @ApiOperation(value = "获取角色列表")
    @PostMapping("/allRole")
    @Response
    public AjaxResult all() {
        List<SysRole> list = loginUserManager.getRoleList();
        return AjaxResult.success(list);
    }
}
