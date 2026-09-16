package cn.kugua.module.english.controller.app.readingmaterial.vo;

import lombok.Data;

@Data
public class AppReadingMaterialProgressRespVO {
    private Long materialId;
    private Integer readCorrectCount;
    private Integer readWrongCount;
    private Integer spellCorrectCount;
    private Integer spellWrongCount;
    private String lastMode;
    private String lastResult;
    private String lastPracticeAt;
}
