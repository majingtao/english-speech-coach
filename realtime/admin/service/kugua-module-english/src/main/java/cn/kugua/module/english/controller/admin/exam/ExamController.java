package cn.kugua.module.english.controller.admin.exam;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.exam.vo.ExamPageReqVO;
import cn.kugua.module.english.controller.admin.exam.vo.ExamRespVO;
import cn.kugua.module.english.controller.admin.exam.vo.ExamSaveReqVO;
import cn.kugua.module.english.dal.dataobject.exam.ExamDO;
import cn.kugua.module.english.service.exam.ExamService;
import cn.kugua.module.english.controller.app.questionBank.AppQuestionBankController;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 英语口语试卷")
@RestController
@RequestMapping("/english/exam")
public class ExamController {

    @Resource
    private ExamService examService;

    @Lazy
    @Resource
    private AppQuestionBankController questionBankController;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    // ========== content_json 相关 ==========

    @GetMapping("/content")
    @Operation(summary = "获取试卷 JSON 内容")
    @Parameter(name = "id", description = "试卷编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:exam:query')")
    public CommonResult<ExamRespVO> getExamContent(@RequestParam("id") Long id) {
        ExamDO exam = examService.getExam(id);
        return success(BeanUtils.toBean(exam, ExamRespVO.class));
    }

    @PutMapping("/content")
    @Operation(summary = "保存试卷 JSON 内容")
    @PreAuthorize("@ss.hasPermission('english:exam:update')")
    public CommonResult<Boolean> updateExamContent(@RequestBody java.util.Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        String contentJson = (String) body.get("contentJson");
        // 校验 JSON 格式
        try {
            new ObjectMapper().readTree(contentJson);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON 格式不正确: " + e.getMessage());
        }
        examService.updateContentJson(id, contentJson);
        return success(true);
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/migrate-to-json")
    @Operation(summary = "一次性迁移：将多表数据写入 content_json")
    @PreAuthorize("@ss.hasPermission('english:exam:update')")
    public CommonResult<String> migrateToJson() {
        // 1. 调用现有聚合逻辑，拿到全量 tests map
        CommonResult<Map<String, Object>> allResult = questionBankController.getAll(null, null);
        Map<String, Object> tests = (Map<String, Object>) allResult.getData().get("tests");
        if (tests == null || tests.isEmpty()) {
            return success("无试卷需要迁移");
        }
        // 2. 查所有激活试卷
        ExamPageReqVO pageReq = new ExamPageReqVO();
        pageReq.setPageNo(1);
        pageReq.setPageSize(1000);
        PageResult<ExamDO> pageResult = examService.getExamPage(pageReq);
        int count = 0;
        for (ExamDO exam : pageResult.getList()) {
            Object testData = tests.get(exam.getExamCode());
            if (testData == null) {
                continue;
            }
            try {
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(testData);
                examService.updateContentJson(exam.getId(), json);
                count++;
            } catch (Exception e) {
                // skip
            }
        }
        return success("已迁移 " + count + " 套试卷");
    }

}
