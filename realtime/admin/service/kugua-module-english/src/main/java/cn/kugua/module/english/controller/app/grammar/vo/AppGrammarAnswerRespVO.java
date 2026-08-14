package cn.kugua.module.english.controller.app.grammar.vo;
import lombok.Data;
import java.util.List;
@Data
public class AppGrammarAnswerRespVO {
    private Boolean correct;
    private List<String> acceptedAnswers;
    private String explanationZh;
    private String ruleText;
    private String errorHint;
}
