package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.AbstractSkdvinTest;
import in.skdv.skdvinbackend.model.entity.Sequence;
import in.skdv.skdvinbackend.service.ISequenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SequenceServiceTest extends AbstractSkdvinTest {

    @Autowired
    private ISequenceService sequenceService;

    @Autowired
    private MongoOperations mongoOperations;

    @Test
    public void generateNotExistingId() {
        int foo = sequenceService.getNextSequence("foo");
        assertEquals(1, foo);
    }

    @Test
    public void generateWithExistingId() {
        Sequence seq = new Sequence("bar", 5);
        mongoOperations.save(seq);

        int bar = sequenceService.getNextSequence("bar");
        assertEquals(6, bar);
    }
}
