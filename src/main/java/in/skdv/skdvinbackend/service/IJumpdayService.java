package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.Jumpday;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface IJumpdayService {

    List<Jumpday> findJumpdays();

    List<Jumpday> findJumpdaysByMonth(YearMonth yearMonth);

    Jumpday findJumpday(LocalDate date);

    Jumpday saveJumpday(Jumpday jumpday);

    Jumpday updateJumpday(LocalDate date, Jumpday changedJumpday);

    void deleteJumpday(LocalDate date);

}
