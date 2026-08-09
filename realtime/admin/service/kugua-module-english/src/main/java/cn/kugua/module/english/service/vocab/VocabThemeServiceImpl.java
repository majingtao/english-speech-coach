package cn.kugua.module.english.service.vocab;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabThemePageReqVO;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabThemeSaveReqVO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabThemeDO;
import cn.kugua.module.english.dal.mysql.vocab.VocabThemeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

@Service
@Validated
public class VocabThemeServiceImpl implements VocabThemeService {

    @Resource
    private VocabThemeMapper themeMapper;

    @Override
    public Long createTheme(VocabThemeSaveReqVO reqVO) {
        validateCodeUnique(null, reqVO.getCode());
        VocabThemeDO theme = BeanUtils.toBean(reqVO, VocabThemeDO.class);
        if (theme.getStatus() == null) theme.setStatus(1);
        if (theme.getSort() == null) theme.setSort(0);
        themeMapper.insert(theme);
        return theme.getId();
    }

    @Override
    public void updateTheme(VocabThemeSaveReqVO reqVO) {
        validateExists(reqVO.getId());
        validateCodeUnique(reqVO.getId(), reqVO.getCode());
        VocabThemeDO update = BeanUtils.toBean(reqVO, VocabThemeDO.class);
        themeMapper.updateById(update);
    }

    @Override
    public void deleteTheme(Long id) {
        validateExists(id);
        themeMapper.deleteById(id);
    }

    @Override
    public VocabThemeDO getTheme(Long id) {
        return themeMapper.selectById(id);
    }

    @Override
    public VocabThemeDO getThemeByCode(String code) {
        return themeMapper.selectByCode(code);
    }

    @Override
    public PageResult<VocabThemeDO> getThemePage(VocabThemePageReqVO reqVO) {
        return themeMapper.selectPage(reqVO);
    }

    @Override
    public List<VocabThemeDO> getThemeListByLevel(String levelCode) {
        return themeMapper.selectListByLevel(levelCode);
    }

    private void validateExists(Long id) {
        if (themeMapper.selectById(id) == null) {
            throw exception(VOCAB_THEME_NOT_EXISTS);
        }
    }

    private void validateCodeUnique(Long id, String code) {
        VocabThemeDO exist = themeMapper.selectByCode(code);
        if (exist == null) return;
        if (id == null || !exist.getId().equals(id)) {
            throw exception(VOCAB_THEME_CODE_DUPLICATE);
        }
    }

}
