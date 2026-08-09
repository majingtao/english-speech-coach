package cn.kugua.module.english.dal.mysql.expression;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExpressionItemMapper extends BaseMapperX<ExpressionItemDO> {

    default PageResult<ExpressionItemDO> selectPage(PageParam page, Long themeId, String prompt, Integer status) {
        return selectPage(page, new LambdaQueryWrapperX<ExpressionItemDO>()
                .eqIfPresent(ExpressionItemDO::getThemeId, themeId)
                .likeIfPresent(ExpressionItemDO::getPromptEn, prompt)
                .eqIfPresent(ExpressionItemDO::getStatus, status)
                .orderByAsc(ExpressionItemDO::getSort)
                .orderByDesc(ExpressionItemDO::getId));
    }

    default List<ExpressionItemDO> selectPublishedByTheme(Long themeId) {
        return selectList(new LambdaQueryWrapperX<ExpressionItemDO>()
                .eq(ExpressionItemDO::getThemeId, themeId)
                .eq(ExpressionItemDO::getStatus, 1)
                .orderByAsc(ExpressionItemDO::getSort)
                .orderByAsc(ExpressionItemDO::getId));
    }
}
