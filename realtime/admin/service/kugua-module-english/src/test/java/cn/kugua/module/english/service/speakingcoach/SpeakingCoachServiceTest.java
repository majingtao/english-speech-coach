package cn.kugua.module.english.service.speakingcoach;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import org.junit.jupiter.api.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.*;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class SpeakingCoachServiceTest {
    SpeakingCoachService service; JdbcTemplate jdbc; ObjectMapper json=new ObjectMapper();
    @BeforeEach void setup() {
        TenantContextHolder.setTenantId(1L);
        var ds=new DriverManagerDataSource("jdbc:h2:mem:coach"+UUID.randomUUID()+";MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE","sa","");
        new ResourceDatabasePopulator(new ClassPathResource("db/migration/V1_0_30__speaking_coach.sql"),new ClassPathResource("db/migration/V1_0_31__seed_coach_tasks.sql")).execute(ds);
        jdbc=new JdbcTemplate(ds); service=new SpeakingCoachService();
        ReflectionTestUtils.setField(service,"jdbc",jdbc); ReflectionTestUtils.setField(service,"transactions",new TransactionTemplate(new DataSourceTransactionManager(ds)));
        ReflectionTestUtils.setField(service,"pythonUrl","http://127.0.0.1:1");
    }
    @AfterEach void clear() { TenantContextHolder.clear(); }
    ObjectNode start(String mode) { return service.start(7,json.createObjectNode().put("mode",mode).put("taskId","ket-reason-1")); }
    ObjectNode answer(String task) {
        var body=json.createObjectNode().put("id",UUID.randomUUID().toString()).put("taskId",task);
        body.putArray("turns").addObject().put("role","learner").put("text","I like swimming.").put("asrText","I like swimming."); return body;
    }
    @Test void planIsPersistentAndTenantSpecific() {
        var first=service.plan(7,12); assertEquals(first,service.plan(7,20));
        TenantContextHolder.setTenantId(2L); assertNotEquals(first.path("id"),service.plan(7,12).path("id"));
        assertEquals(36,service.tasks().size());
    }
    @Test void cannotReadAnotherChildOrTenant() {
        var s=start("practice"); String id=s.path("id").asText();
        assertThrows(ResponseStatusException.class,()->service.session(8,id));
        TenantContextHolder.setTenantId(2L); assertThrows(ResponseStatusException.class,()->service.session(7,id));
    }
    @Test void uploadIsIdempotentAndScoresAreNotTakenFromBrowser() {
        String id=start("practice").path("id").asText(); var body=answer("ket-reason-1");
        body.putObject("feedback").put("score",100);
        service.submit(7,id,body); var s=service.submit(7,id,body);
        assertEquals(1,s.path("attempts").size()); assertFalse(s.path("attempts").get(0).has("feedback"));
        assertTrue(s.path("attempts").get(0).path("assisted").asBoolean()); // text-only
    }
    @Test void mockCannotRevealHintsOrEarlyGrades() {
        var s=start("mock"); String id=s.path("id").asText(); String task=s.path("tasks").get(0).path("id").asText();
        assertFalse(s.path("tasks").get(0).has("sample"));
        assertThrows(ResponseStatusException.class,()->service.hint(7,id,task));
        assertThrows(ResponseStatusException.class,()->service.submit(7,id,answer(task))); // audio required
    }
    @Test void finishPreventsFurtherSubmissions() {
        String id=start("practice").path("id").asText(); service.finish(7,id);
        assertThrows(ResponseStatusException.class,()->service.submit(7,id,answer("ket-reason-1")));
    }
    @Test void unrelatedTaskAndParentAreRejected() {
        String id=start("practice").path("id").asText();
        assertThrows(ResponseStatusException.class,()->service.submit(7,id,answer("ket-detail-1")));
        assertThrows(ResponseStatusException.class,()->service.transfer(7,id,"absent"));
    }
    @Test void hintsAreDurableAndVisibleOnlyAfterRequest() {
        var s=start("practice"); String id=s.path("id").asText(); assertFalse(s.path("tasks").get(0).has("hint"));
        service.hint(7,id,"ket-reason-1"); assertTrue(service.session(7,id).path("tasks").get(0).has("hint"));
    }
}
