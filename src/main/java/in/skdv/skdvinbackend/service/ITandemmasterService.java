package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsDTO;
import in.skdv.skdvinbackend.util.GenericResult;

import java.time.LocalDate;

public interface ITandemmasterService {

    TandemmasterDetailsDTO getById(String id);

    GenericResult<Void> assignTandemmasterToJumpday(LocalDate date, String tandemmasterId, boolean isAddition);

    GenericResult<Void> assignTandemmaster(TandemmasterDetailsDTO tandemmasterDetails);

}
