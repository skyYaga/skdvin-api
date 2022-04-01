package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.entity.OutgoingMail;
import in.skdv.skdvinbackend.model.entity.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmailOutboxRepository extends MongoRepository<OutgoingMail, String> {

    List<OutgoingMail> findByStatus(Status status);
}
