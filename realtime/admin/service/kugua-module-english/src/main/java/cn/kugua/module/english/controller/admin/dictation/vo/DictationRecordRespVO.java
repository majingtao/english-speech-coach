package cn.kugua.module.english.controller.admin.dictation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 听写记录 Response VO")
@Data
public class DictationRecordRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "单词ID")
    private Long wordId;

    @Schema(description = "词书ID")
    private Long wordlistId;

    @Schema(description = "是否认识")
    private Boolean knowMeaning;

    @Schema(description = "是否会拼")
    private Boolean canSpell;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
