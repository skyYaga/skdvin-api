package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.converter.TandemmasterConverter;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.TandemmasterRepository;
import in.skdv.skdvinbackend.service.ITandemmasterService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MongoTandemmasterService implements ITandemmasterService {

    private JumpdayRepository jumpdayRepository;
    private TandemmasterRepository tandemmasterRepository;
    private TandemmasterConverter converter = new TandemmasterConverter();

    @Autowired
    public MongoTandemmasterService(JumpdayRepository jumpdayRepository, TandemmasterRepository tandemmasterRepository) {
        this.jumpdayRepository = jumpdayRepository;
        this.tandemmasterRepository = tandemmasterRepository;
    }

    @Override
    public TandemmasterDetailsDTO getById(String id) {
        Map<LocalDate, Boolean> assignments = new HashMap<>();

        Optional<Tandemmaster> tandemmaster = tandemmasterRepository.findById(id);
        if (tandemmaster.isEmpty()) {
            return null;
        }

        jumpdayRepository.findAll().forEach(j -> {
            Optional<Tandemmaster> localTandemmaster = j.getTandemmaster().stream().filter(t -> t.getId().equals(id)).findFirst();
            assignments.put(j.getDate(), localTandemmaster.isPresent());
        });

        TandemmasterDetailsDTO tandemmasterDetailsDTO = converter.convertToDetailsDto(tandemmaster.get());
        tandemmasterDetailsDTO.setAssignments(assignments);

        return tandemmasterDetailsDTO;
    }


    @Override
    public GenericResult<Void> assignTandemmasterToJumpday(LocalDate date, String tandemmasterId, boolean isAddition) {
        Optional<Tandemmaster> tandemmaster = tandemmasterRepository.findById(tandemmasterId);

        if (tandemmaster.isPresent()) {
            Jumpday jumpday = jumpdayRepository.findByDate(date);
            if (jumpday != null) {
                manageTandemmasterAssignment(jumpday, tandemmaster.get(), isAddition);
                return new GenericResult<>(true);
            }
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }
        return new GenericResult<>(false, ErrorMessage.TANDEMMASTER_NOT_FOUND);
    }

    @Override
    public GenericResult<Void> assignTandemmaster(TandemmasterDetailsDTO tandemmasterDetails) {
        for (LocalDate date : tandemmasterDetails.getAssignments().keySet()) {
            GenericResult<Void> result = assignTandemmasterToJumpday(date, tandemmasterDetails.getId(), tandemmasterDetails.getAssignments().get(date));
            if (!result.isSuccess()) {
                return new GenericResult<>(false, result.getMessage());
            }
        }
        return new GenericResult<>(true);
    }

    private void manageTandemmasterAssignment(Jumpday jumpday, Tandemmaster tandemmaster, boolean isAddition) {
        Optional<Tandemmaster> foundTandemmaster = jumpday.getTandemmaster().stream()
                .filter(t -> t != null && t.getId().equals(tandemmaster.getId()))
                .findFirst();

        if (foundTandemmaster.isEmpty() && isAddition) {
            jumpday.getTandemmaster().add(tandemmaster);
            jumpdayRepository.save(jumpday);
        }

        if (foundTandemmaster.isPresent() && !isAddition) {
            jumpday.getTandemmaster().remove(foundTandemmaster.get());
            jumpdayRepository.save(jumpday);
        }
    }
}
