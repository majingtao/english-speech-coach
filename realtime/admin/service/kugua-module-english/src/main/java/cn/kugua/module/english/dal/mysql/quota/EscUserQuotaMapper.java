package cn.kugua.module.english.dal.mysql.quota;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.controller.admin.quota.vo.EscUserQuotaPageReqVO;
import cn.kugua.module.english.dal.dataobject.quota.EscUserQuotaDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EscUserQuotaMapper extends BaseMapperX<EscUserQuotaDO> {

    default EscUserQuotaDO selectByUserId(Long userId) {
        return selectOne(EscUserQuotaDO::getUserId, userId);
    }

    default PageResult<EscUserQuotaDO> selectPage(EscUserQuotaPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EscUserQuotaDO>()
                .eqIfPresent(EscUserQuotaDO::getUserId, reqVO.getUserId())
                .eqIfPresent(EscUserQuotaDO::getEnabled, reqVO.getEnabled())
                .orderByDesc(EscUserQuotaDO::getId));
    }

}
