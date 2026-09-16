package cn.kugua.module.english.service.synonym;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.synonym.vo.*;
import cn.kugua.module.english.controller.app.synonym.vo.*;
import cn.kugua.module.english.dal.dataobject.synonym.SynonymPointDO;
import java.util.List;

public interface SynonymService {
    PageResult<SynonymPointDO> getPointPage(SynonymPointPageReqVO req);
    SynonymPointDO getPoint(Long id);
    Long createPoint(SynonymPointSaveReqVO req);
    void updatePoint(SynonymPointSaveReqVO req);
    void deletePoint(Long id);
    Integer importPoints(SynonymBatchImportReqVO req);
    List<AppSynonymPointRespVO> getPracticeQueue(Long userId, String level, String mode);
    AppSynonymStatsRespVO getStats(Long userId, String level);
    AppSynonymAnswerRespVO answer(Long userId, Long pointId, AppSynonymAnswerReqVO req);
    String getAudioUrl(String level, String text, String accent);
}
