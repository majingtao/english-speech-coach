package cn.kugua.module.english.service.speakingcoach;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

@Service
public class SpeakingCoachService {
    @Resource private JdbcTemplate jdbc;
    @Resource private TransactionTemplate transactions;
    @Value("${english.coach.python-url:http://127.0.0.1:8444}") private String pythonUrl;
    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private long tenant() { return TenantContextHolder.getRequiredTenantId(); }
    private LocalDate today() { return LocalDate.now(ZONE); }
    private ResponseStatusException bad(String text) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, text); }
    private JsonNode parse(Object text) {
        try { return json.readTree(String.valueOf(text)); } catch (Exception e) { throw new IllegalStateException("Invalid coach JSON", e); }
    }
    private String encode(Object value) {
        try { return json.writeValueAsString(value); } catch (Exception e) { throw new IllegalStateException(e); }
    }
    private List<String> strings(JsonNode array) {
        List<String> result = new ArrayList<>(); array.forEach(n -> result.add(n.asText())); return result;
    }
    private Map<String,Object> owned(String table, String id, long user) {
        // Table identifiers are constants from this class, never request input.
        List<Map<String,Object>> rows = jdbc.queryForList("SELECT * FROM " + table + " WHERE id=? AND user_id=? AND tenant_id=?", id, user, tenant());
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "练习记录不存在");
        return rows.get(0);
    }
    public List<JsonNode> tasks() {
        return jdbc.query("SELECT payload_json FROM esc_coach_task WHERE status=1 AND tenant_id IN (0,?) ORDER BY id",
                (rs, n) -> parse(rs.getString(1)), tenant());
    }
    private JsonNode task(String id) {
        return tasks().stream().filter(t -> id.equals(t.path("id").asText())).findFirst().orElseThrow(() -> bad("题目不存在或未发布"));
    }
    private JsonNode publicTask(JsonNode task, boolean hints) {
        ObjectNode out = task.deepCopy(); if (!hints) { out.remove("hint"); out.remove("sample"); } return out;
    }
    public List<CoachEvidence.Skill> skills(long user) {
        List<CoachEvidence.Observation> history = jdbc.query(
            "SELECT a.* FROM esc_coach_attempt a JOIN esc_coach_session s ON s.id=a.session_id AND s.user_id=a.user_id AND s.tenant_id=a.tenant_id " +
            "WHERE a.user_id=? AND a.tenant_id=? AND a.status='graded' AND (s.mode='practice' OR s.status='finished') ORDER BY a.created_at,a.id",
            (rs,n) -> new CoachEvidence.Observation(rs.getString("task_id"), rs.getString("stage"), rs.getBoolean("assisted"),
                rs.getTimestamp("created_at").toLocalDateTime().toLocalDate(), parse(rs.getString("feedback_json"))), user, tenant());
        return CoachEvidence.summarize(history, today());
    }
    public ObjectNode dashboard(long user, int minutes) {
        ObjectNode out = json.createObjectNode(); out.set("skills", json.valueToTree(skills(user)));
        out.set("plan", plan(user, minutes));
        out.set("sessions", json.valueToTree(jdbc.queryForList(
            "SELECT id,mode,status,created_at,deadline_at FROM esc_coach_session WHERE user_id=? AND tenant_id=? ORDER BY created_at DESC LIMIT 15", user, tenant())));
        out.set("tasks", json.valueToTree(tasks().stream().map(t -> publicTask(t, false)).toList()));
        return out;
    }
    public ObjectNode plan(long user, int minutes) {
        minutes = Math.max(5, Math.min(30, minutes));
        List<Map<String,Object>> rows = jdbc.queryForList("SELECT * FROM esc_coach_plan WHERE user_id=? AND tenant_id=? AND plan_date=?", user, tenant(), today());
        if (rows.isEmpty()) {
            List<CoachEvidence.Skill> skills = new ArrayList<>(skills(user));
            skills.sort(Comparator.comparingInt(s -> s.nextReviewAt() != null && !LocalDate.parse(s.nextReviewAt()).isAfter(today()) ? 0 : "practice".equals(s.status()) ? 1 : "unobserved".equals(s.status()) ? 2 : 3));
            Set<String> seen = new HashSet<>(jdbc.queryForList("SELECT DISTINCT task_id FROM esc_coach_attempt WHERE user_id=? AND tenant_id=?", String.class, user, tenant()));
            List<JsonNode> bank = tasks(); ArrayNode items = json.createArrayNode(); Set<String> selected = new HashSet<>(); int used = 0;
            for (CoachEvidence.Skill skill : skills) {
                List<JsonNode> choices = bank.stream().filter(t -> t.path("focus").asText().equals(skill.code())).sorted(Comparator.comparing(t -> seen.contains(t.path("id").asText()))).toList();
                for (JsonNode t : choices) {
                    int seconds = t.path("seconds").asInt(120); String id = t.path("id").asText();
                    if (selected.contains(id) || used + seconds > minutes * 60) continue;
                    String reason = "unobserved".equals(skill.status()) ? "先了解你的" + skill.label() : skill.label() + "：复习后用新情境验证";
                    items.addObject().put("taskId", id).put("title", t.path("title").asText()).put("seconds", seconds).put("reason", reason);
                    used += seconds; selected.add(id); break;
                }
                if (items.size() >= 4) break;
            }
            if (items.isEmpty()) throw bad("尚无可用练习，请先导入口语题库");
            try { jdbc.update("INSERT INTO esc_coach_plan(id,user_id,tenant_id,plan_date,minutes,items_json) VALUES(?,?,?,?,?,?)",
                    UUID.randomUUID().toString(), user, tenant(), today(), minutes, encode(items)); }
            catch (DuplicateKeyException ignored) { /* another tab already created today's plan */ }
            rows = jdbc.queryForList("SELECT * FROM esc_coach_plan WHERE user_id=? AND tenant_id=? AND plan_date=?", user, tenant(), today());
        }
        Map<String,Object> row = rows.get(0); ObjectNode out = json.createObjectNode();
        out.put("id", row.get("id").toString()).put("date", today().toString()).put("minutes", ((Number)row.get("minutes")).intValue());
        ArrayNode items = (ArrayNode)parse(row.get("items_json"));
        for (JsonNode item : items) {
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM esc_coach_session s WHERE s.user_id=? AND s.tenant_id=? AND s.plan_id=? AND s.plan_task_id=? AND s.status='finished' AND EXISTS(SELECT 1 FROM esc_coach_attempt a WHERE a.session_id=s.id AND a.status='graded')",
                Integer.class,user,tenant(),row.get("id"),item.path("taskId").asText());
            ((ObjectNode)item).put("completed", count != null && count > 0);
        }
        out.set("items", items); return out;
    }
    public ObjectNode start(long user, JsonNode input) {
        String mode = input.path("mode").asText(); if (!Set.of("practice", "mock").contains(mode)) throw bad("请选择练习或模考");
        List<String> ids = new ArrayList<>();
        if (mode.equals("mock")) {
            List<JsonNode> bank = tasks(); Collections.shuffle(bank);
            bank.stream().filter(t -> t.path("kind").asText().equals("interview")).limit(3).forEach(t -> ids.add(t.path("id").asText()));
            bank.stream().filter(t -> t.path("kind").asText().equals("dialogue")).limit(1).forEach(t -> ids.add(t.path("id").asText()));
            if (ids.size()!=4) throw bad("模考题库不足");
        } else ids.add(task(input.path("taskId").asText()).path("id").asText());
        String planId = input.path("planId").asText("");
        if (!planId.isBlank()) {
            Map<String,Object> p = owned("esc_coach_plan",planId,user);
            boolean found = false; for (JsonNode item : parse(p.get("items_json"))) if (ids.get(0).equals(item.path("taskId").asText())) found=true;
            if (!found || !mode.equals("practice")) throw bad("任务不属于该计划");
        }
        String id = UUID.randomUUID().toString();
        jdbc.update("INSERT INTO esc_coach_session(id,user_id,tenant_id,mode,task_ids,hint_task_ids,plan_id,plan_task_id,deadline_at) VALUES(?,?,?,?,?,?,?,?,?)",
            id,user,tenant(),mode,encode(ids),"[]",planId.isBlank()?null:planId,planId.isBlank()?null:ids.get(0),
            mode.equals("mock") ? java.sql.Timestamp.valueOf(LocalDateTime.now(ZONE).plusMinutes(10)) : null);
        return session(user,id);
    }
    public ObjectNode session(long user,String id) {
        Map<String,Object> row=owned("esc_coach_session",id,user);
        ObjectNode out=json.createObjectNode(); out.put("id",id).put("mode",row.get("mode").toString()).put("status",row.get("status").toString());
        out.put("deadline",row.get("deadline_at")==null?0:((java.sql.Timestamp)row.get("deadline_at")).toLocalDateTime().atZone(ZONE).toInstant().toEpochMilli());
        out.put("serverTime",System.currentTimeMillis());
        List<String> hinted=strings(parse(row.get("hint_task_ids")));
        ArrayNode bank=out.putArray("tasks"); for(String taskId:strings(parse(row.get("task_ids")))) bank.add(publicTask(task(taskId), row.get("mode").equals("practice") && hinted.contains(taskId)));
        ArrayNode attempts=out.putArray("attempts");
        for(Map<String,Object> a:jdbc.queryForList("SELECT * FROM esc_coach_attempt WHERE session_id=? AND user_id=? AND tenant_id=? ORDER BY created_at,id",id,user,tenant())) attempts.add(attemptView(a));
        return out;
    }
    private ObjectNode attemptView(Map<String,Object> row) {
        ObjectNode out=json.createObjectNode(); for(String key:List.of("id","stage","status")) out.put(key,row.get(key).toString());
        out.put("taskId",row.get("task_id").toString()).put("parentId",row.get("parent_id")==null?"":row.get("parent_id").toString());
        out.put("assisted", ((Number)row.get("assisted")).intValue()!=0).put("error",row.get("error_text").toString());
        if(row.get("feedback_json")!=null) out.set("feedback",parse(row.get("feedback_json")));
        ArrayNode turns=(ArrayNode)parse(row.get("turns_json"));
        for(int i=0;i<turns.size();i++) { ObjectNode t=(ObjectNode)turns.get(i); boolean audio=t.hasNonNull("audioBase64")&&!t.path("audioBase64").asText().isBlank(); t.remove("audioBase64"); t.put("hasAudio",audio); }
        out.set("turns",turns); return out;
    }
    private void active(Map<String,Object> s) {
        if(!s.get("status").equals("active")) throw bad("本次练习已结束");
        if(s.get("deadline_at")!=null && ((java.sql.Timestamp)s.get("deadline_at")).toLocalDateTime().isBefore(LocalDateTime.now(ZONE))) throw bad("模考时间已到，请结束并查看报告");
    }
    public ObjectNode hint(long user,String sessionId,String taskId) {
        return transactions.execute(status -> {
            lockSession(user,sessionId); Map<String,Object> s=owned("esc_coach_session",sessionId,user); active(s);
            if(!s.get("mode").equals("practice") || !strings(parse(s.get("task_ids"))).contains(taskId)) throw bad("当前不能查看提示");
            List<String> hints=strings(parse(s.get("hint_task_ids"))); if(!hints.contains(taskId)) hints.add(taskId);
            jdbc.update("UPDATE esc_coach_session SET hint_task_ids=? WHERE id=? AND user_id=? AND tenant_id=?",encode(hints),sessionId,user,tenant());
            return session(user,sessionId);
        });
    }
    private void lockSession(long user,String id) {
        if(jdbc.queryForList("SELECT id FROM esc_coach_session WHERE id=? AND user_id=? AND tenant_id=? FOR UPDATE",id,user,tenant()).isEmpty()) throw bad("练习不存在");
    }
    public ObjectNode submit(long user,String sessionId,JsonNode input) {
        return transactions.execute(status -> {
            lockSession(user,sessionId); Map<String,Object> s=owned("esc_coach_session",sessionId,user);
            String id=input.path("id").asText(); try { UUID.fromString(id); } catch(Exception e) { throw bad("提交编号无效"); }
            List<Map<String,Object>> existing=jdbc.queryForList("SELECT * FROM esc_coach_attempt WHERE id=? AND user_id=? AND tenant_id=? AND session_id=?",id,user,tenant(),sessionId);
            if(!existing.isEmpty()) return session(user,sessionId); // retrying the same upload is idempotent
            active(s); String taskId=input.path("taskId").asText(); List<String> ids=strings(parse(s.get("task_ids")));
            if(!ids.contains(taskId)) throw bad("题目不属于本次练习");
            JsonNode t=task(taskId); JsonNode turns=input.path("turns");
            if(!turns.isArray() || turns.isEmpty() || turns.size()>16 || encode(turns).length()>11_000_000) throw bad("作答内容无效或录音过长");
            int learners=0; boolean audioOnly=true;
            for(JsonNode turn:turns) {
                if(!Set.of("learner","partner").contains(turn.path("role").asText()) || turn.path("text").asText().isBlank() || turn.path("text").asText().length()>3000) throw bad("对话内容无效");
                if(turn.path("role").asText().equals("learner")) { learners++; if(turn.path("audioBase64").asText().isBlank()) audioOnly=false; }
                if(turn.path("audioBase64").asText().length()>2_600_000) throw bad("单段录音过长");
            }
            if(learners<1 || learners>8) throw bad("请先回答题目");
            if(s.get("mode").equals("mock")&&!audioOnly) throw bad("模考请使用录音作答");
            String parent=input.path("parentId").asText(""); String stage="initial";
            Integer previous=jdbc.queryForObject("SELECT COUNT(*) FROM esc_coach_attempt WHERE session_id=? AND task_id=? AND user_id=? AND tenant_id=?",Integer.class,sessionId,taskId,user,tenant());
            if(!parent.isBlank()) {
                Map<String,Object> p=owned("esc_coach_attempt",parent,user);
                if(!p.get("session_id").equals(sessionId)||!p.get("status").equals("graded")||!s.get("mode").equals("practice")) throw bad("重说关联记录无效");
                stage=p.get("task_id").equals(taskId)?"retry":"transfer";
                if(!task(p.get("task_id").toString()).path("focus").equals(t.path("focus"))) throw bad("换题须保持相同训练目标");
                if(stage.equals("transfer") && previous!=null && previous>0) throw bad("请换一道未作答的新题");
                if(stage.equals("retry") && previous!=null && previous>=3) throw bad("本题已练习三次，休息一下或换题验证");
            } else if(previous!=null && previous>0) throw bad("本题已提交，请使用重说功能");
            boolean assisted=stage.equals("retry")||strings(parse(s.get("hint_task_ids"))).contains(taskId)||!audioOnly;
            // Prior exposure to a sample/feedback on the same task cannot prove unseen mastery.
            Integer exposed=jdbc.queryForObject("SELECT COUNT(*) FROM esc_coach_attempt WHERE user_id=? AND tenant_id=? AND task_id=? AND status='graded'",Integer.class,user,tenant(),taskId);
            assisted |= exposed!=null&&exposed>0;
            jdbc.update("INSERT INTO esc_coach_attempt(id,user_id,tenant_id,session_id,task_id,stage,parent_id,assisted,turns_json) VALUES(?,?,?,?,?,?,?,?,?)",
                id,user,tenant(),sessionId,taskId,stage,parent.isBlank()?null:parent,assisted?1:0,encode(turns));
            return session(user,sessionId);
        });
    }
    public ObjectNode transfer(long user,String id,String parentId) {
        return transactions.execute(status -> {
            lockSession(user,id); Map<String,Object> s=owned("esc_coach_session",id,user); active(s);
            Map<String,Object> parent=owned("esc_coach_attempt",parentId,user);
            if(!s.get("mode").equals("practice") || !parent.get("session_id").equals(id)||!parent.get("status").equals("graded")) throw bad("请先完成当前题的反馈");
            List<String> ids=strings(parse(s.get("task_ids"))); if(ids.size()>=4) throw bad("本组练习已完成，请结束并查看报告");
            Set<String> seen=new HashSet<>(jdbc.queryForList("SELECT DISTINCT task_id FROM esc_coach_attempt WHERE user_id=? AND tenant_id=?",String.class,user,tenant())); seen.addAll(ids);
            String focus=task(parent.get("task_id").toString()).path("focus").asText();
            JsonNode next=tasks().stream().filter(t -> t.path("focus").asText().equals(focus)&&!seen.contains(t.path("id").asText())).findFirst().orElseThrow(() -> bad("该目标暂无未见过的新题，可明天继续复习"));
            ids.add(next.path("id").asText()); jdbc.update("UPDATE esc_coach_session SET task_ids=? WHERE id=? AND user_id=? AND tenant_id=?",encode(ids),id,user,tenant());
            return session(user,id);
        });
    }
    public ObjectNode finish(long user,String id) {
        owned("esc_coach_session",id,user);
        jdbc.update("UPDATE esc_coach_session SET status='finished',ended_at=CURRENT_TIMESTAMP WHERE id=? AND user_id=? AND tenant_id=? AND status='active'",id,user,tenant());
        return session(user,id);
    }
    private JsonNode python(String action,JsonNode payload,String auth) {
        try {
            HttpRequest request=HttpRequest.newBuilder(URI.create(pythonUrl+"/py/coach/"+action)).timeout(Duration.ofSeconds(250))
                .header("Authorization",auth).header("tenant-id",Long.toString(tenant())).header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(encode(payload),StandardCharsets.UTF_8)).build();
            HttpResponse<String> res=http.send(request,HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if(res.statusCode()!=200) throw bad(res.statusCode()==403?"AI 额度或登录校验未通过":"AI 服务暂不可用，作答已保留，可重试");
            return parse(res.body());
        } catch(InterruptedException e) { Thread.currentThread().interrupt(); throw bad("请求已中断，可重试"); }
        catch(ResponseStatusException e) { throw e; }
        catch(Exception e) { throw bad("暂时无法连接评分服务，作答已保留"); }
    }
    public ObjectNode grade(long user,String attemptId,String auth) {
        Map<String,Object> a=owned("esc_coach_attempt",attemptId,user); String sessionId=a.get("session_id").toString();
        Map<String,Object> s=owned("esc_coach_session",sessionId,user);
        if(s.get("mode").equals("mock")&&!s.get("status").equals("finished")) throw bad("模考结束后才能查看反馈");
        if(a.get("status").equals("graded")) return session(user,sessionId);
        int claimed=jdbc.update("UPDATE esc_coach_attempt SET status='grading',grade_started=CURRENT_TIMESTAMP WHERE id=? AND user_id=? AND tenant_id=? AND (status IN ('pending','failed') OR (status='grading' AND grade_started < DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE)))",attemptId,user,tenant());
        if(claimed==0) throw bad("正在评分，请稍后刷新");
        try {
            ObjectNode body=json.createObjectNode(); body.set("task",task(a.get("task_id").toString())); body.set("turns",parse(a.get("turns_json")));
            JsonNode feedback=python("grade",body,auth);
            if(!feedback.path("skills").isArray()) throw bad("评分结果不完整，请重试");
            jdbc.update("UPDATE esc_coach_attempt SET status='graded',feedback_json=?,error_text='' WHERE id=? AND user_id=? AND tenant_id=?",encode(feedback),attemptId,user,tenant());
        } catch(Exception e) {
            jdbc.update("UPDATE esc_coach_attempt SET status='failed',error_text=? WHERE id=? AND user_id=? AND tenant_id=?","评分未完成，原始作答已保存，请重试",attemptId,user,tenant());
        }
        return session(user,sessionId);
    }
    public JsonNode partner(long user,String id,JsonNode input,String auth) {
        Map<String,Object> s=owned("esc_coach_session",id,user); active(s); String taskId=input.path("taskId").asText();
        if(!strings(parse(s.get("task_ids"))).contains(taskId)) throw bad("题目不属于该练习");
        JsonNode turns=input.path("turns"); if(!turns.isArray()||turns.size()>15||encode(turns).length()>30000) throw bad("对话内容过长");
        ObjectNode body=json.createObjectNode(); body.set("task",task(taskId)); body.set("turns",turns); return python("partner",body,auth);
    }
    public byte[] audio(long user,String attemptId,int turnIndex) {
        JsonNode turns=parse(owned("esc_coach_attempt",attemptId,user).get("turns_json"));
        if(turnIndex<0||turnIndex>=turns.size()) throw bad("录音不存在");
        try { return Base64.getDecoder().decode(turns.get(turnIndex).path("audioBase64").asText()); } catch(Exception e) { throw bad("录音无效"); }
    }
}
