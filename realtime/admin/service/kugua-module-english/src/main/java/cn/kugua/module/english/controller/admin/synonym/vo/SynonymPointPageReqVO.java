package cn.kugua.module.english.controller.admin.synonym.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class SynonymPointPageReqVO extends PageParam {
    private String levelCode;
    private String mode;
    private String source;
    private Integer status;
}
