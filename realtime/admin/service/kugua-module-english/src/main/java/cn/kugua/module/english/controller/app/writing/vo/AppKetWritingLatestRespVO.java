package cn.kugua.module.english.controller.app.writing.vo;

import lombok.Data;

@Data
public class AppKetWritingLatestRespVO {

    private Integer attemptCount;
    private AppKetWritingAttemptRespVO latest;
}
