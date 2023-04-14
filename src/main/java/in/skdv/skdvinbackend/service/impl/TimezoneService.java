package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.service.ITimezoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class TimezoneService implements ITimezoneService {

    private final ZoneId zoneId;

    @Override
    public ZoneId getZoneId() {
        return zoneId;
    }
}
