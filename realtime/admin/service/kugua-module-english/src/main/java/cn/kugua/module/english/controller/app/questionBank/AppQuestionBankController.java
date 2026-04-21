package cn.kugua.module.english.controller.app.questionBank;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.kugua.module.english.dal.dataobject.exam.ExamDO;
import cn.kugua.module.english.dal.dataobject.examPart.ExamPartDO;
import cn.kugua.module.english.dal.dataobject.partFindDiffDifference.PartFindDiffDifferenceDO;
import cn.kugua.module.english.dal.dataobject.partFindDiffPair.PartFindDiffPairDO;
import cn.kugua.module.english.dal.dataobject.partInfoExchangeCard.PartInfoExchangeCardDO;
import cn.kugua.module.english.dal.dataobject.partInfoExchangeQa.PartInfoExchangeQaDO;
import cn.kugua.module.english.dal.dataobject.partPersonalQaSample.PartPersonalQaSampleDO;
import cn.kugua.module.english.dal.dataobject.partPersonalQuestion.PartPersonalQuestionDO;
import cn.kugua.module.english.dal.dataobject.partTellStoryFrame.PartTellStoryFrameDO;
import cn.kugua.module.english.dal.mysql.exam.ExamMapper;
import cn.kugua.module.english.dal.mysql.examPart.ExamPartMapper;
import cn.kugua.module.english.dal.mysql.partFindDiffDifference.PartFindDiffDifferenceMapper;
import cn.kugua.module.english.dal.mysql.partFindDiffPair.PartFindDiffPairMapper;
import cn.kugua.module.english.dal.mysql.partInfoExchangeCard.PartInfoExchangeCardMapper;
import cn.kugua.module.english.dal.mysql.partInfoExchangeQa.PartInfoExchangeQaMapper;
import cn.kugua.module.english.dal.mysql.partPersonalQaSample.PartPersonalQaSampleMapper;
import cn.kugua.module.english.dal.mysql.partPersonalQuestion.PartPersonalQuestionMapper;
import cn.kugua.module.english.dal.mysql.partTellStoryFrame.PartTellStoryFrameMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 题库聚合接口（H5 会员专用）—— 将多表数据组装为 H5 前端 QuestionBank JSON 格式。
 * 仅允许已登录的 MEMBER 用户调用，未登录返回 401。
 */
@Tag(name = "H5 - 题库聚合")
@RestController
@RequestMapping("/english/question-bank")
@Validated
public class AppQuestionBankController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Resource private ExamMapper examMapper;
    @Resource private ExamPartMapper examPartMapper;
    @Resource private PartFindDiffPairMapper findDiffPairMapper;
    @Resource private PartFindDiffDifferenceMapper findDiffDiffMapper;
    @Resource private PartInfoExchangeCardMapper infoCardMapper;
    @Resource private PartInfoExchangeQaMapper infoQaMapper;
    @Resource private PartTellStoryFrameMapper storyFrameMapper;
    @Resource private PartPersonalQuestionMapper personalQMapper;
    @Resource private PartPersonalQaSampleMapper personalSampleMapper;

    @GetMapping("/all")
    @Operation(summary = "获取题库（H5 刷题用，需登录；支持按级别/系列筛选）")
    public CommonResult<Map<String, Object>> getAll(
            @RequestParam(value = "levelCode", required = false) String levelCode,
            @RequestParam(value = "seriesCode", required = false) String seriesCode) {
        // 1. 查所有激活的试卷
        LambdaQueryWrapper<ExamDO> examQuery = new LambdaQueryWrapper<ExamDO>()
                .eq(ExamDO::getIsActive, 1);
        if (levelCode != null && !levelCode.isBlank()) {
            examQuery.eq(ExamDO::getLevelCode, levelCode);
        }
        if (seriesCode != null && !seriesCode.isBlank()) {
            examQuery.eq(ExamDO::getSeriesCode, seriesCode);
        }
        List<ExamDO> exams = examMapper.selectList(examQuery.orderByAsc(ExamDO::getId));
        if (exams.isEmpty()) {
            return success(Map.of("tests", Map.of()));
        }

        List<Long> examIds = exams.stream().map(ExamDO::getId).toList();

        // 2. 查所有 Part
        List<ExamPartDO> allParts = examPartMapper.selectList(new LambdaQueryWrapper<ExamPartDO>()
                .in(ExamPartDO::getExamId, examIds)
                .orderByAsc(ExamPartDO::getPartNo));
        Map<Long, List<ExamPartDO>> partsByExam = allParts.stream()
                .collect(Collectors.groupingBy(ExamPartDO::getExamId));
        List<Long> allPartIds = allParts.stream().map(ExamPartDO::getId).toList();

        // 3. 批量查所有子表
        List<PartFindDiffPairDO> allPairs = allPartIds.isEmpty() ? List.of() :
                findDiffPairMapper.selectList(new LambdaQueryWrapper<PartFindDiffPairDO>()
                        .in(PartFindDiffPairDO::getPartId, allPartIds));
        List<Long> pairIds = allPairs.stream().map(PartFindDiffPairDO::getId).toList();
        List<PartFindDiffDifferenceDO> allDiffs = pairIds.isEmpty() ? List.of() :
                findDiffDiffMapper.selectList(new LambdaQueryWrapper<PartFindDiffDifferenceDO>()
                        .in(PartFindDiffDifferenceDO::getPairId, pairIds)
                        .orderByAsc(PartFindDiffDifferenceDO::getSort));

        List<PartInfoExchangeCardDO> allCards = allPartIds.isEmpty() ? List.of() :
                infoCardMapper.selectList(new LambdaQueryWrapper<PartInfoExchangeCardDO>()
                        .in(PartInfoExchangeCardDO::getPartId, allPartIds)
                        .orderByAsc(PartInfoExchangeCardDO::getSort));
        List<Long> cardIds = allCards.stream().map(PartInfoExchangeCardDO::getId).toList();
        List<PartInfoExchangeQaDO> allQas = cardIds.isEmpty() ? List.of() :
                infoQaMapper.selectList(new LambdaQueryWrapper<PartInfoExchangeQaDO>()
                        .in(PartInfoExchangeQaDO::getCardId, cardIds)
                        .orderByAsc(PartInfoExchangeQaDO::getSort));

        List<PartTellStoryFrameDO> allFrames = allPartIds.isEmpty() ? List.of() :
                storyFrameMapper.selectList(new LambdaQueryWrapper<PartTellStoryFrameDO>()
                        .in(PartTellStoryFrameDO::getPartId, allPartIds)
                        .orderByAsc(PartTellStoryFrameDO::getSort));

        List<PartPersonalQuestionDO> allPQs = allPartIds.isEmpty() ? List.of() :
                personalQMapper.selectList(new LambdaQueryWrapper<PartPersonalQuestionDO>()
                        .in(PartPersonalQuestionDO::getPartId, allPartIds)
                        .orderByAsc(PartPersonalQuestionDO::getSort));
        List<Long> pqIds = allPQs.stream().map(PartPersonalQuestionDO::getId).toList();
        List<PartPersonalQaSampleDO> allSamples = pqIds.isEmpty() ? List.of() :
                personalSampleMapper.selectList(new LambdaQueryWrapper<PartPersonalQaSampleDO>()
                        .in(PartPersonalQaSampleDO::getQuestionId, pqIds)
                        .orderByAsc(PartPersonalQaSampleDO::getSort));

        // 索引
        Map<Long, List<PartFindDiffPairDO>> pairsByPart = allPairs.stream()
                .collect(Collectors.groupingBy(PartFindDiffPairDO::getPartId));
        Map<Long, List<PartFindDiffDifferenceDO>> diffsByPair = allDiffs.stream()
                .collect(Collectors.groupingBy(PartFindDiffDifferenceDO::getPairId));
        Map<Long, List<PartInfoExchangeCardDO>> cardsByPart = allCards.stream()
                .collect(Collectors.groupingBy(PartInfoExchangeCardDO::getPartId));
        Map<Long, List<PartInfoExchangeQaDO>> qasByCard = allQas.stream()
                .collect(Collectors.groupingBy(PartInfoExchangeQaDO::getCardId));
        Map<Long, List<PartTellStoryFrameDO>> framesByPart = allFrames.stream()
                .collect(Collectors.groupingBy(PartTellStoryFrameDO::getPartId));
        Map<Long, List<PartPersonalQuestionDO>> pqsByPart = allPQs.stream()
                .collect(Collectors.groupingBy(PartPersonalQuestionDO::getPartId));
        Map<Long, List<PartPersonalQaSampleDO>> samplesByQ = allSamples.stream()
                .collect(Collectors.groupingBy(PartPersonalQaSampleDO::getQuestionId));

        // 4. 组装：优先使用 content_json，为空时 fallback 到多表拼装
        Map<String, Object> tests = new LinkedHashMap<>();
        for (ExamDO exam : exams) {
            // 优先使用 content_json
            if (exam.getContentJson() != null && !exam.getContentJson().isBlank()) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> cached = objectMapper.readValue(exam.getContentJson(), LinkedHashMap.class);
                    tests.put(exam.getExamCode(), cached);
                    continue;
                } catch (Exception ignored) {
                    // JSON 解析失败，fallback 到多表拼装
                }
            }

            Map<String, Object> testMap = new LinkedHashMap<>();
            testMap.put("label", exam.getLabel());

            List<ExamPartDO> parts = partsByExam.getOrDefault(exam.getId(), List.of());
            for (ExamPartDO part : parts) {
                String partKey = "part" + part.getPartNo();
                Map<String, Object> partMap = new LinkedHashMap<>();
                partMap.put("title", part.getTitle());
                partMap.put("intro", part.getIntro());

                switch (part.getPartType()) {
                    case "find_diff" -> buildPart1(partMap, part.getId(), pairsByPart, diffsByPair);
                    case "info_exchange" -> buildPart2(partMap, part.getId(), cardsByPart, qasByCard);
                    case "tell_story" -> buildPart3(partMap, part.getId(), framesByPart);
                    case "personal_qa" -> buildPart4(partMap, part.getId(), pqsByPart, samplesByQ);
                }

                testMap.put(partKey, partMap);
            }
            tests.put(exam.getExamCode(), testMap);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        // 热身问题（Flyers 考试统一的 warmup）
        result.put("warmup", List.of(
                Map.of("examiner", "Hello. My name's Anna. What's your name?"),
                Map.of("examiner", "What's your surname?"),
                Map.of("examiner", "How old are you?")
        ));
        result.put("tests", tests);
        return success(result);
    }

    // ---- Part 1: Find the Differences ----
    private void buildPart1(Map<String, Object> partMap, Long partId,
                            Map<Long, List<PartFindDiffPairDO>> pairsByPart,
                            Map<Long, List<PartFindDiffDifferenceDO>> diffsByPair) {
        List<PartFindDiffPairDO> pairs = pairsByPart.getOrDefault(partId, List.of());
        if (!pairs.isEmpty()) {
            partMap.put("scene", pairs.get(0).getTopic());
        }
        List<Map<String, String>> questions = new ArrayList<>();
        for (PartFindDiffPairDO pair : pairs) {
            List<PartFindDiffDifferenceDO> diffs = diffsByPair.getOrDefault(pair.getId(), List.of());
            for (PartFindDiffDifferenceDO diff : diffs) {
                Map<String, String> q = new LinkedHashMap<>();
                // DB: keyword=examiner句, description=expected句
                q.put("examiner", diff.getKeyword());
                q.put("expected", diff.getDescription());
                questions.add(q);
            }
        }
        partMap.put("questions", questions);
    }

    // ---- Part 2: Information Exchange ----
    private void buildPart2(Map<String, Object> partMap, Long partId,
                            Map<Long, List<PartInfoExchangeCardDO>> cardsByPart,
                            Map<Long, List<PartInfoExchangeQaDO>> qasByCard) {
        List<PartInfoExchangeCardDO> cards = cardsByPart.getOrDefault(partId, List.of());
        for (PartInfoExchangeCardDO card : cards) {
            List<PartInfoExchangeQaDO> qas = qasByCard.getOrDefault(card.getId(), List.of());
            if ("A".equals(card.getPhase())) {
                partMap.put("context", card.getCardTitle());
                List<Map<String, String>> phaseA = new ArrayList<>();
                for (PartInfoExchangeQaDO qa : qas) {
                    Map<String, String> q = new LinkedHashMap<>();
                    q.put("examiner", qa.getQuestionText());
                    q.put("expected", qa.getAnswerText());
                    phaseA.add(q);
                }
                partMap.put("phaseA", phaseA);
            } else if ("B".equals(card.getPhase())) {
                partMap.put("transition", card.getIntro());
                List<Map<String, String>> phaseB = new ArrayList<>();
                for (PartInfoExchangeQaDO qa : qas) {
                    Map<String, String> q = new LinkedHashMap<>();
                    if (qa.getPromptLabel() != null) {
                        q.put("hint", qa.getPromptLabel());
                    }
                    q.put("expected_question", qa.getQuestionText());
                    q.put("answer", qa.getAnswerText());
                    phaseB.add(q);
                }
                partMap.put("phaseB", phaseB);
            }
        }
    }

    // ---- Part 3: Tell the Story ----
    private void buildPart3(Map<String, Object> partMap, Long partId,
                            Map<Long, List<PartTellStoryFrameDO>> framesByPart) {
        List<PartTellStoryFrameDO> frames = framesByPart.getOrDefault(partId, List.of());

        // frameNo=1 是考官示范
        Optional<PartTellStoryFrameDO> frame1 = frames.stream()
                .filter(f -> f.getFrameNo() != null && f.getFrameNo() == 1).findFirst();
        if (frame1.isPresent()) {
            partMap.put("pic1", frame1.get().getSentenceText());
        }
        partMap.put("instruction", "Now you tell the story.");

        // frameNo >= 2 是学生作答
        List<Map<String, Object>> pictures = new ArrayList<>();
        Map<Integer, List<PartTellStoryFrameDO>> byFrame = frames.stream()
                .filter(f -> f.getFrameNo() != null && f.getFrameNo() >= 2)
                .collect(Collectors.groupingBy(PartTellStoryFrameDO::getFrameNo,
                        LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<Integer, List<PartTellStoryFrameDO>> entry : byFrame.entrySet()) {
            int frameNo = entry.getKey();
            List<PartTellStoryFrameDO> frameRows = entry.getValue();
            // 只有一行（init SQL 合并了多句），需要拆分
            PartTellStoryFrameDO row = frameRows.get(0);
            Map<String, Object> pic = new LinkedHashMap<>();
            pic.put("pic", frameNo);

            String hintRaw = row.getHint() != null ? row.getHint() : "";
            String[] hintParts = hintRaw.split("\\s*\\|\\s*");
            pic.put("prompt", hintParts[0]);

            // 按 hint 个数-1 拆分句子
            String sentenceRaw = row.getSentenceText() != null ? row.getSentenceText() : "";
            int expectedCount = Math.max(hintParts.length - 1, 1);
            List<String> sentenceTexts = splitSentences(sentenceRaw, expectedCount);

            List<Map<String, String>> sentences = new ArrayList<>();
            for (int i = 0; i < sentenceTexts.size(); i++) {
                Map<String, String> s = new LinkedHashMap<>();
                s.put("text", sentenceTexts.get(i));
                if (i + 1 < hintParts.length) {
                    s.put("hint", hintParts[i + 1]);
                } else {
                    s.put("hint", hintParts[0]);
                }
                sentences.add(s);
            }
            pic.put("sentences", sentences);
            pictures.add(pic);
        }
        partMap.put("pictures", pictures);
    }

    /** 将合并的多句文本拆分为指定数量的句子 */
    private List<String> splitSentences(String text, int expected) {
        if (expected <= 1) {
            return List.of(text.trim());
        }
        // 按句号+空格拆
        String[] parts = text.split("(?<=\\.)\\s+");
        if (parts.length == expected) {
            return Arrays.stream(parts).map(String::trim).toList();
        }
        // 拆出来的数量不匹配时，尽量均分
        if (parts.length > expected) {
            List<String> result = new ArrayList<>();
            int perGroup = parts.length / expected;
            int remainder = parts.length % expected;
            int idx = 0;
            for (int g = 0; g < expected; g++) {
                int count = perGroup + (g < remainder ? 1 : 0);
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < count && idx < parts.length; j++, idx++) {
                    if (!sb.isEmpty()) sb.append(" ");
                    sb.append(parts[idx].trim());
                }
                result.add(sb.toString());
            }
            return result;
        }
        // parts.length < expected：无法拆更多，按实际返回
        return Arrays.stream(parts).map(String::trim).toList();
    }

    // ---- Part 4: Personal Questions ----
    private void buildPart4(Map<String, Object> partMap, Long partId,
                            Map<Long, List<PartPersonalQuestionDO>> pqsByPart,
                            Map<Long, List<PartPersonalQaSampleDO>> samplesByQ) {
        List<PartPersonalQuestionDO> pqs = pqsByPart.getOrDefault(partId, List.of());

        List<Map<String, String>> questions = new ArrayList<>();
        List<Map<String, String>> followups = new ArrayList<>();

        for (PartPersonalQuestionDO pq : pqs) {
            Map<String, String> q = new LinkedHashMap<>();
            q.put("examiner", pq.getQuestionText());
            // 取第一个 sample
            List<PartPersonalQaSampleDO> samples = samplesByQ.getOrDefault(pq.getId(), List.of());
            if (!samples.isEmpty()) {
                q.put("sample", samples.get(0).getSampleText());
            }
            if (pq.getQNo() != null && pq.getQNo() <= 5) {
                questions.add(q);
            } else {
                followups.add(q);
            }
        }

        partMap.put("questions", questions);
        if (!followups.isEmpty()) {
            partMap.put("followups", followups);
        }
    }

}
