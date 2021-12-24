package in.skdv.skdvinbackend.model.common.waiver;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class AbstractWaiver {

    private int appointmentId;

    private WaiverState state = WaiverState.NEW;

    @Valid
    @NotNull
    private WaiverCustomer waiverCustomer;

    @NotNull
    private String signature;

    private String parentSignature1;
    private String parentSignature2;

    private String tandemmaster;
    private String tandemmasterSignature;

    private boolean gdprSocial;

}