package cn.kugua.module.english.controller.app.readingmaterial.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AppReadingMaterialSelfCheckReqVO {
    @NotEmpty(message = "自评结果不能为空")
    private String result;
}
