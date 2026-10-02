package com.zhenshu.jianxue.mec.base.controller;

import com.zhenshu.common.annotation.Response;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.framework.aspectj.lang.annotation.Log;
import com.zhenshu.framework.aspectj.lang.enums.BusinessType;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.framework.web.page.TableDataInfo;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusVO;
import com.zhenshu.jianxue.mec.base.domain.vo.ResetPwdVo;
import com.zhenshu.jianxue.mec.base.manager.CampusInfoManager;
import com.zhenshu.jianxue.mec.base.service.IBdCampusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jing
 */
@Api(value = "校区", tags = "校区接口")
@RestController
@RequestMapping("/base/campus")
@PreAuthorize("@ss.hasPermi('base:campus:list')")
public class BdCampusController extends BaseController {
    @Resource
    private IBdCampusService bdCampusService;
    @Resource
    private CampusInfoManager campusInfoManager;

    @ApiOperation(value = "查询校区列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdCampusVO", value = "校区封装类", dataType = "BdCampusVO")
    @GetMapping("/list")
    @Response
    public TableDataInfo list(BdCampusVO bdCampusVO) {
        // 根据机构id查询校区列表
        Long mecId = SecurityUtils.getLoginMecId();
        startPage();
        List<BdCampusBO> resultList = bdCampusService.selectBdCampusList(bdCampusVO, mecId);
        if (resultList == null || resultList.isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(resultList);
    }


    @ApiOperation(value = "获取校区详细信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "id", value = "id", dataType = "Long")
    // 社区版（OS008）：路径变量限定为数字。原写法 `/{id}` 会吞掉 `/base/campus/` 下的任意路径，
    // 使已删除的接口（如 checkLatAndLng）被路由吃住后抛 String→Long 转换异常，
    // 再被 GlobalExceptionHandler 兜成 HTTP 200 +「访问人数过多」，无法按验收要求返回 404。
    @GetMapping(value = "/{id:\\d+}")
    @Response
    public Result<BdCampusBO> getInfo(@PathVariable("id") Long id) {
        // 社区版（OS009）：按 id 直读也必须落在登录机构范围内。
        // 原实现直接 selectById，改一个 id 就能读到其他归属的校区（指引 §4.2「客户端提交
        // 其他机构或校区 ID 时不能越权」）。这里补一次归属校验，不匹配即拒绝。
        BdCampusBO campus = bdCampusService.selectBdCampusById(id);
        if (campus != null) {
            Long loginMecId = SecurityUtils.getLoginMecId();
            if (loginMecId == null || !loginMecId.equals(campus.getMecId())) {
                return new Result<BdCampusBO>().fail("无权访问该校区");
            }
        }
        return new Result<BdCampusBO>().success(campus);
    }


    @ApiOperation(value = "修改校区详细信息", httpMethod = "PUT")
    @Log(title = "修改校区详细信息", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(paramType = "query", name = "bdCampusVO", value = "修改校区详细信息", dataType = "BdCampusVO")
    @PutMapping
    @Response
    public Result<String> edit(@RequestBody BdCampusVO bdCampusVO) throws Exception {
        campusInfoManager.updateCampus(bdCampusVO);
        return new Result<String>().success();
    }

    // 社区版（OS009）：移除「新增校区」端点。
    // 依据执行指引 §4.2「初始化完成后不开放创建第二机构和第二校区的入口/API」。
    // 原实现只要求类级 base:campus:list 权限（种子已授予机构管理员），因此管理员可直接创建
    // 第二个校区，与「单机构单校区」的产品边界冲突，并会给数据归属校验引入多校区分支。
    // 唯一校区由 database/002_seed_system.sql 初始化，不再提供任何新增入口。

    @ApiOperation(value = "删除校区")
    @Log(title = "删除校区", businessType = BusinessType.DELETE)
    @ApiImplicitParam(paramType = "path", name = "campusId", value = "删除的校区的id", dataType = "Long")
    // 社区版（OS008）：同 getInfo，路径变量限定为数字，避免 `/{campusId}` 吞掉被删接口的路径。
    @DeleteMapping("/{campusId:\\d+}")
    @Response
    public Result<String> remove(@PathVariable Long campusId) {
        campusInfoManager.deleteCampus(campusId);
        return new Result<String>().success();
    }


    @ApiOperation(value = "重置密码")
    @Log(title = "重置密码", businessType = BusinessType.DELETE)
    @DeleteMapping("/resetPwd")
    @Response
    public Result<String> resetPwd(@RequestBody ResetPwdVo resetPwdVo) {
        campusInfoManager.resetPwd(resetPwdVo.getId(), resetPwdVo.getPwd());
        return new Result<String>().success();
    }

}
