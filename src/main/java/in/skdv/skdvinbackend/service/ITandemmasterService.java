package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsDTO;
import in.skdv.skdvinbackend.util.GenericResult;

import java.time.LocalDate;

public interface ITandemmasterService {

    TandemmasterDetailsDTO getById(String id);

    TandemmasterDetailsDTO getByEmail(String email);

    GenericResult<Void> assignTandemmasterToJumpday(LocalDate date, String tandemmasterId, SimpleAssignment assignment);

    GenericResult<Void> assignTandemmaster(TandemmasterDetailsDTO tandemmasterDetails, boolean selfAssign);

    void delete(String id);

}
