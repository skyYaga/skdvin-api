package in.skdv.skdvinbackend.repository;

import in.skdv.skdvinbackend.model.entity.Jumpday;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface JumpdayRepository extends MongoRepository<Jumpday, Integer> {

    Jumpday findByDate(LocalDate date);

    void deleteByDate(LocalDate date);

    List<Jumpday> findBySlotsAppointmentsAppointmentId(int appointmentId);

    @Query("{'date': {$gte: ?0}}")
    List<Jumpday> findAllAfterIncludingDate(LocalDate date);

}
