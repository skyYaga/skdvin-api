package in.skdv.skdvinbackend.service;

import in.skdv.skdvinbackend.model.entity.waiver.Waiver;

import java.util.List;

public interface IWaiverService {
    List<Waiver> getWaivers();

    Waiver saveWaiver(Waiver waiver);

    Waiver updateWaiver(Waiver waiver);
}
