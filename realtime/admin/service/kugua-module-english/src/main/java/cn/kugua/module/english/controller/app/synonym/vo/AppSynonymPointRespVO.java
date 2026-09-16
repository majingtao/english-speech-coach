package cn.kugua.module.english.controller.app.synonym.vo;

import lombok.Data;

@Data
public class AppSynonymPointRespVO {
    private Long id;
    private String code;
    private String mode;
    private String levelCode;
    private String source;
    private String sectionName;
    private String leftText;
    private String leftCn;
    private String rightText;
    private String rightCn;
    private Integer sort;
    private Integer seenCount;
    private Integer correctCount;
    private Integer wrongCount;
    private Boolean mastered;
    private String status;
}
