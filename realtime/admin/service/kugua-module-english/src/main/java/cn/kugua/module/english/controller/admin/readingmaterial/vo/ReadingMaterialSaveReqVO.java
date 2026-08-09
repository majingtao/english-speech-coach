package cn.kugua.module.english.controller.admin.readingmaterial.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReadingMaterialSaveReqVO {
    private Long id;
    @NotEmpty(message = "英文内容不能为空")
    private String textEn;
    private String textCn;
    private String description;
    private String materialType;
    private String partOfSpeech;
    private String levelCode;
    private String tagsJson;
    private String examplesJson;
    private String wordFormsJson;
    private Integer sort;
    private Integer status;
}
