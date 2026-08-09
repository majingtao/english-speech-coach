package cn.kugua.module.english.dal.mysql.vocab;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.vocab.VocabThemeRelDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VocabThemeRelMapper extends BaseMapperX<VocabThemeRelDO> {

    /** 物理删除某词条的所有主题关联（同样规避 deleted 唯一键冲突） */
    @Delete("DELETE FROM esc_vocab_theme_rel WHERE vocab_id = #{vocabId}")
    void physicalDeleteByVocabId(@Param("vocabId") Long vocabId);

    default List<VocabThemeRelDO> selectListByVocabId(Long vocabId) {
        return selectList(VocabThemeRelDO::getVocabId, vocabId);
    }

    default List<VocabThemeRelDO> selectListByThemeId(Long themeId) {
        return selectList(VocabThemeRelDO::getThemeId, themeId);
    }

    default void deleteByVocabId(Long vocabId) {
        delete(new LambdaQueryWrapperX<VocabThemeRelDO>()
                .eq(VocabThemeRelDO::getVocabId, vocabId));
    }

}
