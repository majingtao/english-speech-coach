package cn.kugua.module.english.controller.app.dictation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.dictation.vo.DictationWordRespVO;
import cn.kugua.module.english.dal.dataobject.dictationRecord.DictationRecordDO;
import cn.kugua.module.english.dal.dataobject.dictationWord.DictationWordDO;
import cn.kugua.module.english.dal.dataobject.dictationWordlist.DictationWordlistDO;
import cn.kugua.module.english.dal.dataobject.dictationWordlistWord.DictationWordlistWordDO;
import cn.kugua.module.english.service.dictation.DictationRecordService;
import cn.kugua.module.english.service.dictation.DictationWordService;
import cn.kugua.module.english.service.dictation.DictationWordlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * H5 学生端 - 听写接口
 */
@Tag(name = "H5 - 听写")
@RestController
@RequestMapping("/english/app/dictation")
public class AppDictationController {

    @Resource
    private DictationWordlistService wordlistService;

    @Resource
    private DictationWordService wordService;

    @Resource(name = "dictationRecordService")
    private DictationRecordService recordService;

    @GetMapping("/wordlist/tree")
    @Operation(summary = "获取词书分类树")
    @PreAuthorize("@ss.hasPermission('english:question-bank:query')")
    public CommonResult<Map<String, Object>> getWordlistTree() {
        List<DictationWordlistDO> allLists = wordlistService.listPublished();

        // 按 categoryType 分组
        Map<String, List<DictationWordlistDO>> byType = allLists.stream()
                .collect(Collectors.groupingBy(DictationWordlistDO::getCategoryType));

        // 构建教材听写树
        List<Map<String, Object>> schoolTree = new ArrayList<>();
        List<DictationWordlistDO> schoolLists = byType.getOrDefault("SCHOOL_GRADE", List.of());
        Map<String, Map<Integer, List<DictationWordlistDO>>> byLevelGrade = new LinkedHashMap<>();
        for (DictationWordlistDO wl : schoolLists) {
            byLevelGrade
                    .computeIfAbsent(wl.getSchoolLevel(), k -> new LinkedHashMap<>())
                    .computeIfAbsent(wl.getGrade() != null ? wl.getGrade() : 0, k -> new ArrayList<>())
                    .add(wl);
        }
        for (Map.Entry<String, Map<Integer, List<DictationWordlistDO>>> levelEntry : byLevelGrade.entrySet()) {
            Map<String, Object> levelNode = new LinkedHashMap<>();
            levelNode.put("schoolLevel", levelEntry.getKey());
            levelNode.put("label", switch (levelEntry.getKey()) {
                case "primary" -> "小学";
                case "middle" -> "初中";
                case "high" -> "高中";
                default -> levelEntry.getKey();
            });
            List<Map<String, Object>> grades = new ArrayList<>();
            for (Map.Entry<Integer, List<DictationWordlistDO>> gradeEntry : levelEntry.getValue().entrySet()) {
                Map<String, Object> gradeNode = new LinkedHashMap<>();
                gradeNode.put("grade", gradeEntry.getKey());
                gradeNode.put("wordlists", gradeEntry.getValue().stream().map(this::toSimpleMap).toList());
                grades.add(gradeNode);
            }
            levelNode.put("grades", grades);
            schoolTree.add(levelNode);
        }

        // 构建考试听写树
        List<Map<String, Object>> examTree = new ArrayList<>();
        List<DictationWordlistDO> examLists = byType.getOrDefault("EXAM", List.of());
        Map<String, List<DictationWordlistDO>> byExam = examLists.stream()
                .collect(Collectors.groupingBy(wl -> wl.getExamLevelCode() != null ? wl.getExamLevelCode() : "other",
                        LinkedHashMap::new, Collectors.toList()));
        for (Map.Entry<String, List<DictationWordlistDO>> entry : byExam.entrySet()) {
            Map<String, Object> examNode = new LinkedHashMap<>();
            examNode.put("examLevelCode", entry.getKey());
            examNode.put("wordlists", entry.getValue().stream().map(this::toSimpleMap).toList());
            examTree.add(examNode);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("school", schoolTree);
        result.put("exam", examTree);
        return success(result);
    }

    private Map<String, Object> toSimpleMap(DictationWordlistDO wl) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", wl.getId());
        m.put("name", wl.getName());
        m.put("wordCount", wl.getWordCount());
        m.put("edition", wl.getEdition());
        m.put("unitLabel", wl.getUnitLabel());
        return m;
    }

    @GetMapping("/wordlist/words")
    @Operation(summary = "获取词书所有单词（练习用）")
    @Parameter(name = "wordlistId", description = "词书ID", required = true)
    @PreAuthorize("@ss.hasPermission('english:question-bank:query')")
    public CommonResult<List<DictationWordRespVO>> getWordlistWords(@RequestParam("wordlistId") Long wordlistId) {
        List<DictationWordlistWordDO> links = wordlistService.getWordlistWords(wordlistId);
        List<Long> wordIds = links.stream().map(DictationWordlistWordDO::getWordId).toList();
        if (wordIds.isEmpty()) {
            return success(List.of());
        }
        List<DictationWordDO> words = wordService.getWordsByIds(wordIds);
        // 按 link 顺序排列
        Map<Long, DictationWordDO> wordMap = words.stream().collect(Collectors.toMap(DictationWordDO::getId, w -> w));
        List<DictationWordDO> ordered = wordIds.stream().map(wordMap::get).filter(Objects::nonNull).toList();
        return success(BeanUtils.toBean(ordered, DictationWordRespVO.class));
    }

    @PostMapping("/record/mark")
    @Operation(summary = "提交练习标记")
    @PreAuthorize("@ss.hasPermission('english:question-bank:query')")
    public CommonResult<Long> mark(@RequestBody Map<String, Object> body) {
        DictationRecordDO record = new DictationRecordDO();
        record.setStudentId(Long.valueOf(body.get("studentId").toString()));
        record.setWordId(Long.valueOf(body.get("wordId").toString()));
        if (body.get("wordlistId") != null) {
            record.setWordlistId(Long.valueOf(body.get("wordlistId").toString()));
        }
        record.setKnowMeaning(Boolean.valueOf(body.getOrDefault("knowMeaning", false).toString()));
        record.setCanSpell(Boolean.valueOf(body.getOrDefault("canSpell", false).toString()));
        return success(recordService.createRecord(record));
    }

    @GetMapping("/record/stats")
    @Operation(summary = "获取学生在词书的练习统计")
    @PreAuthorize("@ss.hasPermission('english:question-bank:query')")
    public CommonResult<Map<String, Object>> getStats(@RequestParam("studentId") Long studentId,
                                                      @RequestParam("wordlistId") Long wordlistId) {
        List<DictationRecordDO> records = recordService.getRecordsByStudentAndWordlist(studentId, wordlistId);
        int total = records.size();
        long know = records.stream().filter(DictationRecordDO::getKnowMeaning).count();
        long spell = records.stream().filter(DictationRecordDO::getCanSpell).count();
        long distinctWords = records.stream().map(DictationRecordDO::getWordId).distinct().count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalRecords", total);
        stats.put("knowCount", know);
        stats.put("spellCount", spell);
        stats.put("distinctWords", distinctWords);
        return success(stats);
    }

}


