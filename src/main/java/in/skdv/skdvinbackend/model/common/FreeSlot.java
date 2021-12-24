package in.skdv.skdvinbackend.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Value
public class FreeSlot {

    LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    List<LocalTime> times;

}
