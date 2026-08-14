package cn.kugua.module.english.service.grammar;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.kugua.module.english.controller.admin.grammar.vo.*;
import cn.kugua.module.english.controller.app.grammar.vo.*;
import cn.kugua.module.english.dal.dataobject.grammar.*;
import cn.kugua.module.english.dal.mysql.grammar.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class GrammarServiceImpl implements GrammarService {
    @Resource private GrammarPointMapper grammarPointMapper;
    @Resource private GrammarQuestionMapper grammarQuestionMapper;
    @Resource private GrammarGenerationJobMapper grammarGenerationJobMapper;
    @Resource private GrammarAttemptMapper grammarAttemptMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<GrammarPointDO> getPoints(String level, boolean onlyEnabled) {
        return onlyEnabled ? grammarPointMapper.selectEnabled(level) : grammarPointMapper.selectByLevel(level);
    }
    public PageResult<GrammarQuestionDO> getQuestionPage(GrammarQuestionPageReqVO req) {
        return grammarQuestionMapper.selectPage(req, req.getGrammarPointId(), req.getDifficulty(), req.getQuestionType(), req.getStatus());
    }
    public GrammarQuestionDO getQuestion(Long id) { return requireQuestion(id); }
    public Long createQuestion(GrammarQuestionSaveReqVO req) {
        GrammarQuestionDO question = prepare(req);
        grammarQuestionMapper.insert(question);
        return question.getId();
    }
    public void updateQuestion(GrammarQuestionSaveReqVO req) {
        GrammarQuestionDO existing = requireQuestion(req.getId());
        GrammarQuestionDO question = prepare(req);
        if (req.getSource() == null) question.setSource(existing.getSource());
        if (req.getValidationStatus() == null) question.setValidationStatus(existing.getValidationStatus());
        if (req.getGenerationJobId() == null) question.setGenerationJobId(existing.getGenerationJobId());
        if (req.getSourceQuestionId() == null) question.setSourceQuestionId(existing.getSourceQuestionId());
        grammarQuestionMapper.updateById(question);
    }
    public void deleteQuestion(Long id) { requireQuestion(id); grammarQuestionMapper.deleteById(id); }

    @Transactional
    public GrammarGenerationJobDO importGenerated(GrammarBatchImportReqVO req) {
        if (grammarPointMapper.selectById(req.getGrammarPointId()) == null) throw new IllegalArgumentException("语法知识点不存在");
        GrammarGenerationJobDO job = new GrammarGenerationJobDO();
        job.setGrammarPointId(req.getGrammarPointId());
        job.setRequestedCount(req.getQuestions().size());
        job.setAcceptedCount(0);
        job.setDifficulty(req.getDifficulty());
        job.setQuestionTypes(req.getQuestionTypes());
        job.setAutoPublish(!Boolean.FALSE.equals(req.getAutoPublish()));
        job.setModel(req.getModel() == null ? "" : req.getModel());
        job.setPromptVersion("grammar-v1");
        job.setSettingsJson(req.getSettingsJson());
        job.setStatus(0);
        job.setErrorMessage("");
        grammarGenerationJobMapper.insert(job);
        int accepted = 0;
        for (GrammarQuestionSaveReqVO questionReq : req.getQuestions()) {
            questionReq.setGrammarPointId(req.getGrammarPointId());
            questionReq.setGenerationJobId(job.getId());
            questionReq.setDifficulty(req.getDifficulty());
            questionReq.setStatus(job.getAutoPublish() ? 1 : 0);
            questionReq.setSource("ai");
            questionReq.setValidationStatus(1);
            createQuestion(questionReq);
            accepted++;
        }
        job.setAcceptedCount(accepted);
        job.setStatus(1);
        grammarGenerationJobMapper.updateById(job);
        return job;
    }

    public List<AppGrammarQuestionRespVO> getPractice(Long pointId, Integer difficulty, Integer count) {
        if (grammarPointMapper.selectById(pointId) == null) throw new IllegalArgumentException("语法知识点不存在");
        return BeanUtils.toBean(grammarQuestionMapper.selectPractice(pointId, difficulty == null ? 1 : difficulty,
                count == null ? 10 : count), AppGrammarQuestionRespVO.class);
    }

    @Transactional
    public AppGrammarAnswerRespVO answer(Long userId, Long questionId, AppGrammarAnswerReqVO req) {
        GrammarQuestionDO question = requireQuestion(questionId);
        if (!Integer.valueOf(1).equals(question.getStatus())) throw new IllegalArgumentException("题目未发布");
        List<String> accepted = parseStringList(question.getAnswerJson());
        String submitted = normalize(req.getAnswer());
        boolean correct = accepted.stream().map(this::normalize).anyMatch(submitted::equals);
        GrammarAttemptDO attempt = new GrammarAttemptDO();
        attempt.setUserId(userId);
        attempt.setQuestionId(questionId);
        attempt.setGrammarPointId(question.getGrammarPointId());
        attempt.setDifficulty(question.getDifficulty());
        attempt.setAnswerText(req.getAnswer());
        attempt.setCorrect(correct);
        attempt.setDurationSeconds(req.getDurationSeconds());
        grammarAttemptMapper.insert(attempt);
        AppGrammarAnswerRespVO resp = new AppGrammarAnswerRespVO();
        resp.setCorrect(correct);
        resp.setAcceptedAnswers(accepted);
        resp.setExplanationZh(question.getExplanationZh());
        resp.setRuleText(question.getRuleText());
        resp.setErrorHint(correct ? "" : parseErrorHint(question.getErrorTagsJson(), req.getAnswer()));
        return resp;
    }

    private GrammarQuestionDO prepare(GrammarQuestionSaveReqVO req) {
        if (grammarPointMapper.selectById(req.getGrammarPointId()) == null) throw new IllegalArgumentException("语法知识点不存在");
        validateQuestion(req);
        GrammarQuestionDO q = BeanUtils.toBean(req, GrammarQuestionDO.class);
        if (q.getInstruction() == null) q.setInstruction("");
        if (q.getOptionsJson() == null) q.setOptionsJson("[]");
        if (q.getRuleText() == null) q.setRuleText("");
        if (q.getErrorTagsJson() == null) q.setErrorTagsJson("{}");
        if (q.getMediaJson() == null) q.setMediaJson("null");
        if (q.getSource() == null) q.setSource("manual");
        if (q.getValidationStatus() == null) q.setValidationStatus(1);
        if (q.getSort() == null) q.setSort(0);
        if (q.getStatus() == null) q.setStatus(0);
        return q;
    }

    private void validateQuestion(GrammarQuestionSaveReqVO req) {
        if (!Set.of("single_choice", "text_input").contains(req.getQuestionType())) throw new IllegalArgumentException("不支持的题型");
        if (req.getDifficulty() == null || req.getDifficulty() < 1 || req.getDifficulty() > 4) throw new IllegalArgumentException("难度必须为 L1-L4");
        List<String> answers = parseStringList(req.getAnswerJson());
        if (answers.isEmpty()) throw new IllegalArgumentException("至少需要一个答案");
        if ("single_choice".equals(req.getQuestionType())) {
            List<String> options = parseStringList(req.getOptionsJson());
            if (options.size() < 2 || new HashSet<>(options).size() != options.size()) throw new IllegalArgumentException("单选题选项必须至少两个且不能重复");
            if (answers.size() != 1 || !options.contains(answers.get(0))) throw new IllegalArgumentException("单选题必须有且只有一个存在于选项中的答案");
        }
        parseJson(req.getErrorTagsJson(), JsonNode.class);
        parseJson(req.getMediaJson(), JsonNode.class);
    }

    private List<String> parseStringList(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node.isTextual()) return List.of(node.asText());
            return objectMapper.convertValue(node, new TypeReference<List<String>>() {});
        } catch (Exception e) { throw new IllegalArgumentException("答案或选项不是有效 JSON 数组", e); }
    }
    private <T> T parseJson(String json, Class<T> type) {
        if (json == null || json.isBlank()) return null;
        try { return objectMapper.readValue(json, type); }
        catch (Exception e) { throw new IllegalArgumentException("题目 JSON 配置无效", e); }
    }
    private String parseErrorHint(String json, String answer) {
        if (json == null || json.isBlank()) return "再看看题目中的时间词和主语。";
        try {
            JsonNode node = objectMapper.readTree(json);
            return node.path(answer).asText("再看看题目中的时间词和主语。");
        } catch (Exception ignored) { return "再看看题目中的时间词和主语。"; }
    }
    private String normalize(String value) { return value == null ? "" : value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT); }
    private GrammarQuestionDO requireQuestion(Long id) {
        GrammarQuestionDO q = id == null ? null : grammarQuestionMapper.selectById(id);
        if (q == null) throw new IllegalArgumentException("语法题目不存在");
        return q;
    }
}
