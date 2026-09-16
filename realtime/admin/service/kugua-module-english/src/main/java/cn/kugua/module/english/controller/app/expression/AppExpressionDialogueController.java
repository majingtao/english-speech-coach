package cn.kugua.module.english.controller.app.expression;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.kugua.module.english.controller.app.expression.vo.AppExpressionDialogueAttemptReqVO;
import cn.kugua.module.english.controller.app.expression.vo.AppExpressionDialogueTaskRespVO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionDialogueAttemptDO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionDialogueTaskDO;
import cn.kugua.module.english.service.expression.ExpressionDialogueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "H5 - KET Part 2 双人互动")
@RestController
@RequestMapping("/english/expression/dialogue")
public class AppExpressionDialogueController {
    @Resource
    private ExpressionDialogueService dialogueService;

    @GetMapping("/list")
    @Operation(summary = "获取已发布的 Part 2 互动题")
    public CommonResult<List<AppExpressionDialogueTaskRespVO>> list(
            @RequestParam(value = "level", defaultValue = "ket") String level) {
        return success(BeanUtils.toBean(dialogueService.getPublishedTasks(level), AppExpressionDialogueTaskRespVO.class));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取 Part 2 互动题详情")
    public CommonResult<AppExpressionDialogueTaskRespVO> detail(@PathVariable("id") Long id) {
        ExpressionDialogueTaskDO task = dialogueService.getPublishedTask(id);
        return success(BeanUtils.toBean(task, AppExpressionDialogueTaskRespVO.class));
    }

    @PostMapping("/{id}/attempt")
    @Operation(summary = "保存 Part 2 互动练习记录")
    public CommonResult<Long> submitAttempt(
            @PathVariable("id") Long id,
            @Valid @RequestBody AppExpressionDialogueAttemptReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        ExpressionDialogueAttemptDO attempt = dialogueService.submitAttempt(userId, id, reqVO);
        return success(attempt.getId());
    }
}
