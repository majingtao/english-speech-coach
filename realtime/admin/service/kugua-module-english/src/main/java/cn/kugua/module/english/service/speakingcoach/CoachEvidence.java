package cn.kugua.module.english.service.speakingcoach;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.util.*;

/** Pure, conservative evidence aggregation; missing evidence is never a failure. */
public final class CoachEvidence {
    private CoachEvidence() {}
    public static final Map<String, String> LABELS = new LinkedHashMap<>();
    static {
        LABELS.put("relevance", "回答切题"); LABELS.put("reason", "补充理由");
        LABELS.put("detail", "展开细节"); LABELS.put("question", "主动提问");
        LABELS.put("response", "回应搭档"); LABELS.put("past", "讲述经历");
    }
    public record Observation(String task, String stage, boolean assisted, LocalDate date, JsonNode feedback) {}
    public record Skill(String code, String label, String status, int observed, int successes,
                        int failures, String nextReviewAt) {}
    public static List<Skill> summarize(List<Observation> history, LocalDate today) {
        List<Skill> result = new ArrayList<>();
        for (String code : LABELS.keySet()) {
            int observed = 0, successes = 0, failures = 0;
            Set<LocalDate> days = new HashSet<>(); Set<String> tasks = new HashSet<>();
            Set<String> counted = new HashSet<>(); LocalDate last = null;
            for (Observation row : history) {
                if (row.assisted || "retry".equals(row.stage)) continue;
                // One task/day contributes once, regardless of repeated sessions.
                for (JsonNode skill : row.feedback.path("skills")) {
                    if (!code.equals(skill.path("code").asText()) || !skill.path("observed").asBoolean()) continue;
                    if (!counted.add(row.task + ":" + row.date)) continue;
                    observed++; last = row.date;
                    if (skill.path("met").asBoolean()) {
                        successes++; days.add(row.date); tasks.add(row.task);
                    } else {
                        failures++; days.clear(); tasks.clear();
                    }
                }
            }
            boolean mastered = days.size() >= 2 && tasks.size() >= 3;
            String status = observed == 0 ? "unobserved" : mastered ? "stable" : failures >= 2 ? "practice" : "developing";
            result.add(new Skill(code, LABELS.get(code), status, observed, successes, failures,
                    last == null ? null : last.plusDays(mastered ? 7 : 1).toString()));
        }
        return result;
    }
}
