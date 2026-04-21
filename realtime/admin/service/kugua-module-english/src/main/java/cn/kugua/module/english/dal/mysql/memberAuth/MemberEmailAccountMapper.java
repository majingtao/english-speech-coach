package cn.kugua.module.english.dal.mysql.memberAuth;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.memberAuth.MemberEmailAccountDO;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberEmailAccountMapper extends BaseMapperX<MemberEmailAccountDO> {

    default MemberEmailAccountDO selectByEmail(String email) {
        return selectOne(Wrappers.<MemberEmailAccountDO>lambdaQuery()
                .eq(MemberEmailAccountDO::getEmail, email));
    }

    default MemberEmailAccountDO selectByUsername(String username) {
        return selectOne(Wrappers.<MemberEmailAccountDO>lambdaQuery()
                .eq(MemberEmailAccountDO::getUsername, username));
    }

    default MemberEmailAccountDO selectByUserId(Long userId) {
        return selectOne(Wrappers.<MemberEmailAccountDO>lambdaQuery()
                .eq(MemberEmailAccountDO::getUserId, userId));
    }

}
