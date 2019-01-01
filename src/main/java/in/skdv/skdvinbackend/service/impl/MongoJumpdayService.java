package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

public class MongoJumpdayService implements IJumpdayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoJumpdayService.class);

    private JumpdayRepository jumpdayRepository;

    @Autowired
    public MongoJumpdayService(JumpdayRepository jumpdayRepository) {
        this.jumpdayRepository = jumpdayRepository;
    }

    @Override
    public GenericResult<List<Jumpday>> findJumpdays() {
        try {
            List<Jumpday> jumpdays = jumpdayRepository.findAll();
            return new GenericResult<>(true, jumpdays);
        } catch (Exception e) {
            LOGGER.error("Error finding jumpdays", e);
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG, e);
        }
    }

    @Override
    public GenericResult<Jumpday> findJumpday(LocalDate date) {
        try {
            Jumpday jumpday = jumpdayRepository.findByDate(date);
            if (jumpday == null) {
                return new GenericResult<>(false, ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
            }
            return new GenericResult<>(true, jumpday);
        } catch (Exception e) {
            LOGGER.error("Error finding jumpday", e);
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG, e);
        }
    }

    @Override
    public GenericResult<Jumpday> saveJumpday(Jumpday jumpday) {
        try {
            Jumpday existingJumpday = jumpdayRepository.findByDate(jumpday.getDate());
            if (existingJumpday != null) {
                return new GenericResult<>(false, ErrorMessage.JUMPDAY_ALREADY_EXISTS_MSG);
            }
            jumpday = jumpdayRepository.save(jumpday);
            return new GenericResult<>(true, jumpday);
        } catch (Exception e) {
            LOGGER.error("Error saving jumpday", e);
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_SERVICE_ERROR_MSG, e);
        }
    }
}
