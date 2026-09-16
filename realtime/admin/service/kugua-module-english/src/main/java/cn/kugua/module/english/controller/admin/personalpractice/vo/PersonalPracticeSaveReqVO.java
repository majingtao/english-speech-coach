package cn.kugua.module.english.controller.admin.personalpractice.vo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PersonalPracticeSaveReqVO {
    private Long id;
    @NotEmpty(message = "练习类型不能为空")
    private String practiceType;
    @NotEmpty(message = "标题不能为空")
    private String title;
    @NotEmpty(message = "英文题目不能为空")
    private String promptEn;
    private String promptCn;
    @NotEmpty(message = "参考句不能为空")
    private String referenceJson;
    private String contentPointsJson;
    @NotNull(message = "最低句数不能为空")
    private Integer minSentences;
    private Integer minWords;
    private Integer sort;
    private Integer status;
}
