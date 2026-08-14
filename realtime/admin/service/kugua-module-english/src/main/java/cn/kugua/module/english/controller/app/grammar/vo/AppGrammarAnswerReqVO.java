package cn.kugua.module.english.controller.app.grammar.vo;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class AppGrammarAnswerReqVO {
    @NotEmpty private String answer;
    private Integer durationSeconds;
}
