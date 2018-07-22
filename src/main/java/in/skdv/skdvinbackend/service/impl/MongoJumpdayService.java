package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import org.springframework.beans.factory.annotation.Autowired;

public class MongoJumpdayService implements IJumpdayService {

    private JumpdayRepository jumpdayRepository;

    @Autowired
    public MongoJumpdayService(JumpdayRepository jumpdayRepository) {
        this.jumpdayRepository = jumpdayRepository;
    }
}
