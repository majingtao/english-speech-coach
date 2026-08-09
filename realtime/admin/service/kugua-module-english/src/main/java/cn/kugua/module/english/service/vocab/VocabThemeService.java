package cn.kugua.module.english.service.vocab;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabThemePageReqVO;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabThemeSaveReqVO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabThemeDO;
import jakarta.validation.Valid;

import java.util.List;

public interface VocabThemeService {

    Long createTheme(@Valid VocabThemeSaveReqVO reqVO);

    void updateTheme(@Valid VocabThemeSaveReqVO reqVO);

    void deleteTheme(Long id);

    VocabThemeDO getTheme(Long id);

    VocabThemeDO getThemeByCode(String code);

    PageResult<VocabThemeDO> getThemePage(VocabThemePageReqVO reqVO);

    List<VocabThemeDO> getThemeListByLevel(String levelCode);

}
