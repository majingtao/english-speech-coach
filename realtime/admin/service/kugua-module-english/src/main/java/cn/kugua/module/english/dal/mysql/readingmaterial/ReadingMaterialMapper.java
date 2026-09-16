package cn.kugua.module.english.dal.mysql.readingmaterial;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.readingmaterial.ReadingMaterialDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReadingMaterialMapper extends BaseMapperX<ReadingMaterialDO> {

    default PageResult<ReadingMaterialDO> selectPage(PageParam page, String text, String materialType,
                                                     String partOfSpeech, String levelCode, String tag,
                                                     String priority, Integer status) {
        LambdaQueryWrapperX<ReadingMaterialDO> wrapper = new LambdaQueryWrapperX<ReadingMaterialDO>()
                .likeIfPresent(ReadingMaterialDO::getTextEn, text)
                .eqIfPresent(ReadingMaterialDO::getMaterialType, materialType)
                .eqIfPresent(ReadingMaterialDO::getPartOfSpeech, partOfSpeech)
                .eqIfPresent(ReadingMaterialDO::getLevelCode, levelCode)
                .likeIfPresent(ReadingMaterialDO::getTagsJson, tag)
                .eqIfPresent(ReadingMaterialDO::getStatus, status);
        applyPriority(wrapper, priority);
        return selectPage(page, wrapper
                .orderByAsc(ReadingMaterialDO::getSort)
                .orderByDesc(ReadingMaterialDO::getId));
    }

    default List<ReadingMaterialDO> selectPublished(String levelCode, String materialType, String tag, String priority) {
        LambdaQueryWrapperX<ReadingMaterialDO> wrapper = new LambdaQueryWrapperX<ReadingMaterialDO>()
                .eqIfPresent(ReadingMaterialDO::getLevelCode, levelCode)
                .eqIfPresent(ReadingMaterialDO::getMaterialType, materialType)
                .likeIfPresent(ReadingMaterialDO::getTagsJson, tag)
                .eq(ReadingMaterialDO::getStatus, 1);
        applyPriority(wrapper, priority);
        return selectList(wrapper
                .orderByAsc(ReadingMaterialDO::getSort)
                .orderByAsc(ReadingMaterialDO::getId));
    }

    default PageResult<ReadingMaterialDO> selectPublishedPage(PageParam page, String levelCode, String materialType,
                                                              String tag, String priority) {
        LambdaQueryWrapperX<ReadingMaterialDO> wrapper = new LambdaQueryWrapperX<ReadingMaterialDO>()
                .eqIfPresent(ReadingMaterialDO::getLevelCode, levelCode)
                .eqIfPresent(ReadingMaterialDO::getMaterialType, materialType)
                .likeIfPresent(ReadingMaterialDO::getTagsJson, tag)
                .eq(ReadingMaterialDO::getStatus, 1);
        applyPriority(wrapper, priority);
        return selectPage(page, wrapper
                .orderByAsc(ReadingMaterialDO::getSort)
                .orderByAsc(ReadingMaterialDO::getId));
    }

    default void applyPriority(LambdaQueryWrapperX<ReadingMaterialDO> wrapper, String priority) {
        if ("mustKnow".equals(priority)) {
            wrapper.eq(ReadingMaterialDO::getMustKnow, 1);
        } else if ("highFrequency".equals(priority)) {
            wrapper.eq(ReadingMaterialDO::getHighFrequency, 1);
        } else if ("mustSpell".equals(priority)) {
            wrapper.eq(ReadingMaterialDO::getMustSpell, 1);
        }
    }
}
