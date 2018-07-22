package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.entity.Sequence;
import in.skdv.skdvinbackend.service.ISequenceService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SequenceServiceTest {

    @Autowired
    private ISequenceService sequenceService;

    @Autowired
    private MongoOperations mongoOperations;

    @Test
    public void generateNotExistingId() {
        int foo = sequenceService.getNextSequence("foo");
        Assert.assertEquals(1, foo);
    }

    @Test
    public void generateWithExistingId() {
        Sequence seq = new Sequence("bar", 5);
        mongoOperations.save(seq);

        int bar = sequenceService.getNextSequence("bar");
        Assert.assertEquals(6, bar);
    }
}
