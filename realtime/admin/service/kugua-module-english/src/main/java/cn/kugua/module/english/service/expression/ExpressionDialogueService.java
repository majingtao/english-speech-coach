package cn.kugua.module.english.service.expression;

import cn.kugua.module.english.controller.app.expression.vo.AppExpressionDialogueAttemptReqVO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionDialogueAttemptDO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionDialogueTaskDO;

import java.util.List;

public interface ExpressionDialogueService {
    List<ExpressionDialogueTaskDO> getPublishedTasks(String levelCode);
    ExpressionDialogueTaskDO getPublishedTask(Long id);
    ExpressionDialogueAttemptDO submitAttempt(Long userId, Long taskId, AppExpressionDialogueAttemptReqVO reqVO);
}
