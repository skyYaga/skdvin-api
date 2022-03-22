package in.skdv.skdvinbackend.model.common;

import in.skdv.skdvinbackend.model.entity.AppointmentState;
import in.skdv.skdvinbackend.model.entity.Customer;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;

import static in.skdv.skdvinbackend.model.entity.AppointmentState.UNCONFIRMED;

@Data
public abstract class AbstractAppointment {

    @NotNull
    private Instant date;

    @NotNull
    private Customer customer;

    @NotNull
    @Min(1)
    private int tandem;

    @NotNull
    private int picOrVid;

    @NotNull
    private int picAndVid;

    @NotNull
    private int handcam;

    private AppointmentState state = UNCONFIRMED;

    private LocalDateTime createdOn;

    private String createdBy;

    private String clientId;
    private String note = "";

}
