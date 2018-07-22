package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.model.Sequence;
import in.skdv.skdvinbackend.service.ISequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Service that generated sequence-style auto incremented ids
 * @see <a href="https://www.mkyong.com/mongodb/spring-data-mongodb-auto-sequence-id-example/">
 *     https://www.mkyong.com/mongodb/spring-data-mongodb-auto-sequence-id-example/</a>
 */
public class SequenceService implements ISequenceService {

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public int getNextSequence(String sequenceName) {

        //get sequence id
        Query query = new Query(Criteria.where("_id").is(sequenceName));

        //increase sequence id by 1
        Update update = new Update();
        update.inc("seq", 1);

        //return new increased id
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);

        //this is the magic happened.
        Sequence seq = mongoOperations.findAndModify(query, update, options, Sequence.class);

        //if no id, create a new one with id = 1
        if (seq == null) {
            seq = new Sequence(sequenceName, 1);
            mongoOperations.save(seq);
        }

        return seq.getSeq();
    }

}
