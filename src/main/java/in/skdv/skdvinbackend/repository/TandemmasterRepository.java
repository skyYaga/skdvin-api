package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TandemmasterRepository extends MongoRepository<Tandemmaster, String> {

    Optional<Tandemmaster> findByEmail(String email);

    @Query(value = "{}", sort = "{'favorite': -1, 'firstName': 1}")
    List<Tandemmaster> findAllSortByFavorite();
}
