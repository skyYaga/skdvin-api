package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppointmentRepository extends MongoRepository<Appointment, Integer> {
}
