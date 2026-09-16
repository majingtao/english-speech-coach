package cn.kugua.module.english.dal.mysql.personalpractice;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.personalpractice.PersonalPracticeProgressDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PersonalPracticeProgressMapper extends BaseMapperX<PersonalPracticeProgressDO> {

    default PersonalPracticeProgressDO selectByUserAndPractice(Long userId, Long practiceId) {
        return selectOne(new LambdaQueryWrapperX<PersonalPracticeProgressDO>()
                .eq(PersonalPracticeProgressDO::getUserId, userId)
                .eq(PersonalPracticeProgressDO::getPracticeId, practiceId));
    }
}
