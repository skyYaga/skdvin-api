package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.util.GenericResult;

import java.time.LocalDate;

public interface IVideoflyerService {

    VideoflyerDetailsDTO getById(String id);

    GenericResult<Void> assignVideoflyerToJumpday(LocalDate date, String videoflyerId, SimpleAssignment simpleAssignment);

    GenericResult<Void> assignVideoflyer(VideoflyerDetailsDTO videoflyerDetails);

}
