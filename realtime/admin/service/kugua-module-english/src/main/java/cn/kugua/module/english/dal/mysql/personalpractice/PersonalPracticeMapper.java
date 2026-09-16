package cn.kugua.module.english.dal.mysql.personalpractice;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.dal.dataobject.personalpractice.PersonalPracticeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PersonalPracticeMapper extends BaseMapperX<PersonalPracticeDO> {

    default PageResult<PersonalPracticeDO> selectPage(PageParam page, String practiceType, String title, Integer status) {
        return selectPage(page, new LambdaQueryWrapperX<PersonalPracticeDO>()
                .eqIfPresent(PersonalPracticeDO::getPracticeType, practiceType)
                .likeIfPresent(PersonalPracticeDO::getTitle, title)
                .eqIfPresent(PersonalPracticeDO::getStatus, status)
                .orderByAsc(PersonalPracticeDO::getSort)
                .orderByDesc(PersonalPracticeDO::getId));
    }

    default List<PersonalPracticeDO> selectPublished(String practiceType) {
        return selectList(new LambdaQueryWrapperX<PersonalPracticeDO>()
                .eq(PersonalPracticeDO::getPracticeType, practiceType)
                .eq(PersonalPracticeDO::getStatus, 1)
                .orderByAsc(PersonalPracticeDO::getSort)
                .orderByAsc(PersonalPracticeDO::getId));
    }
}
