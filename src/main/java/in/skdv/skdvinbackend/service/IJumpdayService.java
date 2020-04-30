package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.util.GenericResult;

import java.time.LocalDate;
import java.util.List;

public interface IJumpdayService {

    GenericResult<List<Jumpday>> findJumpdays();

    GenericResult<Jumpday> findJumpday(LocalDate date);

    GenericResult<Jumpday> saveJumpday(Jumpday jumpday);

    GenericResult<Jumpday> updateJumpday(LocalDate date, Jumpday changedJumpday);

    GenericResult<Void> deleteJumpday(LocalDate date);

}
