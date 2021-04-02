package in.skdv.skdvinbackend.model.common.waiver;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AbstractWaiver {

    private int appointmentId;

    @NotNull
    private String waiverText;

    @Valid
    @NotNull
    private WaiverCustomer waiverCustomer;

    @NotNull
    private String signature;

    private String parentSignature1;
    private String parentSignature2;

    private boolean gdprSocial;

}