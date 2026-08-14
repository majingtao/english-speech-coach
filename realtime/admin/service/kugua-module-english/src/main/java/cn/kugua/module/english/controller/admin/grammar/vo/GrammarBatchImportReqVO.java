package cn.kugua.module.english.controller.admin.grammar.vo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;
@Data
public class GrammarBatchImportReqVO {
    @NotNull private Long grammarPointId;
    @Min(1) @Max(4) private Integer difficulty;
    @NotEmpty private String questionTypes;
    private Boolean autoPublish = true;
    private String model;
    private String settingsJson;
    @NotEmpty @Valid private List<GrammarQuestionSaveReqVO> questions;
}
