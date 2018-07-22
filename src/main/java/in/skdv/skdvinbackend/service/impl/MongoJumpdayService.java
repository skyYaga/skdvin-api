package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;

public class MongoJumpdayService implements IJumpdayService {

    private JumpdayRepository jumpdayRepository;

    public MongoJumpdayService(JumpdayRepository jumpdayRepository) {
        this.jumpdayRepository = jumpdayRepository;
    }
}
