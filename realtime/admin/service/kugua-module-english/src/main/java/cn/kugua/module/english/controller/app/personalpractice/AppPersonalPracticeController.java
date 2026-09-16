package cn.kugua.module.english.controller.app.personalpractice;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.kugua.module.english.controller.app.personalpractice.vo.AppPersonalPracticeAttemptReqVO;
import cn.kugua.module.english.controller.app.personalpractice.vo.AppPersonalPracticeAttemptRespVO;
import cn.kugua.module.english.controller.app.personalpractice.vo.AppPersonalPracticeRespVO;
import cn.kugua.module.english.dal.dataobject.personalpractice.PersonalPracticeAttemptDO;
import cn.kugua.module.english.dal.dataobject.personalpractice.PersonalPracticeDO;
import cn.kugua.module.english.dal.dataobject.personalpractice.PersonalPracticeProgressDO;
import cn.kugua.module.english.service.personalpractice.PersonalPracticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "H5 - 专属练习")
@RestController
@RequestMapping("/english/personal-practice")
public class AppPersonalPracticeController {

    @Resource
    private PersonalPracticeService personalPracticeService;

    @GetMapping("/list")
    @Operation(summary = "获取当前孩子的已发布专属练习")
    public CommonResult<List<AppPersonalPracticeRespVO>> list(@RequestParam("type") String type) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<PersonalPracticeDO> practices = personalPracticeService.getPublished(type);
        List<AppPersonalPracticeRespVO> result = new ArrayList<>(practices.size());
        for (PersonalPracticeDO practice : practices) {
            result.add(toResp(practice, personalPracticeService.getProgress(userId, practice.getId())));
        }
        return success(result);
    }

    @PostMapping("/{id}/attempt")
    @Operation(summary = "保存专属练习评分结果")
    public CommonResult<AppPersonalPracticeAttemptRespVO> submit(
            @PathVariable("id") Long id,
            @Valid @RequestBody AppPersonalPracticeAttemptReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        PersonalPracticeAttemptDO attempt = personalPracticeService.submitAttempt(userId, id, reqVO);
        PersonalPracticeProgressDO progress = personalPracticeService.getProgress(userId, id);
        AppPersonalPracticeAttemptRespVO resp = new AppPersonalPracticeAttemptRespVO();
        resp.setAttemptId(attempt.getId());
        resp.setProgressStatus(progress.getStatus());
        resp.setAttemptCount(progress.getAttemptCount());
        resp.setBestScore(progress.getBestScore());
        resp.setLastScore(progress.getLastScore());
        return success(resp);
    }

    private AppPersonalPracticeRespVO toResp(PersonalPracticeDO practice, PersonalPracticeProgressDO progress) {
        AppPersonalPracticeRespVO resp = BeanUtils.toBean(practice, AppPersonalPracticeRespVO.class);
        if (progress != null) {
            resp.setProgressStatus(progress.getStatus());
            resp.setAttemptCount(progress.getAttemptCount());
            resp.setBestScore(progress.getBestScore());
            resp.setLastScore(progress.getLastScore());
            if (progress.getLastPracticeAt() != null) {
                resp.setLastPracticeAt(progress.getLastPracticeAt().toString());
            }
        }
        return resp;
    }
}
