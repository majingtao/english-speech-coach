package cn.kugua.module.english.service.student;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.student.vo.*;
import cn.kugua.module.english.dal.dataobject.student.StudentDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 学生 Service 接口
 *
 * @author 苦瓜
 */
public interface StudentService {

    /**
     * 创建学生
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStudent(@Valid StudentSaveReqVO createReqVO);

    /**
     * 更新学生
     *
     * @param updateReqVO 更新信息
     */
    void updateStudent(@Valid StudentSaveReqVO updateReqVO);

    /**
     * 删除学生
     *
     * @param id 编号
     */
    void deleteStudent(Long id);

    /**
    * 批量删除学生
    *
    * @param ids 编号
    */
    void deleteStudentListByIds(List<Long> ids);

    /**
     * 获得学生
     *
     * @param id 编号
     * @return 学生
     */
    StudentDO getStudent(Long id);

    /**
     * 获得学生分页
     *
     * @param pageReqVO 分页查询
     * @return 学生分页
     */
    PageResult<StudentDO> getStudentPage(StudentPageReqVO pageReqVO);

}