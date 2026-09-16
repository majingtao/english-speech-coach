package cn.kugua.module.english.service.speakingcoach;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class CoachEvidenceTest {
    private final ObjectMapper json = new ObjectMapper();
    private final LocalDate date = LocalDate.of(2026,9,9);
    private CoachEvidence.Observation row(String task,String stage,boolean assisted,int day,boolean met) throws Exception {
        return new CoachEvidence.Observation(task,stage,assisted,date.plusDays(day),json.readTree("{\"skills\":[{\"code\":\"reason\",\"observed\":true,\"met\":"+met+"}]}"));
    }
    @Test void retryAndHintDoNotProveMastery() throws Exception {
        var rows=List.of(row("a","retry",false,0,true),row("b","initial",true,1,true),row("c","initial",false,2,true));
        var skill=CoachEvidence.summarize(rows,date).get(1);
        assertEquals(1,skill.observed()); assertNotEquals("stable",skill.status());
    }
    @Test void requiresDifferentTasksAndDates() throws Exception {
        var rows=List.of(row("a","initial",false,0,true),row("b","transfer",false,0,true),row("c","transfer",false,1,true));
        assertEquals("stable",CoachEvidence.summarize(rows,date).get(1).status());
        assertEquals("unobserved",CoachEvidence.summarize(rows,date).get(3).status());
    }
    @Test void repetitionSameDayCountsOnce() throws Exception {
        var rows=List.of(row("a","initial",false,0,true),row("a","initial",false,0,true),row("a","initial",false,0,true));
        assertEquals(1,CoachEvidence.summarize(rows,date).get(1).observed());
    }
    @Test void recentFailureResetsStableEvidence() throws Exception {
        var rows=List.of(row("a","initial",false,0,true),row("b","transfer",false,0,true),row("c","transfer",false,1,true),row("d","initial",false,2,false));
        assertNotEquals("stable",CoachEvidence.summarize(rows,date).get(1).status());
    }
}
