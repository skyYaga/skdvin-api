package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.Jumpday;

import java.time.LocalDate;
import java.util.List;

public interface IJumpdayService {

    List<Jumpday> findJumpdays();

    Jumpday findJumpday(LocalDate date);

    Jumpday saveJumpday(Jumpday jumpday);

    Jumpday updateJumpday(LocalDate date, Jumpday changedJumpday);

    void deleteJumpday(LocalDate date);

}
