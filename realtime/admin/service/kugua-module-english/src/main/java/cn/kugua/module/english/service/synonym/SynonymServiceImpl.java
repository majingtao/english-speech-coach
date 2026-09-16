package cn.kugua.module.english.service.synonym;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.synonym.vo.*;
import cn.kugua.module.english.controller.app.synonym.vo.*;
import cn.kugua.module.english.dal.dataobject.synonym.*;
import cn.kugua.module.english.dal.dataobject.vocab.VocabDO;
import cn.kugua.module.english.dal.mysql.synonym.*;
import cn.kugua.module.english.dal.mysql.vocab.VocabMapper;
import cn.kugua.module.english.service.vocab.PyVocabClient;
import cn.kugua.module.english.service.vocab.VocabService;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SynonymServiceImpl implements SynonymService {
    @Resource private SynonymPointMapper pointMapper;
    @Resource private UserSynonymProgressMapper progressMapper;
    @Resource private SynonymAttemptMapper attemptMapper;
    @Resource private VocabMapper vocabMapper;
    @Resource private VocabService vocabService;
    @Resource private PyVocabClient pyClient;
    @Resource private FileApi fileApi;

    public PageResult<SynonymPointDO> getPointPage(SynonymPointPageReqVO req) {
        return pointMapper.selectPage(req, normalizeLevel(req.getLevelCode()), normalizeMode(req.getMode()), req.getSource(), req.getStatus());
    }

    public SynonymPointDO getPoint(Long id) {
        return requirePoint(id);
    }

    public Long createPoint(SynonymPointSaveReqVO req) {
        SynonymPointDO point = prepare(req);
        pointMapper.insert(point);
        return point.getId();
    }

    public void updatePoint(SynonymPointSaveReqVO req) {
        requirePoint(req.getId());
        pointMapper.updateById(prepare(req));
    }

    public void deletePoint(Long id) {
        requirePoint(id);
        pointMapper.deleteById(id);
    }

    @Transactional
    public Integer importPoints(SynonymBatchImportReqVO req) {
        int count = 0;
        for (SynonymPointSaveReqVO pointReq : req.getPoints()) {
            pointReq.setStatus(Boolean.FALSE.equals(req.getPublish()) ? 0 : 1);
            SynonymPointDO existing = pointMapper.selectByCode(pointReq.getCode());
            if (existing == null) {
                createPoint(pointReq);
            } else {
                pointReq.setId(existing.getId());
                updatePoint(pointReq);
            }
            count++;
        }
        return count;
    }

    public List<AppSynonymPointRespVO> getPracticeQueue(Long userId, String level, String mode) {
        Map<Long, UserSynonymProgressDO> progressMap = progressMap(userId);
        List<AppSynonymPointRespVO> list = new ArrayList<>();
        for (SynonymPointDO point : pointMapper.selectEnabled(normalizeLevel(level), normalizeMode(mode))) {
            list.add(toAppPoint(point, progressMap.get(point.getId())));
        }
        list.sort(Comparator
                .comparingInt((AppSynonymPointRespVO item) -> statusOrder(item.getStatus()))
                .thenComparing(AppSynonymPointRespVO::getSort, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(AppSynonymPointRespVO::getId));
        return list;
    }

    public AppSynonymStatsRespVO getStats(Long userId, String level) {
        List<AppSynonymPointRespVO> list = getPracticeQueue(userId, level, null);
        AppSynonymStatsRespVO stats = new AppSynonymStatsRespVO();
        stats.setTotal(list.size());
        stats.setSynonymTotal((int) list.stream().filter(item -> "synonym".equals(item.getMode())).count());
        stats.setAntonymTotal((int) list.stream().filter(item -> "antonym".equals(item.getMode())).count());
        stats.setDone((int) list.stream().filter(item -> "done".equals(item.getStatus())).count());
        stats.setLearning((int) list.stream().filter(item -> "learning".equals(item.getStatus())).count());
        stats.setNewCount((int) list.stream().filter(item -> "new".equals(item.getStatus())).count());
        return stats;
    }

    @Transactional
    public AppSynonymAnswerRespVO answer(Long userId, Long pointId, AppSynonymAnswerReqVO req) {
        SynonymPointDO point = requirePoint(pointId);
        if (!Integer.valueOf(1).equals(point.getStatus())) throw new IllegalArgumentException("同义替换知识点未发布");
        boolean correct = normalizeAnswer(req.getAnswer()).equals(normalizeAnswer(point.getRightText()));

        SynonymAttemptDO attempt = new SynonymAttemptDO();
        attempt.setUserId(userId);
        attempt.setPointId(pointId);
        attempt.setAnswerText(req.getAnswer());
        attempt.setCorrect(correct);
        attempt.setDurationSeconds(req.getDurationSeconds());
        attemptMapper.insert(attempt);

        UserSynonymProgressDO progress = progressMapper.selectByUserAndPoint(userId, pointId);
        if (progress == null) {
            progress = new UserSynonymProgressDO();
            progress.setUserId(userId);
            progress.setPointId(pointId);
            progress.setSeenCount(0);
            progress.setCorrectCount(0);
            progress.setWrongCount(0);
            progress.setMastered(false);
        }
        progress.setSeenCount(nvl(progress.getSeenCount()) + 1);
        progress.setCorrectCount(nvl(progress.getCorrectCount()) + (correct ? 1 : 0));
        progress.setWrongCount(nvl(progress.getWrongCount()) + (correct ? 0 : 1));
        progress.setMastered(correct);
        progress.setLastAnsweredAt(LocalDateTime.now());
        if (progress.getId() == null) progressMapper.insert(progress); else progressMapper.updateById(progress);

        AppSynonymAnswerRespVO resp = new AppSynonymAnswerRespVO();
        resp.setCorrect(correct);
        resp.setCorrectAnswer(point.getRightText());
        resp.setCorrectAnswerCn(point.getRightCn());
        resp.setExplanation(point.getLeftText() + " = " + point.getRightText());
        resp.setSeenCount(progress.getSeenCount());
        resp.setCorrectCount(progress.getCorrectCount());
        resp.setWrongCount(progress.getWrongCount());
        resp.setMastered(progress.getMastered());
        return resp;
    }

    public String getAudioUrl(String level, String text, String accent) {
        String normAccent = normalizeAccent(accent);
        String value = text == null ? "" : text.trim();
        if (value.isBlank()) throw new IllegalArgumentException("播放文本不能为空");

        VocabDO vocab = vocabMapper.selectPublishedByLevelAndWord(normalizeLevel(level), value);
        if (vocab != null) {
            return vocabService.getOrGenerateAudio(vocab.getId(), normAccent);
        }

        byte[] audio = pyClient.generateAudio(value, normAccent);
        if (audio == null || audio.length == 0) throw new IllegalArgumentException("语音生成失败");
        String fileName = "synonym_" + System.currentTimeMillis() + "_" + normAccent + ".mp3";
        String url = fileApi.createFile(audio, fileName, "english/synonym/audio", "audio/mpeg");
        if (url == null || url.isBlank()) throw new IllegalArgumentException("语音文件保存失败");
        return url;
    }

    private SynonymPointDO prepare(SynonymPointSaveReqVO req) {
        SynonymPointDO point = BeanUtils.toBean(req, SynonymPointDO.class);
        point.setMode(normalizeMode(req.getMode()));
        point.setLevelCode(normalizeLevel(req.getLevelCode()));
        if (point.getLeftCn() == null) point.setLeftCn("");
        if (point.getRightCn() == null) point.setRightCn("");
        if (point.getSort() == null) point.setSort(0);
        if (point.getStatus() == null) point.setStatus(0);
        if (point.getMode() == null || !Set.of("synonym", "antonym").contains(point.getMode())) throw new IllegalArgumentException("mode 只能是 synonym 或 antonym");
        return point;
    }

    private SynonymPointDO requirePoint(Long id) {
        SynonymPointDO point = id == null ? null : pointMapper.selectById(id);
        if (point == null) throw new IllegalArgumentException("同义替换知识点不存在");
        return point;
    }

    private Map<Long, UserSynonymProgressDO> progressMap(Long userId) {
        Map<Long, UserSynonymProgressDO> map = new HashMap<>();
        for (UserSynonymProgressDO progress : progressMapper.selectListByUser(userId)) {
            map.put(progress.getPointId(), progress);
        }
        return map;
    }

    private AppSynonymPointRespVO toAppPoint(SynonymPointDO point, UserSynonymProgressDO progress) {
        AppSynonymPointRespVO vo = BeanUtils.toBean(point, AppSynonymPointRespVO.class);
        vo.setSectionName(point.getSectionName());
        vo.setSeenCount(progress == null ? 0 : nvl(progress.getSeenCount()));
        vo.setCorrectCount(progress == null ? 0 : nvl(progress.getCorrectCount()));
        vo.setWrongCount(progress == null ? 0 : nvl(progress.getWrongCount()));
        vo.setMastered(progress != null && Boolean.TRUE.equals(progress.getMastered()));
        vo.setStatus(status(progress));
        return vo;
    }

    private String status(UserSynonymProgressDO progress) {
        if (progress == null) return "new";
        if (Boolean.TRUE.equals(progress.getMastered())) return "done";
        if (nvl(progress.getWrongCount()) > 0) return "learning";
        return "new";
    }

    private int statusOrder(String status) {
        if ("learning".equals(status)) return 0;
        if ("new".equals(status)) return 1;
        return 2;
    }

    private String normalizeMode(String mode) {
        return mode == null || mode.isBlank() || "all".equalsIgnoreCase(mode) ? null : mode.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeLevel(String level) {
        return level == null || level.isBlank() ? "ket" : level.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeAccent(String accent) {
        String norm = accent == null || accent.isBlank() ? "us" : accent.trim().toLowerCase(Locale.ROOT);
        if (!Set.of("uk", "us").contains(norm)) throw new IllegalArgumentException("accent 只能是 uk 或 us");
        return norm;
    }

    private String normalizeAnswer(String answer) {
        return answer == null ? "" : answer.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private int nvl(Integer value) {
        return value == null ? 0 : value;
    }
}
