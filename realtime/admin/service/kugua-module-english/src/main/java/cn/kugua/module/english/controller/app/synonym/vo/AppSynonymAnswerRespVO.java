package cn.kugua.module.english.controller.app.synonym.vo;

import lombok.Data;

@Data
public class AppSynonymAnswerRespVO {
    private Boolean correct;
    private String correctAnswer;
    private String correctAnswerCn;
    private String explanation;
    private Integer seenCount;
    private Integer correctCount;
    private Integer wrongCount;
    private Boolean mastered;
}
