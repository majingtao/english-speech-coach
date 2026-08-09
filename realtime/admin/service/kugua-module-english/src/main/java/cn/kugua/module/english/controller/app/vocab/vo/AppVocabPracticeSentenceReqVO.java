package cn.kugua.module.english.controller.app.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "H5 - 造句练习 Request VO")
@Data
public class AppVocabPracticeSentenceReqVO {

    @Schema(description = "学生写的例句", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "sentence 必填")
    private String sentence;

}
