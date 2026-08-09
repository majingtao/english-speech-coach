package cn.kugua.module.english.controller.admin.readingmaterial.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReadingMaterialPageReqVO extends PageParam {
    private String text;
    private String materialType;
    private String partOfSpeech;
    private String levelCode;
    private String tag;
    private Integer status;
}
