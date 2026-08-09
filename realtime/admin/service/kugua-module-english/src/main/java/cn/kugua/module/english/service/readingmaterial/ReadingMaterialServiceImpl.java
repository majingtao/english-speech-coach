package cn.kugua.module.english.service.readingmaterial;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.kugua.module.english.controller.admin.readingmaterial.vo.ReadingMaterialPageReqVO;
import cn.kugua.module.english.controller.admin.readingmaterial.vo.ReadingMaterialSaveReqVO;
import cn.kugua.module.english.dal.dataobject.readingmaterial.ReadingMaterialDO;
import cn.kugua.module.english.dal.mysql.readingmaterial.ReadingMaterialMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.READING_MATERIAL_DUPLICATE;
import static cn.kugua.module.english.enums.ErrorCodeConstants.READING_MATERIAL_NOT_EXISTS;

@Service
public class ReadingMaterialServiceImpl implements ReadingMaterialService {

    @Resource
    private ReadingMaterialMapper materialMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Long createMaterial(ReadingMaterialSaveReqVO reqVO) {
        validateUnique(null, reqVO.getLevelCode(), reqVO.getTextEn());
        validateJsonFields(reqVO);
        ReadingMaterialDO material = BeanUtils.toBean(reqVO, ReadingMaterialDO.class);
        applyDefaults(material);
        materialMapper.insert(material);
        return material.getId();
    }

    @Override
    public void updateMaterial(ReadingMaterialSaveReqVO reqVO) {
        validateExists(reqVO.getId());
        validateUnique(reqVO.getId(), reqVO.getLevelCode(), reqVO.getTextEn());
        validateJsonFields(reqVO);
        ReadingMaterialDO material = BeanUtils.toBean(reqVO, ReadingMaterialDO.class);
        applyDefaults(material);
        materialMapper.updateById(material);
    }

    @Override
    public void deleteMaterial(Long id) {
        validateExists(id);
        materialMapper.deleteById(id);
    }

    @Override
    public ReadingMaterialDO getMaterial(Long id) {
        return materialMapper.selectById(id);
    }

    @Override
    public PageResult<ReadingMaterialDO> getMaterialPage(ReadingMaterialPageReqVO reqVO) {
        return materialMapper.selectPage(reqVO, reqVO.getText(), reqVO.getMaterialType(),
                reqVO.getPartOfSpeech(), reqVO.getLevelCode(), reqVO.getTag(), reqVO.getPriority(), reqVO.getStatus());
    }

    @Override
    public java.util.List<ReadingMaterialDO> getPublishedMaterials(String levelCode, String materialType, String tag) {
        return getPublishedMaterials(levelCode, materialType, tag, null);
    }

    @Override
    public java.util.List<ReadingMaterialDO> getPublishedMaterials(String levelCode, String materialType, String tag,
                                                                   String priority) {
        return materialMapper.selectPublished(levelCode, materialType, tag, priority);
    }

    @Override
    public PageResult<ReadingMaterialDO> getPublishedMaterialPage(String levelCode, String materialType, String tag,
                                                                  String priority,
                                                                  cn.iocoder.yudao.framework.common.pojo.PageParam page) {
        return materialMapper.selectPublishedPage(page, levelCode, materialType, tag, priority);
    }

    private void applyDefaults(ReadingMaterialDO material) {
        if (material.getTextCn() == null) material.setTextCn("");
        if (material.getDescription() == null) material.setDescription("");
        if (isBlank(material.getMaterialType())) material.setMaterialType("word");
        if (isBlank(material.getPartOfSpeech())) material.setPartOfSpeech("unknown");
        if (isBlank(material.getLevelCode())) material.setLevelCode("ket");
        if (isBlank(material.getTagsJson())) material.setTagsJson("[]");
        if (isBlank(material.getExamplesJson())) material.setExamplesJson("[]");
        if (isBlank(material.getWordFormsJson())) material.setWordFormsJson("{}");
        if (material.getMustKnow() == null) material.setMustKnow(0);
        if (material.getHighFrequency() == null) material.setHighFrequency(0);
        if (material.getSort() == null) material.setSort(0);
        if (material.getStatus() == null) material.setStatus(0);
        material.setTextEn(material.getTextEn().trim());
    }

    private void validateJsonFields(ReadingMaterialSaveReqVO reqVO) {
        validateJson(reqVO.getTagsJson(), true, "标签配置不是有效 JSON");
        validateJson(reqVO.getExamplesJson(), true, "例句配置不是有效 JSON");
        validateJson(reqVO.getWordFormsJson(), false, "词形配置不是有效 JSON");
    }

    private void validateJson(String value, boolean array, String message) {
        if (isBlank(value)) return;
        try {
            JsonNode node = objectMapper.readTree(value);
            if ((array && !node.isArray()) || (!array && !node.isObject())) {
                throw new IllegalArgumentException(message);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(message, e);
        }
    }

    private void validateExists(Long id) {
        if (id == null || materialMapper.selectById(id) == null) throw exception(READING_MATERIAL_NOT_EXISTS);
    }

    private void validateUnique(Long id, String levelCode, String textEn) {
        String level = isBlank(levelCode) ? "ket" : levelCode;
        ReadingMaterialDO existing = materialMapper.selectOne(new LambdaQueryWrapperX<ReadingMaterialDO>()
                .eq(ReadingMaterialDO::getLevelCode, level)
                .eq(ReadingMaterialDO::getTextEn, textEn == null ? "" : textEn.trim()));
        if (existing != null && (id == null || !id.equals(existing.getId()))) {
            throw exception(READING_MATERIAL_DUPLICATE);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
