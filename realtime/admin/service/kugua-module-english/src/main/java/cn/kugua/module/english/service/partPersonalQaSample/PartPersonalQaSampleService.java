package cn.kugua.module.english.service.partPersonalQaSample;

import java.util.*;
import jakarta.validation.*;
import cn.kugua.module.english.controller.admin.partPersonalQaSample.vo.*;
import cn.kugua.module.english.dal.dataobject.partPersonalQaSample.PartPersonalQaSampleDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 个人问答 - 示例答案 Service 接口
 *
 * @author 苦瓜
 */
public interface PartPersonalQaSampleService {

    Long createPartPersonalQaSample(@Valid PartPersonalQaSampleSaveReqVO createReqVO);

    void updatePartPersonalQaSample(@Valid PartPersonalQaSampleSaveReqVO updateReqVO);

    void deletePartPersonalQaSample(Long id);

    void deletePartPersonalQaSampleListByIds(List<Long> ids);

    PartPersonalQaSampleDO getPartPersonalQaSample(Long id);

    PageResult<PartPersonalQaSampleDO> getPartPersonalQaSamplePage(PartPersonalQaSamplePageReqVO pageReqVO);

}
