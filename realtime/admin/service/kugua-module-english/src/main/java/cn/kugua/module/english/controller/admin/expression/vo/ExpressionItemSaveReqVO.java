package cn.kugua.module.english.controller.admin.expression.vo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExpressionItemSaveReqVO {
    private Long id;
    @NotNull(message = "主题不能为空")
    private Long themeId;
    @NotEmpty(message = "练习编码不能为空")
    private String code;
    @NotEmpty(message = "英文问句不能为空")
    private String promptEn;
    private String promptCn;
    private String functionCode;
    private String practiceMode;
    private Integer difficulty;
    @NotEmpty(message = "答案配置不能为空")
    private String answerJson;
    private String imageUrlsJson;
    private Integer sort;
    private Integer status;
}
