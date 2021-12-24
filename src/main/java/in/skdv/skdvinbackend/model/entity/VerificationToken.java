package in.skdv.skdvinbackend.model.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class VerificationToken {

    private String token;
    private LocalDateTime expiryDate;

}
