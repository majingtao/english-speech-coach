package cn.kugua.module.english.controller.app.vocab;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.kugua.module.english.controller.app.vocab.vo.*;
import cn.kugua.module.english.dal.dataobject.vocab.UserVocabProgressDO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabDO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabThemeDO;
import cn.kugua.module.english.service.vocab.PyVocabClient;
import cn.kugua.module.english.service.vocab.UserVocabProgressService;
import cn.kugua.module.english.service.vocab.VocabService;
import cn.kugua.module.english.service.vocab.VocabThemeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.kugua.module.english.enums.ErrorCodeConstants.VOCAB_NOT_EXISTS;

/**
 * H5 学员端 - 词汇接口
 */
@Tag(name = "H5 - 词汇")
@RestController
@RequestMapping("/english/vocab")
@Validated
public class AppVocabController {

    @Resource
    private VocabService vocabService;

    @Resource
    private VocabThemeService themeService;

    @Resource
    private UserVocabProgressService progressService;

    @Resource
    private PyVocabClient pyClient;

    @PostMapping("/enroll-new")
    @Operation(summary = "把已发布词库中尚未学习的词批量加入 SRS 复习队列（受当日新词上限约束，支持按主题筛选）")
    public CommonResult<Map<String, Integer>> enrollNew(
            @RequestParam(value = "level", defaultValue = "ket") String level,
            @RequestParam(value = "themeCode", required = false) String themeCode,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        int safeLimit = Math.min(UserVocabProgressService.DAILY_NEW_WORD_CAP, Math.max(1, limit));
        int count = progressService.enrollNewWords(userId, level, themeCode, safeLimit);
        long enrolledToday = progressService.countEnrolledToday(userId);
        Map<String, Integer> data = new HashMap<>();
        data.put("count", count);
        data.put("enrolledToday", (int) enrolledToday);
        data.put("dailyCap", UserVocabProgressService.DAILY_NEW_WORD_CAP);
        return success(data);
    }

    @PostMapping("/enroll-wordbook")
    @Operation(summary = "从我的生词本（待学池）把尚未学习的词加入 SRS 队列（受当日新词上限约束）")
    public CommonResult<Map<String, Integer>> enrollWordbook(
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        int safeLimit = Math.min(UserVocabProgressService.DAILY_NEW_WORD_CAP, Math.max(1, limit));
        int count = progressService.enrollFromWordbook(userId, safeLimit);
        long enrolledToday = progressService.countEnrolledToday(userId);
        Map<String, Integer> data = new HashMap<>();
        data.put("count", count);
        data.put("enrolledToday", (int) enrolledToday);
        data.put("dailyCap", UserVocabProgressService.DAILY_NEW_WORD_CAP);
        return success(data);
    }

    @GetMapping("/today-review")
    @Operation(summary = "今日 SRS 复习队列")
    public CommonResult<List<AppVocabListItemVO>> todayReview(
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<UserVocabProgressDO> list = progressService.getTodayReview(userId, limit);
        if (list.isEmpty()) return success(List.of());
        List<Long> vocabIds = list.stream().map(UserVocabProgressDO::getVocabId).toList();
        List<VocabDO> vocabs = vocabService.getVocabListByIds(vocabIds);
        Map<Long, VocabDO> vocabMap = new HashMap<>();
        for (VocabDO v : vocabs) vocabMap.put(v.getId(), v);
        List<AppVocabListItemVO> result = new ArrayList<>(list.size());
        for (UserVocabProgressDO p : list) {
            VocabDO v = vocabMap.get(p.getVocabId());
            if (v == null) continue;
            result.add(toListItem(v, p));
        }
        return success(result);
    }

    @GetMapping("/search")
    @Operation(summary = "按单词前缀搜索已发布词库（生词本联想补全 + 校验）")
    public CommonResult<List<AppVocabListItemVO>> search(
            @RequestParam("word") String word,
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<VocabDO> vocabs = vocabService.searchPublished(level, word, limit);
        List<AppVocabListItemVO> result = new ArrayList<>(vocabs.size());
        for (VocabDO v : vocabs) result.add(toListItem(v, null));
        return success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取词条详情（懒生成 content_json）")
    public CommonResult<AppVocabRespVO> get(@PathVariable("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        VocabDO vocab = vocabService.getOrGenerateContent(id);
        if (vocab == null) throw exception(VOCAB_NOT_EXISTS);
        AppVocabRespVO vo = new AppVocabRespVO();
        vo.setId(vocab.getId());
        vo.setWord(vocab.getWord());
        vo.setLevelCode(vocab.getLevelCode());
        vo.setPos(vocab.getPos());
        vo.setDifficulty(vocab.getDifficulty());
        vo.setContentJson(vocab.getContentJson());
        vo.setFormsJson(vocab.getFormsJson());
        vo.setAudioUkUrl(vocab.getAudioUkUrl());
        vo.setAudioUsUrl(vocab.getAudioUsUrl());
        vo.setThemeCodes(vocabService.getThemeCodesByVocabId(id));
        UserVocabProgressDO progress = progressService.getProgress(userId, id);
        if (progress != null) {
            vo.setProgressStatus(progress.getStatus());
            vo.setRepetitions(progress.getRepetitions());
            if (progress.getNextReviewAt() != null) {
                vo.setNextReviewAt(progress.getNextReviewAt().toString());
            }
        }
        return success(vo);
    }

    @PostMapping("/{id}/audio")
    @Operation(summary = "懒获取词条发音 URL（首次触发 TTS 生成 + 上传文件服务）")
    public CommonResult<Map<String, String>> ensureAudio(
            @PathVariable("id") Long id,
            @RequestParam(value = "accent", defaultValue = "uk") String accent) {
        String url = vocabService.getOrGenerateAudio(id, accent);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        data.put("accent", accent);
        return success(data);
    }

    @PostMapping("/{id}/review")
    @Operation(summary = "提交 SRS 复习结果")
    public CommonResult<AppVocabReviewRespVO> submitReview(
            @PathVariable("id") Long id,
            @Valid @RequestBody AppVocabReviewReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        UserVocabProgressDO progress = progressService.submitReview(userId, id, reqVO.getRemembered());
        AppVocabReviewRespVO resp = new AppVocabReviewRespVO();
        resp.setStatus(progress.getStatus());
        resp.setRepetitions(progress.getRepetitions());
        resp.setIntervalDays(progress.getIntervalDays());
        resp.setCorrectCount(progress.getCorrectCount());
        resp.setWrongCount(progress.getWrongCount());
        if (progress.getNextReviewAt() != null) {
            resp.setNextReviewAt(progress.getNextReviewAt().toString());
        }
        return success(resp);
    }

    @GetMapping("/theme/list")
    @Operation(summary = "主题列表（按级别）")
    @Parameter(name = "level", description = "级别 code，如 ket")
    public CommonResult<List<AppVocabThemeRespVO>> themeList(
            @RequestParam(value = "level", required = false) String level) {
        List<VocabThemeDO> themes = themeService.getThemeListByLevel(level);
        List<AppVocabThemeRespVO> result = new ArrayList<>(themes.size());
        for (VocabThemeDO t : themes) {
            AppVocabThemeRespVO vo = new AppVocabThemeRespVO();
            vo.setId(t.getId());
            vo.setCode(t.getCode());
            vo.setNameCn(t.getNameCn());
            vo.setNameEn(t.getNameEn());
            vo.setLevelCode(t.getLevelCode());
            vo.setSort(t.getSort());
            result.add(vo);
        }
        return success(result);
    }

    @GetMapping("/list")
    @Operation(summary = "按级别 + 主题 + 难度浏览词条")
    public CommonResult<PageResult<AppVocabListItemVO>> browse(
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "themeCode", required = false) String themeCode,
            @RequestParam(value = "difficulty", required = false) Integer difficulty,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        PageResult<VocabDO> page = vocabService.getVocabBrowsePage(level, themeCode, difficulty, pageNo, pageSize);
        List<AppVocabListItemVO> list = new ArrayList<>(page.getList().size());
        for (VocabDO v : page.getList()) {
            UserVocabProgressDO p = progressService.getProgress(userId, v.getId());
            list.add(toListItem(v, p));
        }
        PageResult<AppVocabListItemVO> result = new PageResult<>();
        result.setList(list);
        result.setTotal(page.getTotal());
        return success(result);
    }

    @GetMapping("/word-list")
    @Operation(summary = "词表：按级别分页列出全部已发布单词（字母序，可按主题筛选 + 前缀搜索）")
    public CommonResult<PageResult<AppVocabListItemVO>> wordList(
            @RequestParam(value = "level", defaultValue = "ket") String level,
            @RequestParam(value = "themeCode", required = false) String themeCode,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        PageResult<VocabDO> page = vocabService.getPublishedWordPage(level, themeCode, keyword,
                pageNo == null ? 1 : pageNo, pageSize == null ? 30 : pageSize);
        List<Long> ids = page.getList().stream().map(VocabDO::getId).toList();
        Map<Long, UserVocabProgressDO> progressMap = progressService.getProgressMap(userId, ids);
        List<AppVocabListItemVO> list = new ArrayList<>(page.getList().size());
        for (VocabDO v : page.getList()) {
            AppVocabListItemVO item = toListItem(v, progressMap.get(v.getId()));
            item.setMasteryLevel(v.getMasteryLevel());
            fillSummary(item, v.getContentJson());
            list.add(item);
        }
        return success(new PageResult<>(list, page.getTotal()));
    }

    private static final ObjectMapper JSON = new ObjectMapper();

    /** 从 content_json 里取中文释义 + 音标；兼容顶层 definition_cn 和 entries[].definitions[] 两种结构 */
    private static void fillSummary(AppVocabListItemVO item, String contentJson) {
        if (contentJson == null || contentJson.isBlank()) return;
        try {
            JsonNode root = JSON.readTree(contentJson);
            String ipa = root.path("ipa").asText("");
            if (!ipa.isBlank()) item.setIpa(ipa);
            String cn = root.path("definition_cn").asText("");
            if (cn.isBlank()) {
                List<String> parts = new ArrayList<>();
                for (JsonNode entry : root.path("entries")) {
                    for (JsonNode def : entry.path("definitions")) {
                        String d = def.path("definition_cn").asText("");
                        if (!d.isBlank() && !parts.contains(d)) parts.add(d);
                        if (parts.size() >= 3) break;
                    }
                    if (parts.size() >= 3) break;
                }
                cn = String.join("；", parts);
            }
            if (!cn.isBlank()) item.setDefinitionCn(cn);
        } catch (Exception ignored) {
            // content_json 异常时列表照常返回，只是不带释义
        }
    }

    @PostMapping("/{id}/practice-sentence")
    @Operation(summary = "造句练习（AI 批改）")
    public CommonResult<String> practiceSentence(
            @PathVariable("id") Long id,
            @Valid @RequestBody AppVocabPracticeSentenceReqVO reqVO) {
        VocabDO vocab = vocabService.getVocab(id);
        if (vocab == null) throw exception(VOCAB_NOT_EXISTS);
        String result = pyClient.gradeSentence(vocab.getWord(), reqVO.getSentence(), vocab.getLevelCode());
        return success(result);
    }

    private AppVocabListItemVO toListItem(VocabDO v, UserVocabProgressDO p) {
        AppVocabListItemVO item = new AppVocabListItemVO();
        item.setId(v.getId());
        item.setWord(v.getWord());
        item.setLevelCode(v.getLevelCode());
        item.setPos(v.getPos());
        item.setDifficulty(v.getDifficulty());
        if (p != null) {
            item.setProgressStatus(p.getStatus());
            item.setRepetitions(p.getRepetitions());
        }
        return item;
    }

}
