package cn.kugua.module.english.service.schoolClass;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.schoolClass.vo.*;
import cn.kugua.module.english.dal.dataobject.schoolClass.SchoolClassDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 班级 Service 接口
 *
 * @author 苦瓜
 */
public interface SchoolClassService {

    /**
     * 创建班级
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSchoolClass(@Valid SchoolClassSaveReqVO createReqVO);

    /**
     * 更新班级
     *
     * @param updateReqVO 更新信息
     */
    void updateSchoolClass(@Valid SchoolClassSaveReqVO updateReqVO);

    /**
     * 删除班级
     *
     * @param id 编号
     */
    void deleteSchoolClass(Long id);

    /**
    * 批量删除班级
    *
    * @param ids 编号
    */
    void deleteSchoolClassListByIds(List<Long> ids);

    /**
     * 获得班级
     *
     * @param id 编号
     * @return 班级
     */
    SchoolClassDO getSchoolClass(Long id);

    /**
     * 获得班级分页
     *
     * @param pageReqVO 分页查询
     * @return 班级分页
     */
    PageResult<SchoolClassDO> getSchoolClassPage(SchoolClassPageReqVO pageReqVO);

}