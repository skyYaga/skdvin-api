package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TandemmasterRepository extends MongoRepository<Tandemmaster, String> {

    Optional<Tandemmaster> findByEmail(String email);

}
