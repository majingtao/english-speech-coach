package cn.kugua.module.english.dal.mysql.readingmaterial;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.readingmaterial.UserReadingMaterialProgressDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserReadingMaterialProgressMapper extends BaseMapperX<UserReadingMaterialProgressDO> {

    default UserReadingMaterialProgressDO selectByUserAndMaterial(Long userId, Long materialId) {
        return selectOne(new LambdaQueryWrapperX<UserReadingMaterialProgressDO>()
                .eq(UserReadingMaterialProgressDO::getUserId, userId)
                .eq(UserReadingMaterialProgressDO::getMaterialId, materialId));
    }
}
