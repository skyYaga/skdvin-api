package in.skdv.skdvinbackend.model.entity;

import in.skdv.skdvinbackend.model.common.AbstractAppointment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.Locale;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Appointment extends AbstractAppointment {

    @Id
    private int appointmentId;
    private VerificationToken verificationToken;
    private boolean reminderSent = false;
    private String lang = Locale.GERMAN.getLanguage();

}
