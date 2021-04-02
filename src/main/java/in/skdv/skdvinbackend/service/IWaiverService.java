package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.dto.WaiverDTO;
import in.skdv.skdvinbackend.util.GenericResult;

import java.util.List;

public interface IWaiverService {
    List<WaiverDTO> getWaivers();

    GenericResult<WaiverDTO> saveWaiver(WaiverDTO waiver);

}
