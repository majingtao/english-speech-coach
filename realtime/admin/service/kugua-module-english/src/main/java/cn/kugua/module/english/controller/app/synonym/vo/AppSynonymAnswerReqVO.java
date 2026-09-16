package cn.kugua.module.english.controller.app.synonym.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AppSynonymAnswerReqVO {
    @NotEmpty private String answer;
    private Integer durationSeconds;
}
