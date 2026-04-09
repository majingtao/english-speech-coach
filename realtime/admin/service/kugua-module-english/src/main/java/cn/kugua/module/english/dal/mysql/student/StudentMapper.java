package cn.kugua.module.english.dal.mysql.student;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.kugua.module.english.dal.dataobject.student.StudentDO;
import org.apache.ibatis.annotations.Mapper;
import cn.kugua.module.english.controller.admin.student.vo.*;

/**
 * 学生 Mapper
 *
 * @author 苦瓜
 */
@Mapper
public interface StudentMapper extends BaseMapperX<StudentDO> {

    default PageResult<StudentDO> selectPage(StudentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StudentDO>()
                .likeIfPresent(StudentDO::getUsername, reqVO.getUsername())
                .eqIfPresent(StudentDO::getPassword, reqVO.getPassword())
                .likeIfPresent(StudentDO::getNickname, reqVO.getNickname())
                .eqIfPresent(StudentDO::getAvatar, reqVO.getAvatar())
                .eqIfPresent(StudentDO::getGender, reqVO.getGender())
                .eqIfPresent(StudentDO::getBirthday, reqVO.getBirthday())
                .eqIfPresent(StudentDO::getClassId, reqVO.getClassId())
                .eqIfPresent(StudentDO::getLevelCode, reqVO.getLevelCode())
                .eqIfPresent(StudentDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(StudentDO::getLastLoginTime, reqVO.getLastLoginTime())
                .eqIfPresent(StudentDO::getLastLoginIp, reqVO.getLastLoginIp())
                .betweenIfPresent(StudentDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(StudentDO::getId));
    }

}