package cn.kugua.module.english.controller.admin.exam;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.exam.vo.ExamPageReqVO;
import cn.kugua.module.english.controller.admin.exam.vo.ExamRespVO;
import cn.kugua.module.english.controller.admin.exam.vo.ExamSaveReqVO;
import cn.kugua.module.english.dal.dataobject.exam.ExamDO;
import cn.kugua.module.english.service.exam.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 英语口语试卷")
@RestController
@RequestMapping("/english/exam")
public class ExamController {

    @Resource
    private ExamService examService;

    @PostMapping("/create")
    @Operation(summary = "创建试卷")
    @PreAuthorize("@ss.hasPermission('english:exam:create')")
    public CommonResult<Long> createExam(@Valid @RequestBody ExamSaveReqVO reqVO) {
        return success(examService.createExam(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新试卷")
    @PreAuthorize("@ss.hasPermission('english:exam:update')")
    public CommonResult<Boolean> updateExam(@Valid @RequestBody ExamSaveReqVO reqVO) {
        examService.updateExam(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除试卷")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:exam:delete')")
    public CommonResult<Boolean> deleteExam(@RequestParam("id") Long id) {
        examService.deleteExam(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得试卷")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:exam:query')")
    public CommonResult<ExamRespVO> getExam(@RequestParam("id") Long id) {
        ExamDO exam = examService.getExam(id);
        return success(BeanUtils.toBean(exam, ExamRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得试卷分页")
    @PreAuthorize("@ss.hasPermission('english:exam:query')")
    public CommonResult<PageResult<ExamRespVO>> getExamPage(@Valid ExamPageReqVO reqVO) {
        PageResult<ExamDO> pageResult = examService.getExamPage(reqVO);
        return success(BeanUtils.toBean(pageResult, ExamRespVO.class));
    }

}
