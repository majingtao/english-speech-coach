package cn.kugua.module.english.controller.app.readingmaterial;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.kugua.module.english.controller.app.readingmaterial.vo.AppReadingMaterialPageReqVO;
import cn.kugua.module.english.controller.app.readingmaterial.vo.AppReadingMaterialProgressRespVO;
import cn.kugua.module.english.controller.app.readingmaterial.vo.AppReadingMaterialSelfCheckReqVO;
import cn.kugua.module.english.dal.dataobject.readingmaterial.ReadingMaterialDO;
import cn.kugua.module.english.dal.dataobject.readingmaterial.UserReadingMaterialProgressDO;
import cn.kugua.module.english.dal.mysql.readingmaterial.UserReadingMaterialProgressMapper;
import cn.kugua.module.english.dal.dataobject.vocab.VocabDO;
import cn.kugua.module.english.dal.mysql.vocab.VocabMapper;
import cn.kugua.module.english.service.readingmaterial.ReadingMaterialService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.kugua.module.english.enums.ErrorCodeConstants.READING_MATERIAL_NOT_EXISTS;
import static cn.kugua.module.english.enums.ErrorCodeConstants.READING_MATERIAL_SELF_CHECK_INVALID;

@Tag(name = "H5 - 自由跟读")
@RestController
@RequestMapping("/english/reading-material")
public class AppReadingMaterialController {

    @Resource
    private ReadingMaterialService readingMaterialService;

    @Resource
    private VocabMapper vocabMapper;

    @Resource
    private UserReadingMaterialProgressMapper progressMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/list")
    @Operation(summary = "自由跟读素材列表")
    public CommonResult<List<ReadingMaterialDO>> list(
            @RequestParam(value = "level", defaultValue = "ket") String level,
            @RequestParam(value = "materialType", required = false) String materialType,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "priority", required = false) String priority) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<ReadingMaterialDO> materials = readingMaterialService.getPublishedMaterials(level, materialType, tag, priority);
        for (ReadingMaterialDO material : materials) {
            attachVocabAudio(material);
            attachProgress(material, userId);
        }
        return success(materials);
    }

    @GetMapping("/page")
    @Operation(summary = "自由跟读素材分页")
    public CommonResult<PageResult<ReadingMaterialDO>> page(AppReadingMaterialPageReqVO reqVO) {
        String level = reqVO.getLevel() == null || reqVO.getLevel().trim().isEmpty() ? "ket" : reqVO.getLevel();
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        PageResult<ReadingMaterialDO> page = readingMaterialService.getPublishedMaterialPage(
                level, reqVO.getMaterialType(), reqVO.getTag(), reqVO.getPriority(), reqVO);
        for (ReadingMaterialDO material : page.getList()) {
            attachVocabAudio(material);
            attachProgress(material, userId);
        }
        return success(page);
    }

    @PostMapping("/{id}/self-check")
    @Operation(summary = "提交自由跟读自评结果")
    public CommonResult<AppReadingMaterialProgressRespVO> selfCheck(
            @PathVariable("id") Long id,
            @Valid @RequestBody AppReadingMaterialSelfCheckReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        ReadingMaterialDO material = readingMaterialService.getMaterial(id);
        if (material == null || material.getStatus() == null || material.getStatus() != 1) {
            throw exception(READING_MATERIAL_NOT_EXISTS);
        }
        String result = reqVO.getResult();
        if (!"correct".equals(result) && !"wrong".equals(result)) {
            throw exception(READING_MATERIAL_SELF_CHECK_INVALID);
        }
        UserReadingMaterialProgressDO progress = progressMapper.selectByUserAndMaterial(userId, id);
        if (progress == null) {
            progress = new UserReadingMaterialProgressDO();
            progress.setUserId(userId);
            progress.setMaterialId(id);
            progress.setCorrectCount(0);
            progress.setWrongCount(0);
            progress.setLastResult(result);
            progress.setLastPracticeAt(LocalDateTime.now());
            if ("correct".equals(result)) {
                progress.setCorrectCount(1);
            } else {
                progress.setWrongCount(1);
            }
            progressMapper.insert(progress);
        } else {
            if ("correct".equals(result)) {
                progress.setCorrectCount(safeCount(progress.getCorrectCount()) + 1);
            } else {
                progress.setWrongCount(safeCount(progress.getWrongCount()) + 1);
            }
            progress.setLastResult(result);
            progress.setLastPracticeAt(LocalDateTime.now());
            progressMapper.updateById(progress);
        }
        return success(toProgressResp(progress));
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

    private void attachProgress(ReadingMaterialDO material, Long userId) {
        if (userId == null || material == null || material.getId() == null) {
            fillEmptyProgress(material);
            return;
        }
        UserReadingMaterialProgressDO progress = progressMapper.selectByUserAndMaterial(userId, material.getId());
        if (progress == null) {
            fillEmptyProgress(material);
            return;
        }
        material.setCorrectCount(safeCount(progress.getCorrectCount()));
        material.setWrongCount(safeCount(progress.getWrongCount()));
        material.setLastResult(progress.getLastResult());
        material.setLastPracticeAt(progress.getLastPracticeAt() == null ? "" : progress.getLastPracticeAt().toString());
    }

    private void fillEmptyProgress(ReadingMaterialDO material) {
        if (material == null) return;
        material.setCorrectCount(0);
        material.setWrongCount(0);
        material.setLastResult("");
        material.setLastPracticeAt("");
    }

    private AppReadingMaterialProgressRespVO toProgressResp(UserReadingMaterialProgressDO progress) {
        AppReadingMaterialProgressRespVO resp = new AppReadingMaterialProgressRespVO();
        resp.setMaterialId(progress.getMaterialId());
        resp.setCorrectCount(safeCount(progress.getCorrectCount()));
        resp.setWrongCount(safeCount(progress.getWrongCount()));
        resp.setLastResult(progress.getLastResult());
        resp.setLastPracticeAt(progress.getLastPracticeAt() == null ? "" : progress.getLastPracticeAt().toString());
        return resp;
    }

    private int safeCount(Integer value) {
        return value == null ? 0 : value;
    }
}
