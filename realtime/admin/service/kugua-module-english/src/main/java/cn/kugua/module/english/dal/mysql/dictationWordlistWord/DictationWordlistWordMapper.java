package cn.kugua.module.english.dal.mysql.dictationWordlistWord;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.dictationWordlistWord.DictationWordlistWordDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DictationWordlistWordMapper extends BaseMapperX<DictationWordlistWordDO> {

    default List<DictationWordlistWordDO> selectByWordlistId(Long wordlistId) {
        return selectList(new LambdaQueryWrapper<DictationWordlistWordDO>()
                .eq(DictationWordlistWordDO::getWordlistId, wordlistId)
                .orderByAsc(DictationWordlistWordDO::getSeq));
    }

    default DictationWordlistWordDO selectByWordlistIdAndWordId(Long wordlistId, Long wordId) {
        return selectOne(DictationWordlistWordDO::getWordlistId, wordlistId,
                DictationWordlistWordDO::getWordId, wordId);
    }

    default void deleteByWordlistIdAndWordId(Long wordlistId, Long wordId) {
        delete(new LambdaQueryWrapper<DictationWordlistWordDO>()
                .eq(DictationWordlistWordDO::getWordlistId, wordlistId)
                .eq(DictationWordlistWordDO::getWordId, wordId));
    }

}
