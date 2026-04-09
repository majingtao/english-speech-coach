package cn.kugua.module.english.controller.admin.schoolClass;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.kugua.module.english.controller.admin.schoolClass.vo.*;
import cn.kugua.module.english.dal.dataobject.schoolClass.SchoolClassDO;
import cn.kugua.module.english.service.schoolClass.SchoolClassService;

@Tag(name = "管理后台 - 班级")
@RestController
@RequestMapping("/english/school-class")
@Validated
public class SchoolClassController {

    @Resource
    private SchoolClassService schoolClassService;

    @PostMapping("/create")
    @Operation(summary = "创建班级")
    @PreAuthorize("@ss.hasPermission('english:school-class:create')")
    public CommonResult<Long> createSchoolClass(@Valid @RequestBody SchoolClassSaveReqVO createReqVO) {
        return success(schoolClassService.createSchoolClass(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新班级")
    @PreAuthorize("@ss.hasPermission('english:school-class:update')")
    public CommonResult<Boolean> updateSchoolClass(@Valid @RequestBody SchoolClassSaveReqVO updateReqVO) {
        schoolClassService.updateSchoolClass(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除班级")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:school-class:delete')")
    public CommonResult<Boolean> deleteSchoolClass(@RequestParam("id") Long id) {
        schoolClassService.deleteSchoolClass(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除班级")
                @PreAuthorize("@ss.hasPermission('english:school-class:delete')")
    public CommonResult<Boolean> deleteSchoolClassList(@RequestParam("ids") List<Long> ids) {
        schoolClassService.deleteSchoolClassListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得班级")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:school-class:query')")
    public CommonResult<SchoolClassRespVO> getSchoolClass(@RequestParam("id") Long id) {
        SchoolClassDO schoolClass = schoolClassService.getSchoolClass(id);
        return success(BeanUtils.toBean(schoolClass, SchoolClassRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得班级分页")
    @PreAuthorize("@ss.hasPermission('english:school-class:query')")
    public CommonResult<PageResult<SchoolClassRespVO>> getSchoolClassPage(@Valid SchoolClassPageReqVO pageReqVO) {
        PageResult<SchoolClassDO> pageResult = schoolClassService.getSchoolClassPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SchoolClassRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出班级 Excel")
    @PreAuthorize("@ss.hasPermission('english:school-class:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSchoolClassExcel(@Valid SchoolClassPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SchoolClassDO> list = schoolClassService.getSchoolClassPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "班级.xls", "数据", SchoolClassRespVO.class,
                        BeanUtils.toBean(list, SchoolClassRespVO.class));
    }

}