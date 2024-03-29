package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.TandemmasterDetails;

import java.util.List;

public interface ITandemmasterService {

    Tandemmaster save(Tandemmaster convertedInput);

    Tandemmaster updateTandemmaster(Tandemmaster tandemmaster);

    List<Tandemmaster> findAll();

    TandemmasterDetails getById(String id);

    TandemmasterDetails getByEmail(String email);

    void assignTandemmaster(TandemmasterDetails tandemmasterDetails, boolean selfAssign);

    void delete(String id);

}
