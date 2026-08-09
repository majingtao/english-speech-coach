package cn.kugua.module.english.service.vocab;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabBatchImportReqVO;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabPageReqVO;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabSaveReqVO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabDO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabThemeDO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabThemeRelDO;
import cn.kugua.module.english.dal.mysql.vocab.UserVocabListItemMapper;
import cn.kugua.module.english.dal.mysql.vocab.UserVocabProgressMapper;
import cn.kugua.module.english.dal.mysql.vocab.VocabMapper;
import cn.kugua.module.english.dal.mysql.vocab.VocabThemeMapper;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.kugua.module.english.dal.mysql.vocab.VocabThemeRelMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class VocabServiceImpl implements VocabService {

    @Resource
    private VocabMapper vocabMapper;

    @Resource
    private VocabThemeMapper themeMapper;

    @Resource
    private VocabThemeRelMapper themeRelMapper;

    @Resource
    private UserVocabProgressMapper progressMapper;

    @Resource
    private UserVocabListItemMapper listItemMapper;

    @Resource
    private PyVocabClient pyClient;

    @Resource
    private FileApi fileApi;

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createVocab(VocabSaveReqVO reqVO) {
        validateUnique(null, reqVO.getLevelCode(), reqVO.getWord());
        VocabDO vocab = BeanUtils.toBean(reqVO, VocabDO.class);
        if (vocab.getStatus() == null) vocab.setStatus(0);
        if (vocab.getSort() == null) vocab.setSort(0);
        if (vocab.getDifficulty() == null) vocab.setDifficulty(1);
        vocabMapper.insert(vocab);
        replaceThemeRels(vocab.getId(), reqVO.getThemeIds());
        return vocab.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVocab(VocabSaveReqVO reqVO) {
        validateExists(reqVO.getId());
        validateUnique(reqVO.getId(), reqVO.getLevelCode(), reqVO.getWord());
        VocabDO update = BeanUtils.toBean(reqVO, VocabDO.class);
        vocabMapper.updateById(update);
        if (reqVO.getThemeIds() != null) {
            replaceThemeRels(reqVO.getId(), reqVO.getThemeIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVocab(Long id) {
        validateExists(id);
        // 物理删，避免软删时 (tenant_id, level_code, word, deleted) 唯一键重复冲突；
        // 同时清理所有引用该词条的下游表，避免 orphan：
        //   - 主题关联（esc_vocab_theme_rel）
        //   - 学员 SRS 进度（esc_user_vocab_progress）
        //   - 用户自建词库引用（esc_user_vocab_list_item）
        themeRelMapper.physicalDeleteByVocabId(id);
        progressMapper.physicalDeleteByVocabId(id);
        listItemMapper.physicalDeleteByVocabId(id);
        vocabMapper.physicalDeleteById(id);
    }

    @Override
    public VocabDO getVocab(Long id) {
        return vocabMapper.selectById(id);
    }

    @Override
    public VocabDO getOrGenerateContent(Long id) {
        VocabDO vocab = vocabMapper.selectById(id);
        if (vocab == null) {
            throw exception(VOCAB_NOT_EXISTS);
        }
        if (vocab.getContentJson() != null && !vocab.getContentJson().isBlank()) {
            return vocab;
        }
        List<String> themeCodes = resolveThemeCodes(id);
        String generated = pyClient.generateContent(vocab.getWord(), vocab.getLevelCode(), vocab.getPos(), themeCodes);
        if (generated != null && !generated.isBlank()) {
            vocab.setContentJson(generated);
            applyFormsFromGenerated(vocab, generated);
            vocabMapper.updateById(vocab);
            log.info("[vocab] generated and cached for id={} word={}", id, vocab.getWord());
        }
        return vocab;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void regenerateContent(Long id) {
        VocabDO vocab = vocabMapper.selectById(id);
        if (vocab == null) {
            throw exception(VOCAB_NOT_EXISTS);
        }
        List<String> themeCodes = resolveThemeCodes(id);
        String generated = pyClient.generateContent(vocab.getWord(), vocab.getLevelCode(), vocab.getPos(), themeCodes);
        if (generated == null || generated.isBlank()) {
            throw exception(VOCAB_GENERATE_FAILED);
        }
        vocab.setContentJson(generated);
        applyFormsFromGenerated(vocab, generated);
        vocabMapper.updateById(vocab);
    }

    @Override
    public String getOrGenerateAudio(Long id, String accent) {
        if (accent == null || (!"uk".equalsIgnoreCase(accent) && !"us".equalsIgnoreCase(accent))) {
            throw exception(VOCAB_AUDIO_ACCENT_INVALID);
        }
        String norm = accent.toLowerCase();
        VocabDO vocab = vocabMapper.selectById(id);
        if (vocab == null) {
            throw exception(VOCAB_NOT_EXISTS);
        }
        String existing = "uk".equals(norm) ? vocab.getAudioUkUrl() : vocab.getAudioUsUrl();
        if (existing != null && !existing.isBlank()) {
            return existing;
        }
        byte[] audio = pyClient.generateAudio(vocab.getWord(), norm);
        if (audio == null || audio.length == 0) {
            throw exception(VOCAB_AUDIO_GENERATE_FAILED);
        }
        String fileName = vocab.getId() + "_" + norm + ".mp3";
        String url = fileApi.createFile(audio, fileName, "english/vocab/audio", "audio/mpeg");
        if (url == null || url.isBlank()) {
            throw exception(VOCAB_AUDIO_GENERATE_FAILED);
        }
        if ("uk".equals(norm)) vocab.setAudioUkUrl(url);
        else vocab.setAudioUsUrl(url);
        vocabMapper.updateById(vocab);
        log.info("[vocab] audio generated id={} word={} accent={} url={}", id, vocab.getWord(), norm, url);
        return url;
    }

    /**
     * 从模板 content.audio[] 数组里抓首条 region=uk / region=us 的 URL，回填到 audioUkUrl / audioUsUrl。
     * 只在对应列尚未被显式赋值时填充（顶层 audioUkUrl 优先级更高）。
     */
    private void extractAudioUrlsFromContent(VocabDO vocab, Map<String, Object> content) {
        Object audio = content.get("audio");
        if (!(audio instanceof List<?> list)) return;
        for (Object entry : list) {
            if (!(entry instanceof Map<?, ?> m)) continue;
            Object regionObj = m.get("region");
            Object urlObj = m.get("url");
            if (!(regionObj instanceof String region) || !(urlObj instanceof String url)) continue;
            String urlStr = url.trim();
            if (urlStr.isEmpty()) continue;
            String r = region.toLowerCase();
            if ("uk".equals(r) && (vocab.getAudioUkUrl() == null || vocab.getAudioUkUrl().isBlank())) {
                vocab.setAudioUkUrl(urlStr);
            } else if ("us".equals(r) && (vocab.getAudioUsUrl() == null || vocab.getAudioUsUrl().isBlank())) {
                vocab.setAudioUsUrl(urlStr);
            }
        }
    }

    /**
     * 从 LLM 返回的 JSON 中拎出 `forms` 节点，写到 forms_json 列。
     * - LLM 没产出 forms / forms 为空对象 / 解析失败：保留 vocab 原有 formsJson 不动。
     * - 解析出非空 forms：覆盖写回，让"重新生成"也刷新词形。
     */
    private void applyFormsFromGenerated(VocabDO vocab, String generatedJson) {
        try {
            JsonNode root = jsonMapper.readTree(generatedJson);
            JsonNode forms = root.get("forms");
            if (forms == null || !forms.isObject() || forms.isEmpty()) {
                return;
            }
            vocab.setFormsJson(jsonMapper.writeValueAsString(forms));
        } catch (Exception e) {
            log.warn("[vocab] parse forms failed id={} err={}", vocab.getId(), e.getMessage());
        }
    }

    @Override
    public PageResult<VocabDO> getVocabPage(VocabPageReqVO reqVO) {
        if (reqVO.getThemeId() != null) {
            List<VocabThemeRelDO> rels = themeRelMapper.selectListByThemeId(reqVO.getThemeId());
            if (rels.isEmpty()) {
                return PageResult.empty();
            }
            List<Long> vocabIds = rels.stream().map(VocabThemeRelDO::getVocabId).collect(Collectors.toList());
            return vocabMapper.selectPage(reqVO, new LambdaQueryWrapperX<VocabDO>()
                    .in(VocabDO::getId, vocabIds)
                    .likeIfPresent(VocabDO::getWord, reqVO.getWord())
                    .eqIfPresent(VocabDO::getLevelCode, reqVO.getLevelCode())
                    .eqIfPresent(VocabDO::getDifficulty, reqVO.getDifficulty())
                    .eqIfPresent(VocabDO::getStatus, reqVO.getStatus())
                    .orderByAsc(VocabDO::getSort)
                    .orderByDesc(VocabDO::getId));
        }
        return vocabMapper.selectPage(reqVO);
    }

    @Override
    public List<VocabDO> getVocabListByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return vocabMapper.selectBatchIds(ids);
    }

    @Override
    public List<VocabDO> searchPublished(String levelCode, String keyword, int limit) {
        if (keyword == null || keyword.isBlank()) return Collections.emptyList();
        String kw = keyword.trim();
        int safeLimit = Math.max(1, Math.min(limit, 30));
        return vocabMapper.selectList(new LambdaQueryWrapperX<VocabDO>()
                .eq(VocabDO::getStatus, 1)
                .eqIfPresent(VocabDO::getLevelCode, (levelCode != null && !levelCode.isBlank()) ? levelCode : null)
                .likeRight(VocabDO::getWord, kw)
                .orderByAsc(VocabDO::getWord)
                .last("LIMIT " + safeLimit));
    }

    @Override
    public PageResult<VocabDO> getVocabBrowsePage(String levelCode, String themeCode, Integer difficulty, int pageNo, int pageSize) {
        VocabPageReqVO req = new VocabPageReqVO();
        req.setLevelCode(levelCode);
        req.setDifficulty(difficulty);
        req.setStatus(1);
        req.setPageNo(pageNo);
        req.setPageSize(pageSize);
        if (themeCode != null && !themeCode.isBlank()) {
            VocabThemeDO theme = themeMapper.selectByCode(themeCode);
            if (theme != null) req.setThemeId(theme.getId());
            else return PageResult.empty();
        }
        return getVocabPage(req);
    }

    @Override
    public List<Long> getThemeIdsByVocabId(Long vocabId) {
        return themeRelMapper.selectListByVocabId(vocabId).stream()
                .map(VocabThemeRelDO::getThemeId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchImport(VocabBatchImportReqVO reqVO) {
        if (reqVO.getItems() == null || reqVO.getItems().isEmpty()) return 0;
        int count = 0;
        for (VocabBatchImportReqVO.Item item : reqVO.getItems()) {
            if (item.getWord() == null || item.getWord().isBlank()) continue;
            String word = item.getWord().trim();
            // 级别解析：item.level 覆盖批次 levelCode；两者都空则跳过
            String level = (item.getLevel() != null && !item.getLevel().isBlank())
                    ? item.getLevel().trim()
                    : reqVO.getLevelCode();
            if (level == null || level.isBlank()) {
                log.warn("[vocab] batch import: skip word={} — no level (batch and item both empty)", word);
                continue;
            }
            if (vocabMapper.selectByLevelAndWord(level, word) != null) continue;
            VocabDO vocab = new VocabDO();
            vocab.setWord(word);
            vocab.setLevelCode(level);
            vocab.setPos(item.getPos() != null ? item.getPos() : "");
            vocab.setDifficulty(item.getDifficulty() != null ? item.getDifficulty() : 1);
            vocab.setStatus(item.getStatus() != null ? item.getStatus() : 1);
            vocab.setSort(0);
            // 能力要求：缺省 1（三会，保守）；2=四会会进听写题源
            vocab.setMasteryLevel(item.getMasteryLevel() != null ? item.getMasteryLevel() : 1);
            // CEFR：cefr 顶层字符串原样；cefr_list 把数组 join 成逗号串
            if (item.getCefr() != null && !item.getCefr().isBlank()) {
                vocab.setCefr(item.getCefr().trim());
            }
            if (item.getCefrList() != null && !item.getCefrList().isEmpty()) {
                vocab.setCefrList(item.getCefrList().stream()
                        .filter(c -> c != null && !c.isBlank())
                        .map(String::trim)
                        .collect(Collectors.joining(",")));
            }
            // content + forms（离线 LLM 生成的结果直接落库；同 regenerateContent 行为，由 forms 子节点抽出 forms_json）
            if (item.getContent() != null && !item.getContent().isEmpty()) {
                try {
                    String contentStr = jsonMapper.writeValueAsString(item.getContent());
                    vocab.setContentJson(contentStr);
                    applyFormsFromGenerated(vocab, contentStr);
                } catch (Exception e) {
                    log.warn("[vocab] batch import: serialize content failed word={} err={}", word, e.getMessage());
                }
            }
            // 顶层 audioUkUrl / audioUsUrl 显式优先
            if (item.getAudioUkUrl() != null && !item.getAudioUkUrl().isBlank()) {
                vocab.setAudioUkUrl(item.getAudioUkUrl().trim());
            }
            if (item.getAudioUsUrl() != null && !item.getAudioUsUrl().isBlank()) {
                vocab.setAudioUsUrl(item.getAudioUsUrl().trim());
            }
            // 顶层留空时，从 content.audio[] 抓首个 uk / us URL 兜底（兼容新模板）
            if (item.getContent() != null) {
                extractAudioUrlsFromContent(vocab, item.getContent());
            }
            vocabMapper.insert(vocab);
            if (item.getThemes() != null) {
                List<Long> themeIds = item.getThemes().stream()
                        .map(themeMapper::selectByCode)
                        .filter(t -> t != null)
                        .map(VocabThemeDO::getId)
                        .collect(Collectors.toList());
                replaceThemeRels(vocab.getId(), themeIds);
            }
            count++;
        }
        return count;
    }

    @Override
    public List<String> getThemeCodesByVocabId(Long vocabId) {
        List<Long> themeIds = getThemeIdsByVocabId(vocabId);
        if (themeIds.isEmpty()) return Collections.emptyList();
        return themeIds.stream()
                .map(themeMapper::selectById)
                .filter(t -> t != null)
                .map(VocabThemeDO::getCode)
                .collect(Collectors.toList());
    }

    private List<String> resolveThemeCodes(Long vocabId) {
        return getThemeCodesByVocabId(vocabId);
    }

    private void replaceThemeRels(Long vocabId, List<Long> themeIds) {
        themeRelMapper.deleteByVocabId(vocabId);
        if (themeIds == null || themeIds.isEmpty()) return;
        List<VocabThemeRelDO> toInsert = new ArrayList<>();
        for (Long themeId : themeIds) {
            VocabThemeRelDO rel = new VocabThemeRelDO();
            rel.setVocabId(vocabId);
            rel.setThemeId(themeId);
            toInsert.add(rel);
        }
        for (VocabThemeRelDO r : toInsert) themeRelMapper.insert(r);
    }

    private void validateExists(Long id) {
        if (vocabMapper.selectById(id) == null) {
            throw exception(VOCAB_NOT_EXISTS);
        }
    }

    private void validateUnique(Long id, String level, String word) {
        VocabDO exist = vocabMapper.selectByLevelAndWord(level, word);
        if (exist == null) return;
        if (id == null || !exist.getId().equals(id)) {
            throw exception(VOCAB_DUPLICATE);
        }
    }

}
