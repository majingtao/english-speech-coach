package cn.kugua.module.english.controller.app.synonym.vo;

import lombok.Data;

@Data
public class AppSynonymStatsRespVO {
    private Integer total;
    private Integer synonymTotal;
    private Integer antonymTotal;
    private Integer done;
    private Integer learning;
    private Integer newCount;
}
