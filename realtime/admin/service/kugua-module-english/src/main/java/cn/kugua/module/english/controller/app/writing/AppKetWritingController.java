package cn.kugua.module.english.controller.app.writing;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.kugua.module.english.controller.app.writing.vo.AppKetWritingAttemptReqVO;
import cn.kugua.module.english.controller.app.writing.vo.AppKetWritingAttemptRespVO;
import cn.kugua.module.english.controller.app.writing.vo.AppKetWritingLatestRespVO;
import cn.kugua.module.english.dal.dataobject.writing.KetWritingAttemptDO;
import cn.kugua.module.english.dal.dataobject.writing.KetWritingTaskDO;
import cn.kugua.module.english.service.writing.KetWritingAttemptService;
import cn.kugua.module.english.service.writing.KetWritingTaskService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "H5 - KET写作练习")
@RestController
@RequestMapping("/english/writing/ket")
public class AppKetWritingController {

    @Resource
    private KetWritingAttemptService attemptService;
    @Resource
    private KetWritingTaskService taskService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/task/list")
    @Operation(summary = "获取 KET 写作题库")
    public CommonResult<List<Map<String, Object>>> taskList(
            @RequestParam(value = "level", defaultValue = "ket") String level) {
        List<KetWritingTaskDO> tasks = taskService.getPublishedTasks(level);
        List<Map<String, Object>> result = new ArrayList<>(tasks.size());
        for (KetWritingTaskDO task : tasks) {
            result.add(toTaskMap(task));
        }
        return success(result);
    }

    @PostMapping("/attempt")
    @Operation(summary = "保存 KET 写作提交记录")
    public CommonResult<AppKetWritingAttemptRespVO> submitAttempt(
            @Valid @RequestBody AppKetWritingAttemptReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        KetWritingAttemptDO attempt = attemptService.submitAttempt(userId, reqVO);
        return success(toAttemptResp(attempt, attemptService.countAttempts(userId, reqVO.getTaskId())));
    }

    @GetMapping("/attempt/latest")
    @Operation(summary = "获取当前用户某道 KET 写作题最近一次提交")
    public CommonResult<AppKetWritingLatestRespVO> latestAttempt(@RequestParam("taskId") String taskId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        AppKetWritingLatestRespVO resp = new AppKetWritingLatestRespVO();
        Integer count = attemptService.countAttempts(userId, taskId);
        resp.setAttemptCount(count);
        KetWritingAttemptDO latest = attemptService.getLatestAttempt(userId, taskId);
        if (latest != null) {
            resp.setLatest(toAttemptResp(latest, count));
        }
        return success(resp);
    }

    private AppKetWritingAttemptRespVO toAttemptResp(KetWritingAttemptDO attempt, Integer attemptCount) {
        AppKetWritingAttemptRespVO resp = BeanUtils.toBean(attempt, AppKetWritingAttemptRespVO.class);
        resp.setAttemptId(attempt.getId());
        resp.setAttemptCount(attemptCount);
        return resp;
    }

    private Map<String, Object> toTaskMap(KetWritingTaskDO task) {
        try {
            Map<String, Object> map = objectMapper.readValue(task.getContentJson(),
                    new TypeReference<Map<String, Object>>() {});
            map.put("id", task.getTaskId());
            map.put("status", task.getStatus());
            map.put("part", task.getPart());
            map.put("type", task.getType());
            map.put("title", task.getTitle());
            map.put("sourceBook", task.getSourceBook());
            map.put("sourceUnit", task.getSourceUnit());
            map.put("sourcePage", task.getSourcePage());
            return map;
        } catch (Exception e) {
            throw new IllegalArgumentException("KET 写作题库 JSON 无效: " + task.getTaskId(), e);
        }
    }
}
