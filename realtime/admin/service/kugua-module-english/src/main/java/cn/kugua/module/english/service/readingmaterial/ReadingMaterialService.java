package cn.kugua.module.english.service.readingmaterial;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.readingmaterial.vo.ReadingMaterialPageReqVO;
import cn.kugua.module.english.controller.admin.readingmaterial.vo.ReadingMaterialSaveReqVO;
import cn.kugua.module.english.dal.dataobject.readingmaterial.ReadingMaterialDO;

import java.util.List;

public interface ReadingMaterialService {

    Long createMaterial(ReadingMaterialSaveReqVO reqVO);

    void updateMaterial(ReadingMaterialSaveReqVO reqVO);

    void deleteMaterial(Long id);

    ReadingMaterialDO getMaterial(Long id);

    PageResult<ReadingMaterialDO> getMaterialPage(ReadingMaterialPageReqVO reqVO);

    List<ReadingMaterialDO> getPublishedMaterials(String levelCode, String materialType, String tag);

    List<ReadingMaterialDO> getPublishedMaterials(String levelCode, String materialType, String tag, String priority);

    PageResult<ReadingMaterialDO> getPublishedMaterialPage(String levelCode, String materialType, String tag,
                                                           String priority, cn.iocoder.yudao.framework.common.pojo.PageParam page);
}
