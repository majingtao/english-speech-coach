package cn.kugua.module.english.controller.app.grammar.vo;
import lombok.Data;
@Data
public class AppGrammarQuestionRespVO {
    private Long id;
    private Long grammarPointId;
    private String questionType;
    private Integer difficulty;
    private String instruction;
    private String stem;
    private String optionsJson;
    private String mediaJson;
}
