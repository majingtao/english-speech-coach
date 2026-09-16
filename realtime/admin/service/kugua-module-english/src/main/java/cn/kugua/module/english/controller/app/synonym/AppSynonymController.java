package cn.kugua.module.english.controller.app.synonym;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.kugua.module.english.controller.app.synonym.vo.*;
import cn.kugua.module.english.service.synonym.SynonymService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/english/synonym")
public class AppSynonymController {
    @Resource private SynonymService service;

    @GetMapping("/queue")
    public CommonResult<List<AppSynonymPointRespVO>> queue(
            @RequestParam(defaultValue = "ket") String level,
            @RequestParam(required = false) String mode) {
        return success(service.getPracticeQueue(SecurityFrameworkUtils.getLoginUserId(), level, mode));
    }

    @GetMapping("/stats")
    public CommonResult<AppSynonymStatsRespVO> stats(@RequestParam(defaultValue = "ket") String level) {
        return success(service.getStats(SecurityFrameworkUtils.getLoginUserId(), level));
    }

    @PostMapping("/point/{id}/answer")
    public CommonResult<AppSynonymAnswerRespVO> answer(@PathVariable Long id, @Valid @RequestBody AppSynonymAnswerReqVO req) {
        return success(service.answer(SecurityFrameworkUtils.getLoginUserId(), id, req));
    }

    @GetMapping("/audio")
    public CommonResult<Map<String, String>> audio(
            @RequestParam(defaultValue = "ket") String level,
            @RequestParam String text,
            @RequestParam(defaultValue = "us") String accent) {
        return success(Map.of("url", service.getAudioUrl(level, text, accent), "accent", accent));
    }
}
