package cn.kugua.module.english.controller.admin.personalpractice.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonalPracticePageReqVO extends PageParam {
    private String practiceType;
    private String title;
    private Integer status;
}
