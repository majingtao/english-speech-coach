package cn.kugua.module.english.service.aiModel;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.aiModel.vo.EscAiModelPageReqVO;
import cn.kugua.module.english.controller.admin.aiModel.vo.EscAiModelSaveReqVO;
import cn.kugua.module.english.dal.dataobject.aiModel.EscAiModelDO;
import cn.kugua.module.english.dal.mysql.aiModel.EscAiModelMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.AI_MODEL_NOT_EXISTS;

@Service
@Validated
public class EscAiModelServiceImpl implements EscAiModelService {

    @Resource
    private EscAiModelMapper aiModelMapper;

    @Override
    public Long createAiModel(EscAiModelSaveReqVO createReqVO) {
        EscAiModelDO aiModel = BeanUtils.toBean(createReqVO, EscAiModelDO.class);
        aiModelMapper.insert(aiModel);
        return aiModel.getId();
    }

    @Override
    public void updateAiModel(EscAiModelSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        EscAiModelDO updateObj = BeanUtils.toBean(updateReqVO, EscAiModelDO.class);
        aiModelMapper.updateById(updateObj);
    }

    @Override
    public void deleteAiModel(Long id) {
        validateExists(id);
        aiModelMapper.deleteById(id);
    }

    @Override
    public void deleteAiModelListByIds(List<Long> ids) {
        aiModelMapper.deleteByIds(ids);
    }

    @Override
    public EscAiModelDO getAiModel(Long id) {
        return aiModelMapper.selectById(id);
    }

    @Override
    public PageResult<EscAiModelDO> getAiModelPage(EscAiModelPageReqVO pageReqVO) {
        return aiModelMapper.selectPage(pageReqVO);
    }

    @Override
    public List<EscAiModelDO> getEnabledList() {
        return aiModelMapper.selectEnabledList();
    }

    private void validateExists(Long id) {
        if (aiModelMapper.selectById(id) == null) {
            throw exception(AI_MODEL_NOT_EXISTS);
        }
    }
}
