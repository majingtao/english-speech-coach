package cn.kugua.module.english.service.expression;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.app.expression.vo.AppExpressionDialogueAttemptReqVO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionDialogueAttemptDO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionDialogueTaskDO;
import cn.kugua.module.english.dal.mysql.expression.ExpressionDialogueAttemptMapper;
import cn.kugua.module.english.dal.mysql.expression.ExpressionDialogueTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.kugua.module.english.enums.ErrorCodeConstants.EXPRESSION_DIALOGUE_ROLE_INVALID;
import static cn.kugua.module.english.enums.ErrorCodeConstants.EXPRESSION_DIALOGUE_TASK_NOT_EXISTS;

@Service
public class ExpressionDialogueServiceImpl implements ExpressionDialogueService {
    @Resource
    private ExpressionDialogueTaskMapper taskMapper;
    @Resource
    private ExpressionDialogueAttemptMapper attemptMapper;

    @Override
    public List<ExpressionDialogueTaskDO> getPublishedTasks(String levelCode) {
        return taskMapper.selectPublishedByLevel(levelCode);
    }

    @Override
    public ExpressionDialogueTaskDO getPublishedTask(Long id) {
        ExpressionDialogueTaskDO task = taskMapper.selectById(id);
        if (task == null || !Integer.valueOf(1).equals(task.getStatus())) {
            throw exception(EXPRESSION_DIALOGUE_TASK_NOT_EXISTS);
        }
        return task;
    }

    @Override
    @Transactional
    public ExpressionDialogueAttemptDO submitAttempt(
            Long userId, Long taskId, AppExpressionDialogueAttemptReqVO reqVO) {
        getPublishedTask(taskId);
        String role = reqVO.getSelectedRole().trim().toLowerCase();
        if (!"student_a".equals(role) && !"student_b".equals(role)) {
            throw exception(EXPRESSION_DIALOGUE_ROLE_INVALID);
        }
        ExpressionDialogueAttemptDO attempt = BeanUtils.toBean(reqVO, ExpressionDialogueAttemptDO.class);
        attempt.setUserId(userId);
        attempt.setTaskId(taskId);
        attempt.setSelectedRole(role);
        attemptMapper.insert(attempt);
        return attempt;
    }
}
