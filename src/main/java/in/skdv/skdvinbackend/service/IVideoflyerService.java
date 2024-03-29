package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.model.entity.VideoflyerDetails;

import java.util.List;

public interface IVideoflyerService {

    Videoflyer save(Videoflyer convertedInput);

    Videoflyer updateVideoflyer(Videoflyer tandemmaster);

    List<Videoflyer> findAll();

    VideoflyerDetails getById(String id);

    VideoflyerDetails getByEmail(String email);

    void assignVideoflyer(VideoflyerDetails videoflyerDetails, boolean selfAssign);

    void delete(String id);

}
