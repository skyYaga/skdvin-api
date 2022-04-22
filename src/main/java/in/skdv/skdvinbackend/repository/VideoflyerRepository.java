package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.entity.Videoflyer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VideoflyerRepository extends MongoRepository<Videoflyer, String> {

    Optional<Videoflyer> findByEmail(String email);

    @Query(value = "{}", sort = "{'favorite': -1, 'firstName': 1}")
    List<Videoflyer> findAllSortByFavorite();
}
