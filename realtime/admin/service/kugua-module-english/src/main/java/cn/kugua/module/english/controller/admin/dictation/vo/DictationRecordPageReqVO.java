package cn.kugua.module.english.controller.admin.dictation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 听写记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DictationRecordPageReqVO extends PageParam {

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "单词ID")
    private Long wordId;

    @Schema(description = "词书ID")
    private Long wordlistId;

}
