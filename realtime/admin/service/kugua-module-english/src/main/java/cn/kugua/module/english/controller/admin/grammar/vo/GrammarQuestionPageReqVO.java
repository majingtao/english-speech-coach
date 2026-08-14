package cn.kugua.module.english.controller.admin.grammar.vo;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.*;
@Data @EqualsAndHashCode(callSuper = true)
public class GrammarQuestionPageReqVO extends PageParam {
    private Long grammarPointId;
    private Integer difficulty;
    private String questionType;
    private Integer status;
}
