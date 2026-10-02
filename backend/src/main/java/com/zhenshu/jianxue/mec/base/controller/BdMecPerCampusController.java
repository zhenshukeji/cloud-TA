package com.zhenshu.jianxue.mec.base.controller;

import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusConnectionBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusConnectionVO;
import com.zhenshu.jianxue.mec.base.manager.MecPersonnelInfoManager;
import com.zhenshu.common.annotation.Response;
import com.zhenshu.common.manager.mec.base.LoginUserManager;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.AjaxResult;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.framework.web.page.TableDataInfo;
import com.zhenshu.jianxue.mec.project.system.domain.SysRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jing
 * @version 1.0
 * @desc 机构人员在校区里的接口
 * @date 2020/10/13 0013 15:45
 **/
@Api(value = "校区机构人员", tags = "机构人员接口")
@RestController
@RequestMapping("/base/m_personnel_c")
@PreAuthorize("@ss.hasPermi('base:m_personnel_c:list')")
public class BdMecPerCampusController extends BaseController {
    @Resource
    private MecPersonnelInfoManager mecPersonnelInfoManager;
    @Resource
    private LoginUserManager loginUserManager;


    @ApiOperation(value = "查询机构人员列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdCampusConnectionVO", value = "机构人员 封装类", dataType = "BdCampusConnectionVO")
    @GetMapping("/getByCampus")
    @Response
    public TableDataInfo getByCampus(BdCampusConnectionVO campusConnectionVO) {
        startPage();
        List<BdCampusConnectionBO> connections = mecPersonnelInfoManager.getByCampus(campusConnectionVO);
        if (connections == null || connections.isEmpty()){
            return new TableDataInfo();
        }
        return getDataTable(connections);
    }


    @ApiOperation(value = "给机构人员设置角色", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "bdCampusConnectionVO", value = "机构人员 封装类", dataType = "BdCampusConnectionVO")
    @PostMapping("/setRole")
    @Response
    public Result<String> setRole(@RequestBody BdCampusConnectionVO campusConnectionVO) {
        mecPersonnelInfoManager.setRoles(campusConnectionVO.getId(), campusConnectionVO.getRoles());
        return new Result<String>().success();
    }


    @ApiOperation(value = "获取机构人员拥有的角色id列表", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "id", value = "机构人员 封装类", dataType = "Long")
    @PostMapping("/getRole")
    @Response
    public Result<List<Long>> getRoleList(@RequestBody BdCampusConnectionVO campusConnectionVO) {
        List<Long> roles = mecPersonnelInfoManager.getRoles(campusConnectionVO.getId());
        return new Result<List<Long>>().success(roles);
    }


    @ApiOperation(value = "获取角色列表")
    @PostMapping("/allRole")
    @Response
    public AjaxResult all() {
        List<SysRole> list = loginUserManager.getRoleList();
        return AjaxResult.success(list);
    }
}
