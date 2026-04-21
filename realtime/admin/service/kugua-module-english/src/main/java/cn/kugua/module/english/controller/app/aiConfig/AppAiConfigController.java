package cn.kugua.module.english.controller.app.aiConfig;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.kugua.module.english.dal.dataobject.aiModel.EscAiModelDO;
import cn.kugua.module.english.service.aiModel.EscAiModelService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "H5 - AI 服务配置")
@RestController
@RequestMapping("/esc/ai-config")
@Validated
public class AppAiConfigController {

    @Resource
    private EscAiModelService aiModelService;

    @Resource
    private ObjectMapper objectMapper;

    @GetMapping
    @Operation(summary = "获取 AI 服务配置（LLM / ASR / TTS）")
    public CommonResult<Map<String, Object>> getAiConfig() {
        List<EscAiModelDO> all = aiModelService.getEnabledList();
        Map<String, List<EscAiModelDO>> grouped = all.stream()
                .collect(Collectors.groupingBy(EscAiModelDO::getType, LinkedHashMap::new, Collectors.toList()));

        Map<String, Object> result = new HashMap<>();
        result.put("llmModels", toModelList(grouped.getOrDefault("llm", List.of())));
        result.put("asrModels", toModelList(grouped.getOrDefault("asr", List.of())));
        result.put("ttsEngines", toModelList(grouped.getOrDefault("tts", List.of())));
        result.put("defaults", buildDefaults(all));
        return success(result);
    }

    private List<Map<String, Object>> toModelList(List<EscAiModelDO> models) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (EscAiModelDO m : models) {
            Map<String, Object> item = new LinkedHashMap<>();
            if ("llm".equals(m.getType())) {
                item.put("provider", m.getProvider() != null ? m.getProvider() : "");
                item.put("model", m.getModelKey());
                item.put("label", m.getLabel());
                mergeConfigJson(item, m.getConfigJson());
            } else if ("asr".equals(m.getType())) {
                item.put("id", m.getModelKey());
                item.put("type", m.getProvider() != null ? m.getProvider() : "offline");
                item.put("label", m.getLabel());
            } else if ("tts".equals(m.getType())) {
                item.put("id", m.getModelKey());
                item.put("label", m.getLabel());
            }
            list.add(item);
        }
        return list;
    }

    private Map<String, Object> buildDefaults(List<EscAiModelDO> all) {
        Map<String, Object> defaults = new HashMap<>();
        for (EscAiModelDO m : all) {
            if (m.getIsDefault() != null && m.getIsDefault() == 1) {
                switch (m.getType()) {
                    case "llm" -> defaults.put("llm",
                            (m.getProvider() != null ? m.getProvider() : "") + ":" + m.getModelKey());
                    case "asr" -> defaults.put("asr", m.getModelKey());
                    case "tts" -> defaults.put("ttsEngine", m.getModelKey());
                }
            }
        }
        return defaults;
    }

    private void mergeConfigJson(Map<String, Object> target, String json) {
        if (json == null || json.isBlank()) return;
        try {
            Map<String, Object> extra = objectMapper.readValue(json, new TypeReference<>() {});
            target.putAll(extra);
        } catch (Exception ignored) {
        }
    }
}
