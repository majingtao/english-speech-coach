package cn.kugua.module.english.controller.app.quota.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(description = "H5/Server - 配额扣减请求 VO")
@Data
public class EscQuotaConsumeReqVO {

    @Schema(description = "资源类型", allowableValues = {"llm", "asr", "tts"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "resource 不能为空")
    @Pattern(regexp = "^(llm|asr|tts)$", message = "resource 只能是 llm/asr/tts")
    private String resource;

    @Schema(description = "扣减量：llm=1, asr=音频秒数, tts=文本字符数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "amount 不能为空")
    @Min(value = 1, message = "amount 至少为 1")
    private Integer amount;

}
