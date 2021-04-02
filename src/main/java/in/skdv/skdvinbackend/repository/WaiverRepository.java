package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.entity.waiver.Waiver;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WaiverRepository extends MongoRepository<Waiver, String> {

}
