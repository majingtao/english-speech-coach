package cn.kugua.module.english.service.expression;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.kugua.module.english.controller.admin.expression.vo.ExpressionItemPageReqVO;
import cn.kugua.module.english.controller.admin.expression.vo.ExpressionItemSaveReqVO;
import cn.kugua.module.english.controller.admin.expression.vo.ExpressionThemeSaveReqVO;
import cn.kugua.module.english.controller.app.expression.vo.AppExpressionAttemptReqVO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionAttemptDO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionItemDO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionThemeDO;
import cn.kugua.module.english.dal.dataobject.expression.UserExpressionProgressDO;

import java.util.List;

public interface ExpressionService {
    Long createTheme(ExpressionThemeSaveReqVO reqVO);
    void updateTheme(ExpressionThemeSaveReqVO reqVO);
    void deleteTheme(Long id);
    List<ExpressionThemeDO> getEnabledThemes(String levelCode);
    List<ExpressionThemeDO> getAllThemes(String levelCode);

    Long createItem(ExpressionItemSaveReqVO reqVO);
    void updateItem(ExpressionItemSaveReqVO reqVO);
    void deleteItem(Long id);
    ExpressionItemDO getItem(Long id);
    PageResult<ExpressionItemDO> getItemPage(ExpressionItemPageReqVO reqVO);
    List<ExpressionItemDO> getPublishedItems(String levelCode, String themeCode);

    UserExpressionProgressDO getProgress(Long userId, Long itemId);
    ExpressionAttemptDO submitAttempt(Long userId, Long itemId, AppExpressionAttemptReqVO reqVO);
}
