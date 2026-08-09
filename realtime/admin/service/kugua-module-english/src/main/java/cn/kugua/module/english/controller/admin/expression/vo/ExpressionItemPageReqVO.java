package cn.kugua.module.english.controller.admin.expression.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExpressionItemPageReqVO extends PageParam {
    private Long themeId;
    private String prompt;
    private Integer status;
}
