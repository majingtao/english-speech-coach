package cn.kugua.module.english.controller.app.grammar;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.kugua.module.english.controller.app.grammar.vo.*;
import cn.kugua.module.english.dal.dataobject.grammar.GrammarPointDO;
import cn.kugua.module.english.service.grammar.GrammarService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/english/grammar")
public class AppGrammarController {
    @Resource private GrammarService service;
    @GetMapping("/point/list")
    public CommonResult<List<GrammarPointDO>> points(@RequestParam(defaultValue = "ket") String level) {
        return success(service.getPoints(level, true));
    }
    @GetMapping("/practice")
    public CommonResult<List<AppGrammarQuestionRespVO>> practice(@RequestParam Long grammarPointId,
            @RequestParam(defaultValue = "1") Integer difficulty, @RequestParam(defaultValue = "10") Integer count) {
        return success(service.getPractice(grammarPointId, difficulty, count));
    }
    @PostMapping("/question/{id}/answer")
    public CommonResult<AppGrammarAnswerRespVO> answer(@PathVariable Long id, @Valid @RequestBody AppGrammarAnswerReqVO req) {
        return success(service.answer(SecurityFrameworkUtils.getLoginUserId(), id, req));
    }
}
