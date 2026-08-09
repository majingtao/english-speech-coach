package cn.kugua.module.english.controller.app.vocab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "H5 - 批量加词 Request VO")
@Data
public class AppUserVocabListItemsAddReqVO {

    @Schema(description = "词条 ID 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "vocabIds 不能为空")
    private List<Long> vocabIds;

}
