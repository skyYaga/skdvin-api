package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.util.GenericResult;

import java.time.LocalDate;

public interface IVideoflyerService {

    VideoflyerDetailsDTO getById(String id);

    VideoflyerDetailsDTO getByEmail(String email);

    GenericResult<Void> assignVideoflyerToJumpday(LocalDate date, String videoflyerId, SimpleAssignment simpleAssignment);

    GenericResult<Void> assignVideoflyer(VideoflyerDetailsDTO videoflyerDetails, boolean selfAssign);

    void delete(String id);

}
