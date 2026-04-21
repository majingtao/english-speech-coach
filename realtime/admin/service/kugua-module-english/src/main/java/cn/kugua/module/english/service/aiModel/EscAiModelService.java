package cn.kugua.module.english.service.aiModel;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.aiModel.vo.EscAiModelPageReqVO;
import cn.kugua.module.english.controller.admin.aiModel.vo.EscAiModelSaveReqVO;
import cn.kugua.module.english.dal.dataobject.aiModel.EscAiModelDO;
import jakarta.validation.Valid;

import java.util.List;

public interface EscAiModelService {

    Long createAiModel(@Valid EscAiModelSaveReqVO createReqVO);

    void updateAiModel(@Valid EscAiModelSaveReqVO updateReqVO);

    void deleteAiModel(Long id);

    void deleteAiModelListByIds(List<Long> ids);

    EscAiModelDO getAiModel(Long id);

    PageResult<EscAiModelDO> getAiModelPage(EscAiModelPageReqVO pageReqVO);

    /** 获取所有启用的模型（供 app-api 使用） */
    List<EscAiModelDO> getEnabledList();
}
