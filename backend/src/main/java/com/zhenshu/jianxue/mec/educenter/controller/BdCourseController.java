package com.zhenshu.jianxue.mec.educenter.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.zhenshu.common.annotation.Response;
import com.zhenshu.common.cache.CacheService;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.framework.aspectj.lang.annotation.Log;
import com.zhenshu.framework.aspectj.lang.enums.BusinessType;
import com.zhenshu.framework.web.controller.BaseController;
import com.zhenshu.framework.web.domain.Result;
import com.zhenshu.framework.web.page.TableDataInfo;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCourseBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseVO;
import com.zhenshu.jianxue.mec.educenter.manager.CourseManager;
import com.zhenshu.jianxue.mec.educenter.service.IBdCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 课程Controller
 *
 * @author jing
 * @date 2020-10-04
 */
@Api(value = "课程", tags = "课程接口")
@RestController
@RequestMapping("/educenter/course")
@PreAuthorize("@ss.hasPermi('educenter:course:list')")
public class BdCourseController extends BaseController {

    @Resource
    private IBdCourseService bdCourseService;
    @Resource
    private CourseManager courseManager;
    @Resource
    private CacheService cacheService;
    @ApiOperation(value = "查询常规课程列表", httpMethod = "GET")
    @ApiImplicitParam(paramType = "query", name = "bdCourseVO", value = "课程封装类", dataType = "BdCourseVO")
    @GetMapping("/list")
    @Response
    public TableDataInfo conventionalList(BdCourseVO bdCourseVO) {
        Page<BdCourseBO> page = bdCourseService.selectConventionalCourseList(bdCourseVO);
        if (page.getRecords() == null || page.getRecords().isEmpty()) {
            return new TableDataInfo();
        }
        return getDataTable(page);
    }


    @ApiOperation(value = "获取课程详细信息", httpMethod = "GET")
    @ApiImplicitParam(paramType = "path", name = "BdCourseVO", value = "获取课程详细信息", dataType = "Long")
    @GetMapping(value = "/{id}")
    @Response
    public Result<BdCourseBO> getInfo(@PathVariable("id") Long id) {
        return new Result<BdCourseBO>().success(bdCourseService.selectBdCourseById(id));
    }


    @ApiOperation(value = "新增课程", httpMethod = "POST")
    @ApiImplicitParam(paramType = "query", name = "BdCourseVO", value = "新增课程详细信息", dataType = "BdCourseVO")
    @Log(title = "新增课程", businessType = BusinessType.INSERT)
    @PostMapping
    @Response
    public Result<Boolean> add(@RequestBody BdCourseVO bdCourseVO) {
        bdCourseService.insertBdCourse(bdCourseVO);
        cacheService.delIndexRecommendCache();
        cacheService.delMecRecommendCache();
        return new Result<Boolean>().success();
    }


    @ApiOperation(value = "修改课程详细信息", httpMethod = "PUT")
    @Log(title = "课程", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(paramType = "query", name = "bdCourseVO", value = "修改课程详细信息", dataType = "BdCourseVO")
    @PutMapping
    @Response
    public Result<Boolean> edit(@RequestBody BdCourseVO bdCourseVO) {
        // 社区版（OS008）：课程编辑只更新课程自身，不再联动任何平台侧能力
        bdCourseService.updateBdCourse(bdCourseVO);
        cacheService.delIndexRecommendCache();
        cacheService.delMecRecommendCache();
        return new Result<Boolean>().success();
    }


    @ApiOperation(value = "删除课程")
    @Log(title = "删除课程", businessType = BusinessType.DELETE)
    @ApiImplicitParam(paramType = "path", name = "ids", value = "删除的课程的ids", dataType = "Long[]")
    @DeleteMapping("/{ids}")
    @Response
    public Result<String> remove(@PathVariable Long[] ids) {
        if (ids.length <= 0) {
            throw new CustomException("课程id不能为空");
        }
        courseManager.deleteCourse(ids);
        cacheService.delIndexRecommendCache();
        cacheService.delMecRecommendCache();
        return new Result<String>().success();
    }

}
