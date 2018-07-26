package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);

}
