package cn.kugua.module.english.service.vocab;

import cn.kugua.module.english.dal.dataobject.vocab.UserVocabProgressDO;

import java.util.List;

public interface UserVocabProgressService {

    /** 每日新词上限：单用户单日通过 enrollNewWords 加入 SRS 队列的最大词数 */
    int DAILY_NEW_WORD_CAP = 50;

    /** 今日 SRS 复习队列（next_review_at <= now） */
    List<UserVocabProgressDO> getTodayReview(Long userId, int limit);

    /** 当日已 enroll 的新词数（含已答过的） */
    long countEnrolledToday(Long userId);

    /** 提交一次复习结果（recalls SRS 算法） */
    UserVocabProgressDO submitReview(Long userId, Long vocabId, boolean remembered);

    UserVocabProgressDO getProgress(Long userId, Long vocabId);

    /**
     * 从已发布词库（status=1）挑 limit 个用户尚未学过的词加入 SRS 复习队列。
     * 每条新进度初始化：status=0(新词)、repetitions=0、interval_days=0、next_review_at=NOW()。
     * 受 {@link #DAILY_NEW_WORD_CAP} 当日上限约束：超过当日已 enroll 数则按剩余配额返还。
     *
     * @param themeCode 可选；非空时只从该主题下挑词。主题 code 不存在时直接返回 0。
     * @return 实际插入数（题库不够或剩余配额不足时小于 limit）
     */
    int enrollNewWords(Long userId, String levelCode, String themeCode, int limit);

    /**
     * 从用户的默认生词本（待学池）里挑尚未进 SRS 的词加入复习队列，按加入时间 FIFO。
     * 只入队已发布（status=1）的词；受 {@link #DAILY_NEW_WORD_CAP} 当日上限约束。
     * 池空或全部已入队时返回 0（不做随机兜底）。
     *
     * @return 实际插入数
     */
    int enrollFromWordbook(Long userId, int limit);

}
