package in.skdv.skdvinbackend.model.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class Jumper {

    @NotNull
    private final String firstName;
    @NotNull
    private final String lastName;
    @NotNull
    private final LocalDate dateOfBirth;

    private boolean voucher = false;

}
