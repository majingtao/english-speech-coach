package cn.kugua.module.english.service.vocab;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.vocab.UserVocabProgressDO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabDO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabThemeDO;
import cn.kugua.module.english.dal.mysql.vocab.UserVocabProgressMapper;
import cn.kugua.module.english.dal.mysql.vocab.VocabMapper;
import cn.kugua.module.english.dal.mysql.vocab.VocabThemeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserVocabProgressServiceImpl implements UserVocabProgressService {

    /** SRS 间隔表（艾宾浩斯曲线） */
    private static final int[] INTERVAL_DAYS = {1, 2, 4, 7, 15, 30};

    @Resource
    private UserVocabProgressMapper progressMapper;

    @Resource
    private VocabMapper vocabMapper;

    @Resource
    private VocabThemeMapper themeMapper;

    @Resource
    private UserVocabListService listService;

    @Override
    public List<UserVocabProgressDO> getTodayReview(Long userId, int limit) {
        return progressMapper.selectTodayReview(userId, limit);
    }

    @Override
    public long countEnrolledToday(Long userId) {
        return progressMapper.countEnrolledToday(userId);
    }

    @Override
    public UserVocabProgressDO submitReview(Long userId, Long vocabId, boolean remembered) {
        UserVocabProgressDO progress = progressMapper.selectByUserAndVocab(userId, vocabId);
        LocalDateTime now = LocalDateTime.now();
        boolean isNew = (progress == null);
        if (isNew) {
            progress = new UserVocabProgressDO();
            progress.setUserId(userId);
            progress.setVocabId(vocabId);
            progress.setRepetitions(0);
            progress.setIntervalDays(0);
            progress.setEase(250);
            progress.setCorrectCount(0);
            progress.setWrongCount(0);
            progress.setStatus(1);
        }

        if (remembered) {
            int reps = progress.getRepetitions() == null ? 0 : progress.getRepetitions();
            reps++;
            int idx = Math.min(reps - 1, INTERVAL_DAYS.length - 1);
            int interval = INTERVAL_DAYS[idx];
            progress.setRepetitions(reps);
            progress.setIntervalDays(interval);
            progress.setStatus(reps >= INTERVAL_DAYS.length ? 2 : 1);
            progress.setCorrectCount((progress.getCorrectCount() == null ? 0 : progress.getCorrectCount()) + 1);
            progress.setNextReviewAt(now.plusDays(interval));
        } else {
            progress.setRepetitions(0);
            progress.setIntervalDays(0);
            progress.setStatus(1);
            progress.setWrongCount((progress.getWrongCount() == null ? 0 : progress.getWrongCount()) + 1);
            progress.setNextReviewAt(now);
        }
        progress.setLastReviewAt(now);

        if (isNew) {
            progressMapper.insert(progress);
        } else {
            progressMapper.updateById(progress);
        }
        return progress;
    }

    @Override
    public UserVocabProgressDO getProgress(Long userId, Long vocabId) {
        return progressMapper.selectByUserAndVocab(userId, vocabId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int enrollNewWords(Long userId, String levelCode, String themeCode, int limit) {
        if (limit <= 0 || levelCode == null || levelCode.isBlank()) {
            return 0;
        }

        // 0a) 解析主题（如果给了 themeCode）
        Long themeId = null;
        if (themeCode != null && !themeCode.isBlank()) {
            VocabThemeDO theme = themeMapper.selectByCode(themeCode);
            if (theme == null) {
                return 0; // 主题不存在：不做兜底乱抽
            }
            themeId = theme.getId();
        }

        // 0b) 每日上限：今天已经 enroll 多少，剩余配额是多少
        long enrolledToday = progressMapper.countEnrolledToday(userId);
        int remainingToday = (int) (DAILY_NEW_WORD_CAP - enrolledToday);
        if (remainingToday <= 0) {
            return 0;
        }
        if (limit > remainingToday) {
            limit = remainingToday;
        }

        // 1) 当前用户已入队的 vocab_id 集合（用于排除）
        Set<Long> enrolledIds = new HashSet<>();
        for (UserVocabProgressDO p : progressMapper.selectListByUser(userId)) {
            enrolledIds.add(p.getVocabId());
        }

        // 2) 难度配比：80% A2 必备 + 20% B1 延展，随机抽，d=1 不够则用 d=2 补，再不够回头补 d=1
        int targetEasy = (int) Math.round(limit * 0.8);
        int targetHard = limit - targetEasy;

        List<VocabDO> easy = pickRandomVocabs(levelCode, 1, themeId, enrolledIds, targetEasy);
        int easyShort = targetEasy - easy.size();
        List<VocabDO> hard = pickRandomVocabs(levelCode, 2, themeId, enrolledIds, targetHard + easyShort);

        if (easy.size() + hard.size() < limit) {
            int remain = limit - easy.size() - hard.size();
            Set<Long> picked = new HashSet<>(enrolledIds);
            for (VocabDO v : easy) picked.add(v.getId());
            for (VocabDO v : hard) picked.add(v.getId());
            List<VocabDO> backfill = pickRandomVocabs(levelCode, 1, themeId, picked, remain);
            easy.addAll(backfill);
        }

        List<VocabDO> all = new ArrayList<>(easy.size() + hard.size());
        all.addAll(easy);
        all.addAll(hard);
        if (all.isEmpty()) {
            return 0;
        }
        Collections.shuffle(all);

        // 3) 批量 insert 进度行（status=0 新词，next_review_at=NOW()）
        LocalDateTime now = LocalDateTime.now();
        int inserted = 0;
        for (VocabDO v : all) {
            UserVocabProgressDO p = new UserVocabProgressDO();
            p.setUserId(userId);
            p.setVocabId(v.getId());
            p.setStatus(0);
            p.setRepetitions(0);
            p.setIntervalDays(0);
            p.setEase(250);
            p.setCorrectCount(0);
            p.setWrongCount(0);
            p.setLastReviewAt(null);
            p.setNextReviewAt(now);
            progressMapper.insert(p);
            inserted++;
        }
        return inserted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int enrollFromWordbook(Long userId, int limit) {
        if (limit <= 0) return 0;

        // 每日上限：今天已 enroll 多少，剩余配额是多少
        long enrolledToday = progressMapper.countEnrolledToday(userId);
        int remainingToday = (int) (DAILY_NEW_WORD_CAP - enrolledToday);
        if (remainingToday <= 0) return 0;
        if (limit > remainingToday) limit = remainingToday;

        // 生词本待学池（FIFO 升序）
        List<Long> wordbookIds = listService.getWordbookVocabIds(userId);
        if (wordbookIds.isEmpty()) return 0;

        // 排除已进 SRS 的词
        Set<Long> enrolled = new HashSet<>();
        for (UserVocabProgressDO p : progressMapper.selectListByUser(userId)) {
            enrolled.add(p.getVocabId());
        }
        List<Long> candidateIds = new ArrayList<>();
        for (Long id : wordbookIds) {
            if (!enrolled.contains(id)) candidateIds.add(id);
        }
        if (candidateIds.isEmpty()) return 0;

        // 只入队已发布词，保持 FIFO 顺序
        Map<Long, VocabDO> vocabMap = new HashMap<>();
        for (VocabDO v : vocabMapper.selectBatchIds(candidateIds)) {
            vocabMap.put(v.getId(), v);
        }

        LocalDateTime now = LocalDateTime.now();
        int inserted = 0;
        for (Long id : candidateIds) {
            if (inserted >= limit) break;
            VocabDO v = vocabMap.get(id);
            if (v == null || v.getStatus() == null || v.getStatus() != 1) continue;
            UserVocabProgressDO p = new UserVocabProgressDO();
            p.setUserId(userId);
            p.setVocabId(id);
            p.setStatus(0);
            p.setRepetitions(0);
            p.setIntervalDays(0);
            p.setEase(250);
            p.setCorrectCount(0);
            p.setWrongCount(0);
            p.setLastReviewAt(null);
            p.setNextReviewAt(now);
            progressMapper.insert(p);
            inserted++;
        }
        return inserted;
    }

    /** 已发布词库中按级别 + 难度（+ 可选主题）随机抽 limit 个，排除 exclude 集合。 */
    private List<VocabDO> pickRandomVocabs(String levelCode, int difficulty, Long themeId,
                                           Set<Long> exclude, int limit) {
        if (limit <= 0) return Collections.emptyList();
        LambdaQueryWrapperX<VocabDO> q = new LambdaQueryWrapperX<>();
        q.eq(VocabDO::getLevelCode, levelCode);
        q.eq(VocabDO::getStatus, 1);
        q.eq(VocabDO::getDifficulty, difficulty);
        if (themeId != null) {
            // themeId 是 Long，无注入风险
            q.inSql(VocabDO::getId,
                    "SELECT vocab_id FROM esc_vocab_theme_rel WHERE theme_id = " + themeId + " AND deleted = b'0'");
        }
        if (exclude != null && !exclude.isEmpty()) {
            q.notIn(VocabDO::getId, exclude);
        }
        // ORDER BY RAND() LIMIT N — KET 词库 ≤ 2000 条，性能 OK
        q.last("ORDER BY RAND() LIMIT " + limit);
        return vocabMapper.selectList(q);
    }

}
