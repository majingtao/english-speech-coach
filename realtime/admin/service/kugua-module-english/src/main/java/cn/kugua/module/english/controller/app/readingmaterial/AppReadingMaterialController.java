package cn.kugua.module.english.controller.app.readingmaterial;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.kugua.module.english.dal.dataobject.readingmaterial.ReadingMaterialDO;
import cn.kugua.module.english.dal.dataobject.vocab.VocabDO;
import cn.kugua.module.english.dal.mysql.vocab.VocabMapper;
import cn.kugua.module.english.service.readingmaterial.ReadingMaterialService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "H5 - 自由跟读")
@RestController
@RequestMapping("/english/reading-material")
public class AppReadingMaterialController {

    @Resource
    private ReadingMaterialService readingMaterialService;

    @Resource
    private VocabMapper vocabMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/list")
    @Operation(summary = "自由跟读素材列表")
    public CommonResult<List<ReadingMaterialDO>> list(
            @RequestParam(value = "level", defaultValue = "ket") String level,
            @RequestParam(value = "materialType", required = false) String materialType,
            @RequestParam(value = "tag", required = false) String tag) {
        List<ReadingMaterialDO> materials = readingMaterialService.getPublishedMaterials(level, materialType, tag);
        for (ReadingMaterialDO material : materials) {
            attachVocabAudio(material);
        }
        return success(materials);
    }

    @GetMapping("/tags")
    @Operation(summary = "自由跟读素材标签")
    public CommonResult<Set<String>> tags(@RequestParam(value = "level", defaultValue = "ket") String level) {
        Set<String> tags = new LinkedHashSet<>();
        for (ReadingMaterialDO material : readingMaterialService.getPublishedMaterials(level, null, null)) {
            try {
                List<String> parsed = objectMapper.readValue(material.getTagsJson(), new TypeReference<List<String>>() {});
                tags.addAll(parsed);
            } catch (Exception ignored) {
                // Ignore legacy malformed tag snapshots.
            }
        }
        return success(tags);
    }

    private void attachVocabAudio(ReadingMaterialDO material) {
        if (!"word".equals(material.getMaterialType()) || material.getTextEn() == null) return;
        String word = material.getTextEn().trim();
        if (word.isEmpty() || word.contains(" ")) return;
        String levelCode = material.getLevelCode() == null || material.getLevelCode().trim().isEmpty()
                ? "ket" : material.getLevelCode();
        VocabDO vocab = vocabMapper.selectPublishedByLevelAndWord(levelCode, word);
        if (vocab == null) {
            vocab = vocabMapper.selectPublishedByLevelAndWord(levelCode, word.toLowerCase());
        }
        if (vocab == null) return;
        material.setVocabId(vocab.getId());
        material.setVocabWord(vocab.getWord());
        material.setAudioUkUrl(vocab.getAudioUkUrl());
        material.setAudioUsUrl(vocab.getAudioUsUrl());
    }
}
