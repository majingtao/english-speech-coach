package cn.kugua.module.english.dal.mysql.vocab;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.vocab.UserVocabProgressDO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserVocabProgressMapper extends BaseMapperX<UserVocabProgressDO> {

    /** 词条被物理删除时连带清掉所有用户对该词的 SRS 进度，避免 orphan */
    @Delete("DELETE FROM esc_user_vocab_progress WHERE vocab_id = #{vocabId}")
    void physicalDeleteByVocabId(@Param("vocabId") Long vocabId);

    default UserVocabProgressDO selectByUserAndVocab(Long userId, Long vocabId) {
        return selectOne(new LambdaQueryWrapperX<UserVocabProgressDO>()
                .eq(UserVocabProgressDO::getUserId, userId)
                .eq(UserVocabProgressDO::getVocabId, vocabId));
    }

    /**
     * SRS 今日复习队列：next_review_at <= now，按 next_review_at asc
     * 若用户该词无进度记录，按词表 sort 兜底（此处只返回已开始学习的词）
     */
    default List<UserVocabProgressDO> selectTodayReview(Long userId, int limit) {
        return selectList(Wrappers.<UserVocabProgressDO>lambdaQuery()
                .eq(UserVocabProgressDO::getUserId, userId)
                .le(UserVocabProgressDO::getNextReviewAt, LocalDateTime.now())
                .orderByAsc(UserVocabProgressDO::getNextReviewAt)
                .last("LIMIT " + Math.max(1, Math.min(limit, 100))));
    }

    default List<UserVocabProgressDO> selectListByUser(Long userId) {
        return selectList(UserVocabProgressDO::getUserId, userId);
    }

    /** 当天（自然日）已加入 SRS 队列的新词数量，用于"每日新词上限"限流 */
    default long countEnrolledToday(Long userId) {
        return selectCount(Wrappers.<UserVocabProgressDO>lambdaQuery()
                .eq(UserVocabProgressDO::getUserId, userId)
                .ge(UserVocabProgressDO::getCreateTime, LocalDate.now().atStartOfDay()));
    }

}
