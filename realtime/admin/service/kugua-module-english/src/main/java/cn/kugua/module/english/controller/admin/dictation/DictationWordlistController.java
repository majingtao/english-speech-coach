package cn.kugua.module.english.controller.admin.dictation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.dictation.vo.*;
import cn.kugua.module.english.dal.dataobject.dictationWord.DictationWordDO;
import cn.kugua.module.english.dal.dataobject.dictationWordlist.DictationWordlistDO;
import cn.kugua.module.english.dal.dataobject.dictationWordlistWord.DictationWordlistWordDO;
import cn.kugua.module.english.service.dictation.DictationWordService;
import cn.kugua.module.english.service.dictation.DictationWordlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 听写词书")
@RestController
@RequestMapping("/english/dictation/wordlist")
public class DictationWordlistController {

    @Resource
    private DictationWordlistService wordlistService;

    @Resource
    private DictationWordService wordService;

    @PostMapping("/create")
    @Operation(summary = "创建词书")
    @PreAuthorize("@ss.hasPermission('english:dictation-wordlist:create')")
    public CommonResult<Long> createWordlist(@Valid @RequestBody DictationWordlistSaveReqVO reqVO) {
        return success(wordlistService.createWordlist(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新词书")
    @PreAuthorize("@ss.hasPermission('english:dictation-wordlist:update')")
    public CommonResult<Boolean> updateWordlist(@Valid @RequestBody DictationWordlistSaveReqVO reqVO) {
        wordlistService.updateWordlist(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除词书")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:dictation-wordlist:delete')")
    public CommonResult<Boolean> deleteWordlist(@RequestParam("id") Long id) {
        wordlistService.deleteWordlist(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得词书")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('english:dictation-wordlist:query')")
    public CommonResult<DictationWordlistRespVO> getWordlist(@RequestParam("id") Long id) {
        DictationWordlistDO wordlist = wordlistService.getWordlist(id);
        return success(BeanUtils.toBean(wordlist, DictationWordlistRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得词书分页")
    @PreAuthorize("@ss.hasPermission('english:dictation-wordlist:query')")
    public CommonResult<PageResult<DictationWordlistRespVO>> getWordlistPage(@Valid DictationWordlistPageReqVO reqVO) {
        PageResult<DictationWordlistDO> pageResult = wordlistService.getWordlistPage(reqVO);
        return success(BeanUtils.toBean(pageResult, DictationWordlistRespVO.class));
    }

    @PostMapping("/add-words")
    @Operation(summary = "给词书添加单词")
    @PreAuthorize("@ss.hasPermission('english:dictation-wordlist:update')")
    public CommonResult<Boolean> addWords(@RequestBody Map<String, Object> body) {
        Long wordlistId = Long.valueOf(body.get("wordlistId").toString());
        @SuppressWarnings("unchecked")
        List<Number> wordIdNums = (List<Number>) body.get("wordIds");
        List<Long> wordIds = wordIdNums.stream().map(Number::longValue).toList();
        wordlistService.addWordsToWordlist(wordlistId, wordIds);
        return success(true);
    }

    @DeleteMapping("/remove-word")
    @Operation(summary = "从词书移除单词")
    @PreAuthorize("@ss.hasPermission('english:dictation-wordlist:update')")
    public CommonResult<Boolean> removeWord(@RequestParam("wordlistId") Long wordlistId,
                                            @RequestParam("wordId") Long wordId) {
        wordlistService.removeWordFromWordlist(wordlistId, wordId);
        return success(true);
    }

    @GetMapping("/words")
    @Operation(summary = "获取词书的所有单词")
    @Parameter(name = "wordlistId", description = "词书ID", required = true)
    @PreAuthorize("@ss.hasPermission('english:dictation-wordlist:query')")
    public CommonResult<List<DictationWordRespVO>> getWordlistWords(@RequestParam("wordlistId") Long wordlistId) {
        List<DictationWordlistWordDO> links = wordlistService.getWordlistWords(wordlistId);
        List<Long> wordIds = links.stream().map(DictationWordlistWordDO::getWordId).toList();
        if (wordIds.isEmpty()) {
            return success(List.of());
        }
        List<DictationWordDO> words = wordService.getWordsByIds(wordIds);
        return success(BeanUtils.toBean(words, DictationWordRespVO.class));
    }

    @PostMapping("/import-json")
    @Operation(summary = "从 JSON 导入单词到词书")
    @PreAuthorize("@ss.hasPermission('english:dictation-wordlist:update')")
    public CommonResult<String> importJson(@RequestBody Map<String, Object> body) {
        Long wordlistId = Long.valueOf(body.get("wordlistId").toString());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> words = (List<Map<String, Object>>) body.get("words");
        int created = 0;
        List<Long> wordIds = new java.util.ArrayList<>();
        for (Map<String, Object> w : words) {
            String en = (String) w.get("word");
            if (en == null) en = (String) w.get("en");
            if (en == null || en.isBlank()) continue;
            // 尝试找已存在的单词
            DictationWordDO existing = null;
            try {
                DictationWordSaveReqVO vo = new DictationWordSaveReqVO();
                vo.setEn(en.trim());
                vo.setCn((String) w.getOrDefault("cn", ""));
                vo.setPos((String) w.getOrDefault("pos", ""));
                vo.setDifficulty(1);
                vo.setLlmStatus(0);
                Long id = wordService.createWord(vo);
                wordIds.add(id);
                created++;
            } catch (Exception e) {
                // 已存在，查找 ID
                // 通过分页查找
                DictationWordPageReqVO query = new DictationWordPageReqVO();
                query.setEn(en.trim());
                query.setPageNo(1);
                query.setPageSize(1);
                PageResult<DictationWordDO> result = wordService.getWordPage(query);
                if (!result.getList().isEmpty()) {
                    wordIds.add(result.getList().get(0).getId());
                }
            }
        }
        if (!wordIds.isEmpty()) {
            wordlistService.addWordsToWordlist(wordlistId, wordIds);
        }
        return success("导入完成，新建单词 " + created + " 个，关联 " + wordIds.size() + " 个到词书");
    }

}
