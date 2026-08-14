package cn.kugua.module.english.controller.admin.grammar.vo;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class GrammarQuestionSaveReqVO {
    private Long id;
    @NotNull private Long grammarPointId;
    private Long generationJobId;
    private Long sourceQuestionId;
    @NotEmpty private String code;
    @NotEmpty private String questionType;
    @NotNull private Integer difficulty;
    private String instruction;
    @NotEmpty private String stem;
    private String optionsJson;
    @NotEmpty private String answerJson;
    @NotEmpty private String explanationZh;
    private String ruleText;
    private String errorTagsJson;
    private String mediaJson;
    private String source;
    private Integer validationStatus;
    private Integer sort;
    private Integer status;
}
