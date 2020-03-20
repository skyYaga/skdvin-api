package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TandemmasterRepository extends MongoRepository<Tandemmaster, String> {

}
