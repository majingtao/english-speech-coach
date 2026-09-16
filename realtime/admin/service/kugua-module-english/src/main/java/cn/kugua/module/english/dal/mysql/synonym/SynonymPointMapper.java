package cn.kugua.module.english.dal.mysql.synonym;

import cn.iocoder.yudao.framework.common.pojo.*;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.synonym.SynonymPointDO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SynonymPointMapper extends BaseMapperX<SynonymPointDO> {
    default PageResult<SynonymPointDO> selectPage(PageParam page, String levelCode, String mode, String source, Integer status) {
        return selectPage(page, new LambdaQueryWrapperX<SynonymPointDO>()
                .eqIfPresent(SynonymPointDO::getLevelCode, levelCode)
                .eqIfPresent(SynonymPointDO::getMode, mode)
                .likeIfPresent(SynonymPointDO::getSource, source)
                .eqIfPresent(SynonymPointDO::getStatus, status)
                .orderByAsc(SynonymPointDO::getSort)
                .orderByAsc(SynonymPointDO::getId));
    }

    default List<SynonymPointDO> selectEnabled(String levelCode, String mode) {
        return selectList(new LambdaQueryWrapperX<SynonymPointDO>()
                .eqIfPresent(SynonymPointDO::getLevelCode, levelCode)
                .eqIfPresent(SynonymPointDO::getMode, mode)
                .eq(SynonymPointDO::getStatus, 1)
                .orderByAsc(SynonymPointDO::getSort)
                .orderByAsc(SynonymPointDO::getId));
    }

    default SynonymPointDO selectByCode(String code) {
        return selectOne(SynonymPointDO::getCode, code);
    }
}
