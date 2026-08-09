package cn.kugua.module.english.service.expression;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.controller.admin.expression.vo.ExpressionItemPageReqVO;
import cn.kugua.module.english.controller.admin.expression.vo.ExpressionItemSaveReqVO;
import cn.kugua.module.english.controller.admin.expression.vo.ExpressionThemeSaveReqVO;
import cn.kugua.module.english.controller.app.expression.vo.AppExpressionAttemptReqVO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionAttemptDO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionItemDO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionThemeDO;
import cn.kugua.module.english.dal.dataobject.expression.UserExpressionProgressDO;
import cn.kugua.module.english.dal.mysql.expression.ExpressionAttemptMapper;
import cn.kugua.module.english.dal.mysql.expression.ExpressionItemMapper;
import cn.kugua.module.english.dal.mysql.expression.ExpressionThemeMapper;
import cn.kugua.module.english.dal.mysql.expression.UserExpressionProgressMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

@Service
public class ExpressionServiceImpl implements ExpressionService {

    private static final int[] REVIEW_INTERVALS = {1, 2, 4, 7, 15, 30};

    @Resource
    private ExpressionThemeMapper themeMapper;
    @Resource
    private ExpressionItemMapper itemMapper;
    @Resource
    private UserExpressionProgressMapper progressMapper;
    @Resource
    private ExpressionAttemptMapper attemptMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Long createTheme(ExpressionThemeSaveReqVO reqVO) {
        validateThemeUnique(null, reqVO.getLevelCode(), reqVO.getCode());
        ExpressionThemeDO theme = BeanUtils.toBean(reqVO, ExpressionThemeDO.class);
        applyThemeDefaults(theme);
        themeMapper.insert(theme);
        return theme.getId();
    }

    @Override
    public void updateTheme(ExpressionThemeSaveReqVO reqVO) {
        validateThemeExists(reqVO.getId());
        validateThemeUnique(reqVO.getId(), reqVO.getLevelCode(), reqVO.getCode());
        ExpressionThemeDO theme = BeanUtils.toBean(reqVO, ExpressionThemeDO.class);
        applyThemeDefaults(theme);
        themeMapper.updateById(theme);
    }

    @Override
    public void deleteTheme(Long id) {
        validateThemeExists(id);
        if (itemMapper.selectCount(new LambdaQueryWrapperX<ExpressionItemDO>()
                .eq(ExpressionItemDO::getThemeId, id)) > 0) {
            throw exception(EXPRESSION_THEME_IN_USE);
        }
        themeMapper.deleteById(id);
    }

    @Override
    public List<ExpressionThemeDO> getEnabledThemes(String levelCode) {
        return themeMapper.selectEnabledByLevel(levelCode);
    }

    @Override
    public List<ExpressionThemeDO> getAllThemes(String levelCode) {
        return themeMapper.selectList(new LambdaQueryWrapperX<ExpressionThemeDO>()
                .eqIfPresent(ExpressionThemeDO::getLevelCode, levelCode)
                .orderByAsc(ExpressionThemeDO::getSort)
                .orderByAsc(ExpressionThemeDO::getId));
    }

    @Override
    public Long createItem(ExpressionItemSaveReqVO reqVO) {
        validateThemeExists(reqVO.getThemeId());
        validateItemUnique(null, reqVO.getCode());
        validateAnswerJson(reqVO.getAnswerJson());
        ExpressionItemDO item = BeanUtils.toBean(reqVO, ExpressionItemDO.class);
        applyItemDefaults(item);
        itemMapper.insert(item);
        return item.getId();
    }

    @Override
    public void updateItem(ExpressionItemSaveReqVO reqVO) {
        validateItemExists(reqVO.getId());
        validateThemeExists(reqVO.getThemeId());
        validateItemUnique(reqVO.getId(), reqVO.getCode());
        validateAnswerJson(reqVO.getAnswerJson());
        ExpressionItemDO item = BeanUtils.toBean(reqVO, ExpressionItemDO.class);
        applyItemDefaults(item);
        itemMapper.updateById(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        validateItemExists(id);
        itemMapper.deleteById(id);
    }

    @Override
    public ExpressionItemDO getItem(Long id) {
        return itemMapper.selectById(id);
    }

    @Override
    public PageResult<ExpressionItemDO> getItemPage(ExpressionItemPageReqVO reqVO) {
        return itemMapper.selectPage(reqVO, reqVO.getThemeId(), reqVO.getPrompt(), reqVO.getStatus());
    }

    @Override
    public List<ExpressionItemDO> getPublishedItems(String levelCode, String themeCode) {
        ExpressionThemeDO theme = themeMapper.selectByCode(levelCode, themeCode);
        if (theme == null || !Integer.valueOf(1).equals(theme.getStatus())) {
            throw exception(EXPRESSION_THEME_NOT_EXISTS);
        }
        return itemMapper.selectPublishedByTheme(theme.getId());
    }

    @Override
    public UserExpressionProgressDO getProgress(Long userId, Long itemId) {
        return progressMapper.selectByUserAndItem(userId, itemId);
    }

    @Override
    @Transactional
    public ExpressionAttemptDO submitAttempt(Long userId, Long itemId, AppExpressionAttemptReqVO reqVO) {
        validateItemExists(itemId);
        String mode = reqVO.getPracticeMode().trim().toLowerCase();
        if (!"speaking".equals(mode) && !"writing".equals(mode)) {
            throw exception(EXPRESSION_MODE_INVALID);
        }

        int score = reqVO.getScore() == null ? 0 : reqVO.getScore();
        ExpressionAttemptDO attempt = BeanUtils.toBean(reqVO, ExpressionAttemptDO.class);
        attempt.setUserId(userId);
        attempt.setExpressionItemId(itemId);
        attempt.setPracticeMode(mode);
        attempt.setScore(score);
        attemptMapper.insert(attempt);

        UserExpressionProgressDO progress = progressMapper.selectByUserAndItem(userId, itemId);
        boolean creating = progress == null;
        if (creating) {
            progress = new UserExpressionProgressDO();
            progress.setUserId(userId);
            progress.setExpressionItemId(itemId);
            progress.setStatus(0);
            progress.setRepetitions(0);
            progress.setIntervalDays(0);
            progress.setAttemptCount(0);
        }
        updateProgress(progress, mode, score);
        if (creating) progressMapper.insert(progress); else progressMapper.updateById(progress);
        return attempt;
    }

    private void updateProgress(UserExpressionProgressDO progress, String mode, int score) {
        LocalDateTime now = LocalDateTime.now();
        int repetitions = progress.getRepetitions() == null ? 0 : progress.getRepetitions();
        if (score >= 80) {
            repetitions++;
            int interval = REVIEW_INTERVALS[Math.min(repetitions - 1, REVIEW_INTERVALS.length - 1)];
            progress.setRepetitions(repetitions);
            progress.setIntervalDays(interval);
            progress.setStatus(repetitions >= 4 ? 2 : 1);
            progress.setNextReviewAt(now.plusDays(interval));
        } else if (score >= 60) {
            progress.setStatus(1);
            progress.setIntervalDays(1);
            progress.setNextReviewAt(now.plusDays(1));
        } else {
            progress.setStatus(1);
            progress.setRepetitions(0);
            progress.setIntervalDays(0);
            progress.setNextReviewAt(now);
        }
        if ("speaking".equals(mode)) {
            progress.setBestSpeakingScore(max(progress.getBestSpeakingScore(), score));
        } else {
            progress.setBestWritingScore(max(progress.getBestWritingScore(), score));
        }
        progress.setAttemptCount((progress.getAttemptCount() == null ? 0 : progress.getAttemptCount()) + 1);
        progress.setLastPracticeAt(now);
    }

    private int max(Integer current, int score) {
        return current == null ? score : Math.max(current, score);
    }

    private void applyThemeDefaults(ExpressionThemeDO theme) {
        if (theme.getDescription() == null) theme.setDescription("");
        if (theme.getCoverUrl() == null) theme.setCoverUrl("");
        if (theme.getSort() == null) theme.setSort(0);
        if (theme.getStatus() == null) theme.setStatus(1);
    }

    private void applyItemDefaults(ExpressionItemDO item) {
        if (item.getPromptCn() == null) item.setPromptCn("");
        if (item.getFunctionCode() == null) item.setFunctionCode("");
        if (item.getPracticeMode() == null || item.getPracticeMode().isBlank()) item.setPracticeMode("both");
        if (item.getDifficulty() == null) item.setDifficulty(1);
        if (item.getSort() == null) item.setSort(0);
        if (item.getStatus() == null) item.setStatus(0);
    }

    private void validateAnswerJson(String answerJson) {
        try {
            objectMapper.readTree(answerJson);
        } catch (Exception e) {
            throw new IllegalArgumentException("答案配置不是有效 JSON", e);
        }
    }

    private void validateThemeExists(Long id) {
        if (id == null || themeMapper.selectById(id) == null) throw exception(EXPRESSION_THEME_NOT_EXISTS);
    }

    private void validateItemExists(Long id) {
        if (id == null || itemMapper.selectById(id) == null) throw exception(EXPRESSION_ITEM_NOT_EXISTS);
    }

    private void validateThemeUnique(Long id, String levelCode, String code) {
        ExpressionThemeDO existing = themeMapper.selectByCode(levelCode, code);
        if (existing != null && (id == null || !id.equals(existing.getId()))) throw exception(EXPRESSION_THEME_DUPLICATE);
    }

    private void validateItemUnique(Long id, String code) {
        ExpressionItemDO existing = itemMapper.selectOne(new LambdaQueryWrapperX<ExpressionItemDO>()
                .eq(ExpressionItemDO::getCode, code));
        if (existing != null && (id == null || !id.equals(existing.getId()))) throw exception(EXPRESSION_ITEM_DUPLICATE);
    }
}
