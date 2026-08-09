package cn.kugua.module.english.controller.app.readingmaterial.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppReadingMaterialPageReqVO extends PageParam {
    private String level;
    private String materialType;
    private String tag;
    private String priority;
}
