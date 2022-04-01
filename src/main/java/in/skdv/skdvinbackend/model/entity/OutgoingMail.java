package in.skdv.skdvinbackend.model.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document
@Data
@RequiredArgsConstructor
public class OutgoingMail {
    @Id
    private String id;
    private final EmailType emailType;
    private final int appointmentId;
    private Appointment appointment;
    private Instant createdAt = Instant.now();
    private Status status = Status.OPEN;
}
