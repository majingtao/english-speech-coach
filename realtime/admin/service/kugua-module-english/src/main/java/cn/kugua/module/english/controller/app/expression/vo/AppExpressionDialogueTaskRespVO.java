package cn.kugua.module.english.controller.app.expression.vo;

import lombok.Data;

@Data
public class AppExpressionDialogueTaskRespVO {
    private Long id;
    private String code;
    private String levelCode;
    private String titleCn;
    private String titleEn;
    private String topicCode;
    private String promptEn;
    private String promptCn;
    private String configJson;
    private Integer sort;
}
