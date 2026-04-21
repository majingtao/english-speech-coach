package cn.kugua.module.english.controller.app.quota;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.kugua.module.english.controller.app.quota.vo.EscQuotaConsumeReqVO;
import cn.kugua.module.english.controller.app.quota.vo.EscQuotaConsumeRespVO;
import cn.kugua.module.english.controller.app.quota.vo.EscQuotaMeRespVO;
import cn.kugua.module.english.service.quota.EscQuotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * H5 会员端：
 * - /me      展示当前用户的配额 + 今日使用情况
 * - /consume server.py 内部调用扣减（仍复用 yudao token 鉴权）
 */
@Tag(name = "H5 - 英语课配额")
@RestController
@RequestMapping("/esc/quota")
@Validated
public class AppEscQuotaController {

    @Resource
    private EscQuotaService quotaService;

    @GetMapping("/me")
    @Operation(summary = "查看我的配额与剩余")
    public CommonResult<EscQuotaMeRespVO> getMyQuota() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(quotaService.getMyQuota(userId));
    }

    @PostMapping("/consume")
    @Operation(summary = "扣减配额（server.py 每次调用 LLM/ASR/TTS 前后调用）")
    public CommonResult<EscQuotaConsumeRespVO> consume(@Valid @RequestBody EscQuotaConsumeReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(quotaService.consume(userId, reqVO.getResource(), reqVO.getAmount()));
    }

}
