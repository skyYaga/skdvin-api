package in.skdv.skdvinbackend.model.entity.settings;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;

@Data
public class AdminSettings {

    @JsonFormat(pattern = "HH:mm")
    private LocalTime tandemsFrom;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime tandemsTo;
    private String interval;
    private int tandemCount;
    private int picOrVidCount;
    private int picAndVidCount;
    private int handcamCount;
    private String bccMail = "";

}
