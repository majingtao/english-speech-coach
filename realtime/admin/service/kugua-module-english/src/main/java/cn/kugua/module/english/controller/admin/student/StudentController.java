package cn.kugua.module.english.controller.admin.student;

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

import cn.kugua.module.english.controller.admin.student.vo.*;
import cn.kugua.module.english.dal.dataobject.student.StudentDO;
import cn.kugua.module.english.service.student.StudentService;

@Tag(name = "管理后台 - 学生")
@RestController
@RequestMapping("/english/student")
@Validated
public class StudentController {

    @Resource
    private StudentService studentService;

    @PostMapping("/create")
    @Operation(summary = "创建学生")
    @PreAuthorize("@ss.hasPermission('english:student:create')")
    public CommonResult<Long> createStudent(@Valid @RequestBody StudentSaveReqVO createReqVO) {
        return success(studentService.createStudent(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新学生")
    @PreAuthorize("@ss.hasPermission('english:student:update')")
    public CommonResult<Boolean> updateStudent(@Valid @RequestBody StudentSaveReqVO updateReqVO) {
        studentService.updateStudent(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除学生")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:student:delete')")
    public CommonResult<Boolean> deleteStudent(@RequestParam("id") Long id) {
        studentService.deleteStudent(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除学生")
                @PreAuthorize("@ss.hasPermission('english:student:delete')")
    public CommonResult<Boolean> deleteStudentList(@RequestParam("ids") List<Long> ids) {
        studentService.deleteStudentListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得学生")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('english:student:query')")
    public CommonResult<StudentRespVO> getStudent(@RequestParam("id") Long id) {
        StudentDO student = studentService.getStudent(id);
        return success(BeanUtils.toBean(student, StudentRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得学生分页")
    @PreAuthorize("@ss.hasPermission('english:student:query')")
    public CommonResult<PageResult<StudentRespVO>> getStudentPage(@Valid StudentPageReqVO pageReqVO) {
        PageResult<StudentDO> pageResult = studentService.getStudentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, StudentRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出学生 Excel")
    @PreAuthorize("@ss.hasPermission('english:student:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportStudentExcel(@Valid StudentPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StudentDO> list = studentService.getStudentPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "学生.xls", "数据", StudentRespVO.class,
                        BeanUtils.toBean(list, StudentRespVO.class));
    }

}