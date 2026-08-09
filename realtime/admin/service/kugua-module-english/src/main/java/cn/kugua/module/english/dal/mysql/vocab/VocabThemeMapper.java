package cn.kugua.module.english.dal.mysql.vocab;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.controller.admin.vocab.vo.VocabThemePageReqVO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabThemeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VocabThemeMapper extends BaseMapperX<VocabThemeDO> {

    default PageResult<VocabThemeDO> selectPage(VocabThemePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<VocabThemeDO>()
                .likeIfPresent(VocabThemeDO::getCode, reqVO.getCode())
                .likeIfPresent(VocabThemeDO::getNameCn, reqVO.getNameCn())
                .eqIfPresent(VocabThemeDO::getLevelCode, reqVO.getLevelCode())
                .eqIfPresent(VocabThemeDO::getStatus, reqVO.getStatus())
                .orderByAsc(VocabThemeDO::getSort));
    }

    default List<VocabThemeDO> selectListByLevel(String levelCode) {
        return selectList(new LambdaQueryWrapperX<VocabThemeDO>()
                .eqIfPresent(VocabThemeDO::getLevelCode, levelCode)
                .eq(VocabThemeDO::getStatus, 1)
                .orderByAsc(VocabThemeDO::getSort));
    }

    default VocabThemeDO selectByCode(String code) {
        return selectOne(VocabThemeDO::getCode, code);
    }

}
