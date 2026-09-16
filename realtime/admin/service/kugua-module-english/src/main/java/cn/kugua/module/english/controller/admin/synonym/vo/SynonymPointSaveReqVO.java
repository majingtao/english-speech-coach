package cn.kugua.module.english.controller.admin.synonym.vo;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SynonymPointSaveReqVO {
    private Long id;
    @NotEmpty private String code;
    @NotEmpty private String mode;
    @NotEmpty private String levelCode;
    @NotEmpty private String source;
    @NotEmpty private String sectionName;
    @NotEmpty private String leftText;
    private String leftCn;
    @NotEmpty private String rightText;
    private String rightCn;
    private Integer sort;
    private Integer status;
}
