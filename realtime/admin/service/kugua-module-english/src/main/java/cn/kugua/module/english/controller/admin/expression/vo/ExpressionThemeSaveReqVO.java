package cn.kugua.module.english.controller.admin.expression.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ExpressionThemeSaveReqVO {
    private Long id;
    @NotEmpty(message = "主题编码不能为空")
    private String code;
    @NotEmpty(message = "中文名称不能为空")
    private String nameCn;
    @NotEmpty(message = "英文名称不能为空")
    private String nameEn;
    private String description;
    @NotEmpty(message = "级别不能为空")
    private String levelCode;
    private String coverUrl;
    private Integer sort;
    private Integer status;
}
