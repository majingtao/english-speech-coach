package cn.kugua.module.english.controller.admin.grammar;

import cn.iocoder.yudao.framework.common.pojo.*;
import cn.kugua.module.english.controller.admin.grammar.vo.*;
import cn.kugua.module.english.dal.dataobject.grammar.*;
import cn.kugua.module.english.service.grammar.GrammarService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 语法题库")
@RestController
@RequestMapping("/english/grammar")
public class GrammarController {
    @Resource private GrammarService service;

    @GetMapping("/point/list") @PreAuthorize("@ss.hasPermission('english:grammar:query')")
    public CommonResult<List<GrammarPointDO>> points(@RequestParam(defaultValue = "ket") String level) {
        return success(service.getPoints(level, false));
    }
    @GetMapping("/question/page") @PreAuthorize("@ss.hasPermission('english:grammar:query')")
    public CommonResult<PageResult<GrammarQuestionDO>> page(@Valid GrammarQuestionPageReqVO req) { return success(service.getQuestionPage(req)); }
    @GetMapping("/question/get") @PreAuthorize("@ss.hasPermission('english:grammar:query')")
    public CommonResult<GrammarQuestionDO> get(@RequestParam Long id) { return success(service.getQuestion(id)); }
    @PostMapping("/question/create") @PreAuthorize("@ss.hasPermission('english:grammar:create')")
    public CommonResult<Long> create(@Valid @RequestBody GrammarQuestionSaveReqVO req) { return success(service.createQuestion(req)); }
    @PutMapping("/question/update") @PreAuthorize("@ss.hasPermission('english:grammar:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody GrammarQuestionSaveReqVO req) { service.updateQuestion(req); return success(true); }
    @DeleteMapping("/question/delete") @PreAuthorize("@ss.hasPermission('english:grammar:delete')")
    public CommonResult<Boolean> delete(@RequestParam Long id) { service.deleteQuestion(id); return success(true); }
    @PostMapping("/generation/import") @Operation(summary = "保存 AI 生成题目，默认直接发布")
    @PreAuthorize("@ss.hasPermission('english:grammar:create')")
    public CommonResult<GrammarGenerationJobDO> importGenerated(@Valid @RequestBody GrammarBatchImportReqVO req) {
        return success(service.importGenerated(req));
    }
}
