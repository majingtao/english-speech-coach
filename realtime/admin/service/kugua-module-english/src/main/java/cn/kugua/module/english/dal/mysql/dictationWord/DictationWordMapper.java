package cn.kugua.module.english.dal.mysql.dictationWord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationWordPageReqVO;
import cn.kugua.module.english.dal.dataobject.dictationWord.DictationWordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DictationWordMapper extends BaseMapperX<DictationWordDO> {

    default PageResult<DictationWordDO> selectPage(DictationWordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DictationWordDO>()
                .likeIfPresent(DictationWordDO::getEn, reqVO.getEn())
                .likeIfPresent(DictationWordDO::getCn, reqVO.getCn())
                .eqIfPresent(DictationWordDO::getPos, reqVO.getPos())
                .eqIfPresent(DictationWordDO::getLlmStatus, reqVO.getLlmStatus())
                .eqIfPresent(DictationWordDO::getDifficulty, reqVO.getDifficulty())
                .orderByDesc(DictationWordDO::getId));
    }

    default DictationWordDO selectByEn(String en) {
        return selectOne(DictationWordDO::getEn, en);
    }

}
