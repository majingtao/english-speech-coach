package cn.kugua.module.english.controller.app.readingmaterial.vo;

import lombok.Data;

@Data
public class AppReadingMaterialProgressRespVO {
    private Long materialId;
    private Integer correctCount;
    private Integer wrongCount;
    private String lastResult;
    private String lastPracticeAt;
}
