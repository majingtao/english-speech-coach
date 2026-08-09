package cn.kugua.module.english.controller.app.expression;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.kugua.module.english.controller.app.expression.vo.AppExpressionAttemptReqVO;
import cn.kugua.module.english.controller.app.expression.vo.AppExpressionAttemptRespVO;
import cn.kugua.module.english.controller.app.expression.vo.AppExpressionItemRespVO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionAttemptDO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionItemDO;
import cn.kugua.module.english.dal.dataobject.expression.ExpressionThemeDO;
import cn.kugua.module.english.dal.dataobject.expression.UserExpressionProgressDO;
import cn.kugua.module.english.service.expression.ExpressionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.kugua.module.english.enums.ErrorCodeConstants.EXPRESSION_ITEM_NOT_EXISTS;

@Tag(name = "H5 - 表达练习")
@RestController
@RequestMapping("/english/expression")
public class AppExpressionController {

    @Resource
    private ExpressionService expressionService;

    @GetMapping("/theme/list")
    @Operation(summary = "已发布的表达主题")
    public CommonResult<List<ExpressionThemeDO>> themeList(
            @RequestParam(value = "level", defaultValue = "ket") String level) {
        return success(expressionService.getEnabledThemes(level));
    }

    @GetMapping("/item/list")
    @Operation(summary = "按主题获取已发布练习项及当前用户进度")
    public CommonResult<List<AppExpressionItemRespVO>> itemList(
            @RequestParam(value = "level", defaultValue = "ket") String level,
            @RequestParam("themeCode") String themeCode) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<ExpressionItemDO> items = expressionService.getPublishedItems(level, themeCode);
        List<AppExpressionItemRespVO> result = new ArrayList<>(items.size());
        for (ExpressionItemDO item : items) {
            result.add(toItemResp(item, expressionService.getProgress(userId, item.getId())));
        }
        return success(result);
    }

    @GetMapping("/item/{id}")
    @Operation(summary = "获取表达练习详情")
    public CommonResult<AppExpressionItemRespVO> itemDetail(@PathVariable("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        ExpressionItemDO item = expressionService.getItem(id);
        if (item == null || !Integer.valueOf(1).equals(item.getStatus())) {
            throw exception(EXPRESSION_ITEM_NOT_EXISTS);
        }
        return success(toItemResp(item, expressionService.getProgress(userId, id)));
    }

    @PostMapping("/item/{id}/attempt")
    @Operation(summary = "保存口语或写作练习结果并更新掌握度")
    public CommonResult<AppExpressionAttemptRespVO> submitAttempt(
            @PathVariable("id") Long id,
            @Valid @RequestBody AppExpressionAttemptReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        ExpressionAttemptDO attempt = expressionService.submitAttempt(userId, id, reqVO);
        UserExpressionProgressDO progress = expressionService.getProgress(userId, id);
        AppExpressionAttemptRespVO resp = BeanUtils.toBean(progress, AppExpressionAttemptRespVO.class);
        resp.setAttemptId(attempt.getId());
        if (progress.getNextReviewAt() != null) resp.setNextReviewAt(progress.getNextReviewAt().toString());
        return success(resp);
    }

    private AppExpressionItemRespVO toItemResp(ExpressionItemDO item, UserExpressionProgressDO progress) {
        AppExpressionItemRespVO resp = BeanUtils.toBean(item, AppExpressionItemRespVO.class);
        if (progress != null) {
            resp.setProgressStatus(progress.getStatus());
            resp.setRepetitions(progress.getRepetitions());
            resp.setBestSpeakingScore(progress.getBestSpeakingScore());
            resp.setBestWritingScore(progress.getBestWritingScore());
            if (progress.getNextReviewAt() != null) resp.setNextReviewAt(progress.getNextReviewAt().toString());
        }
        return resp;
    }
}
