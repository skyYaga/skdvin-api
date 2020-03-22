package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.converter.VideoflyerConverter;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.VideoflyerRepository;
import in.skdv.skdvinbackend.service.IVideoflyerService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MongoVideoflyerService implements IVideoflyerService {

    private JumpdayRepository jumpdayRepository;
    private VideoflyerRepository videoflyerRepository;
    private VideoflyerConverter converter = new VideoflyerConverter();

    @Autowired
    public MongoVideoflyerService(JumpdayRepository jumpdayRepository, VideoflyerRepository videoflyerRepository) {
        this.jumpdayRepository = jumpdayRepository;
        this.videoflyerRepository = videoflyerRepository;
    }

    @Override
    public VideoflyerDetailsDTO getById(String id) {
        Map<LocalDate, Boolean> assignments = new HashMap<>();

        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(id);
        if (videoflyer.isEmpty()) {
            return null;
        }

        jumpdayRepository.findAll().forEach(j -> {
            Optional<Videoflyer> localVideoflyer = j.getVideoflyer().stream().filter(v -> v.getId().equals(id)).findFirst();
            assignments.put(j.getDate(), localVideoflyer.isPresent());
        });

        VideoflyerDetailsDTO videoflyerDetailsDTO = converter.convertToDetailsDto(videoflyer.get());
        videoflyerDetailsDTO.setAssignments(assignments);

        return videoflyerDetailsDTO;
    }

    @Override
    public GenericResult<Void> assignVideoflyerToJumpday(LocalDate date, String videoflyerId, boolean isAddition) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(videoflyerId);

        if (videoflyer.isPresent()) {
            Jumpday jumpday = jumpdayRepository.findByDate(date);
            if (jumpday != null) {
                manageVideoflyerAssignment(jumpday, videoflyer.get(), isAddition);
                return new GenericResult<>(true);
            }
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }
        return new GenericResult<>(false, ErrorMessage.VIDEOFLYER_NOT_FOUND);
    }

    @Override
    public GenericResult<Void> assignVideoflyer(VideoflyerDetailsDTO videoflyerDetails) {
        for (LocalDate date : videoflyerDetails.getAssignments().keySet()) {
            GenericResult<Void> result = assignVideoflyerToJumpday(date, videoflyerDetails.getId(), videoflyerDetails.getAssignments().get(date));
            if (!result.isSuccess()) {
                return new GenericResult<>(false, result.getMessage());
            }
        }
        return new GenericResult<>(true);
    }


    private void manageVideoflyerAssignment(Jumpday jumpday, Videoflyer videoflyer, boolean isAddition) {
        Optional<Videoflyer> foundVideoflyer = jumpday.getVideoflyer().stream()
                .filter(t -> t != null && t.getId().equals(videoflyer.getId()))
                .findFirst();

        if (foundVideoflyer.isEmpty() && isAddition) {
            jumpday.getVideoflyer().add(videoflyer);
            jumpdayRepository.save(jumpday);
        }

        if (foundVideoflyer.isPresent() && !isAddition) {
            jumpday.getVideoflyer().remove(foundVideoflyer.get());
            jumpdayRepository.save(jumpday);
        }
    }
}
