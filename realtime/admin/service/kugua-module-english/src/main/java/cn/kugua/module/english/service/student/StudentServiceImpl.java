package cn.kugua.module.english.service.student;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.kugua.module.english.controller.admin.student.vo.*;
import cn.kugua.module.english.dal.dataobject.student.StudentDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.kugua.module.english.dal.mysql.student.StudentMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.kugua.module.english.enums.ErrorCodeConstants.*;

/**
 * 学生 Service 实现类
 *
 * @author 苦瓜
 */
@Service
@Validated
public class StudentServiceImpl implements StudentService {

    @Resource
    private StudentMapper studentMapper;

    @Override
    public Long createStudent(StudentSaveReqVO createReqVO) {
        // 插入
        StudentDO student = BeanUtils.toBean(createReqVO, StudentDO.class);
        studentMapper.insert(student);

        // 返回
        return student.getId();
    }

    @Override
    public void updateStudent(StudentSaveReqVO updateReqVO) {
        // 校验存在
        validateStudentExists(updateReqVO.getId());
        // 更新
        StudentDO updateObj = BeanUtils.toBean(updateReqVO, StudentDO.class);
        studentMapper.updateById(updateObj);
    }

    @Override
    public void deleteStudent(Long id) {
        // 校验存在
        validateStudentExists(id);
        // 删除
        studentMapper.deleteById(id);
    }

    @Override
        public void deleteStudentListByIds(List<Long> ids) {
        // 删除
        studentMapper.deleteByIds(ids);
        }


    private void validateStudentExists(Long id) {
        if (studentMapper.selectById(id) == null) {
            throw exception(STUDENT_NOT_EXISTS);
        }
    }

    @Override
    public StudentDO getStudent(Long id) {
        return studentMapper.selectById(id);
    }

    @Override
    public PageResult<StudentDO> getStudentPage(StudentPageReqVO pageReqVO) {
        return studentMapper.selectPage(pageReqVO);
    }

}