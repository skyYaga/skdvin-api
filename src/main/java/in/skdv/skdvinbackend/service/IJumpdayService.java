package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.util.GenericResult;

import java.time.LocalDate;
import java.util.List;

public interface IJumpdayService {

    String JUMPDAY_SERVICE_ERROR_MSG = "jumpday.service.error";
    String JUMPDAY_NOT_FOUND_MSG = "jumpday.not.found";
    String JUMPDAY_ALREADY_EXISTS_MSG = "jumpday.already.exists";

    GenericResult<List<Jumpday>> findJumpdays();

    GenericResult<Jumpday> findJumpday(LocalDate date);

    GenericResult<Jumpday> saveJumpday(Jumpday jumpday);

}
