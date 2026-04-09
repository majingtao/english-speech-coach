package cn.kugua.module.english.service.schoolClass;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.kugua.module.english.controller.admin.schoolClass.vo.*;
import cn.kugua.module.english.dal.dataobject.schoolClass.SchoolClassDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.schoolClass.SchoolClassMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 班级 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class SchoolClassServiceImpl implements SchoolClassService {

    @Resource
    private SchoolClassMapper schoolClassMapper;

    @Override
    public Long createSchoolClass(SchoolClassSaveReqVO createReqVO) {
        // 插入
        SchoolClassDO schoolClass = BeanUtils.toBean(createReqVO, SchoolClassDO.class);
        schoolClassMapper.insert(schoolClass);

        // 返回
        return schoolClass.getId();
    }

    @Override
    public void updateSchoolClass(SchoolClassSaveReqVO updateReqVO) {
        // 校验存在
        validateSchoolClassExists(updateReqVO.getId());
        // 更新
        SchoolClassDO updateObj = BeanUtils.toBean(updateReqVO, SchoolClassDO.class);
        schoolClassMapper.updateById(updateObj);
    }

    @Override
    public void deleteSchoolClass(Long id) {
        // 校验存在
        validateSchoolClassExists(id);
        // 删除
        schoolClassMapper.deleteById(id);
    }

    @Override
        public void deleteSchoolClassListByIds(List<Long> ids) {
        // 删除
        schoolClassMapper.deleteByIds(ids);
        }


    private void validateSchoolClassExists(Long id) {
        if (schoolClassMapper.selectById(id) == null) {
            throw exception(SCHOOL_CLASS_NOT_EXISTS);
        }
    }

    @Override
    public SchoolClassDO getSchoolClass(Long id) {
        return schoolClassMapper.selectById(id);
    }

    @Override
    public PageResult<SchoolClassDO> getSchoolClassPage(SchoolClassPageReqVO pageReqVO) {
        return schoolClassMapper.selectPage(pageReqVO);
    }

}