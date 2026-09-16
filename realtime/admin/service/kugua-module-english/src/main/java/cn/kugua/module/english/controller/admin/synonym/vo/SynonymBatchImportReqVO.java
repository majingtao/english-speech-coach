package cn.kugua.module.english.controller.admin.synonym.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class SynonymBatchImportReqVO {
    @NotEmpty @Valid private List<SynonymPointSaveReqVO> points;
    private Boolean publish = true;
}
