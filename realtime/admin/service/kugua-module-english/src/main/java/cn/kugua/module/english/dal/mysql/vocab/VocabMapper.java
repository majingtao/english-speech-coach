package cn.kugua.module.english.dal.mysql.vocab;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabPageReqVO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VocabMapper extends BaseMapperX<VocabDO> {

    /**
     * 物理删除，绕过 @TableLogic 的软删。
     * 词库是 master data，软删会让 (tenant_id, level_code, word, deleted) 唯一键在重复操作时冲突，
     * 且学员 progress 行会指向 orphan，所以删 vocab 走硬删。
     */
    @Delete("DELETE FROM esc_vocab WHERE id = #{id}")
    void physicalDeleteById(@Param("id") Long id);

    default PageResult<VocabDO> selectPage(VocabPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<VocabDO>()
                .likeIfPresent(VocabDO::getWord, reqVO.getWord())
                .eqIfPresent(VocabDO::getLevelCode, reqVO.getLevelCode())
                .eqIfPresent(VocabDO::getDifficulty, reqVO.getDifficulty())
                .eqIfPresent(VocabDO::getMasteryLevel, reqVO.getMasteryLevel())
                .eqIfPresent(VocabDO::getStatus, reqVO.getStatus())
                .orderByAsc(VocabDO::getSort)
                .orderByDesc(VocabDO::getId));
    }

    default VocabDO selectByLevelAndWord(String levelCode, String word) {
        return selectOne(new LambdaQueryWrapperX<VocabDO>()
                .eq(VocabDO::getLevelCode, levelCode)
                .eq(VocabDO::getWord, word));
    }

}
