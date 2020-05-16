package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.converter.AssignmentConverter;
import in.skdv.skdvinbackend.model.converter.TandemmasterConverter;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.TandemmasterRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.ITandemmasterService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class MongoTandemmasterService implements ITandemmasterService {

    private JumpdayRepository jumpdayRepository;
    private TandemmasterRepository tandemmasterRepository;
    private ISettingsService settingsService;
    private TandemmasterConverter tandemmasterConverter = new TandemmasterConverter();
    private AssignmentConverter assignmentConverter = new AssignmentConverter();


    @Autowired
    public MongoTandemmasterService(JumpdayRepository jumpdayRepository, TandemmasterRepository tandemmasterRepository,
                                    ISettingsService settingsService) {
        this.jumpdayRepository = jumpdayRepository;
        this.tandemmasterRepository = tandemmasterRepository;
        this.settingsService = settingsService;
    }

    @Override
    public TandemmasterDetailsDTO getById(String id) {
        Optional<Tandemmaster> tandemmaster = tandemmasterRepository.findById(id);
        if (tandemmaster.isEmpty()) {
            return null;
        }

        return getDetails(tandemmaster.get());
    }

    @Override
    public TandemmasterDetailsDTO getByEmail(String email) {
        Optional<Tandemmaster> tandemmaster = tandemmasterRepository.findByEmail(email);
        if (tandemmaster.isEmpty()) {
            return null;
        }

        return getDetails(tandemmaster.get());
    }

    private TandemmasterDetailsDTO getDetails(Tandemmaster tandemmaster) {
        Map<LocalDate, SimpleAssignment> assignments = new HashMap<>();

        jumpdayRepository.findAll().forEach(j -> {
            Optional<Assignment<Tandemmaster>> localAssignment = j.getTandemmaster().stream()
                    .filter(t -> t != null && t.getFlyer() != null && t.getFlyer().getId().equals(tandemmaster.getId())).findFirst();
            localAssignment
                    .ifPresentOrElse(assignment -> assignments.put(j.getDate(), assignmentConverter.convertToSimpleAssignment(assignment)),
                            () -> assignments.put(j.getDate(), new SimpleAssignment(false)));
        });

        return tandemmasterConverter.convertToDetailsDto(tandemmaster, assignments);
    }

    @Override
    public GenericResult<Void> assignTandemmasterToJumpday(LocalDate date, String tandemmasterId, SimpleAssignment assignment) {
        Optional<Tandemmaster> tandemmaster = tandemmasterRepository.findById(tandemmasterId);

        if (tandemmaster.isPresent()) {
            Jumpday jumpday = jumpdayRepository.findByDate(date);
            if (jumpday != null) {
                manageTandemmasterAssignment(jumpday, tandemmaster.get(), assignment);
                return new GenericResult<>(true);
            }
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }
        return new GenericResult<>(false, ErrorMessage.TANDEMMASTER_NOT_FOUND);
    }

    @Override
    public GenericResult<Void> assignTandemmaster(TandemmasterDetailsDTO tandemmasterDetails, boolean selfAssign) {
        if (selfAssign) {
            ErrorMessage errorMessage = checkSelfAssignPrerequisites(tandemmasterDetails);
            if (errorMessage != null) {
                return new GenericResult<>(false, errorMessage);
            }
        }
        for (LocalDate date : tandemmasterDetails.getAssignments().keySet()) {
            GenericResult<Void> result = assignTandemmasterToJumpday(date, tandemmasterDetails.getId(), tandemmasterDetails.getAssignments().get(date));
            if (!result.isSuccess()) {
                return new GenericResult<>(false, result.getMessage());
            }
        }
        return new GenericResult<>(true);
    }

    private ErrorMessage checkSelfAssignPrerequisites(TandemmasterDetailsDTO newTandemmasterDetails) {
        SelfAssignmentMode selfAssignmentMode = settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage()).getSelfAssignmentMode();
        if (SelfAssignmentMode.READONLY.equals(selfAssignmentMode)) {
            return ErrorMessage.SELFASSIGNMENT_READONLY;
        }
        if (SelfAssignmentMode.NODELETE.equals(selfAssignmentMode)) {
            TandemmasterDetailsDTO currentDetails = getById(newTandemmasterDetails.getId());
            for (Map.Entry<LocalDate, SimpleAssignment> currentEntry : currentDetails.getAssignments().entrySet()) {
                SimpleAssignment existingAssignment = newTandemmasterDetails.getAssignments().get(currentEntry.getKey());
                if (existingAssignment == null || !existingAssignment.equals(currentEntry.getValue())) {
                    return ErrorMessage.SELFASSIGNMENT_NODELETE;
                }
            }
        }
        return null;
    }

    @Override
    public void delete(String id) {
        // Unassign Tandemmaster
        TandemmasterDetailsDTO detailsDTO = getById(id);
        detailsDTO.getAssignments().forEach((key, value) -> value.setAssigned(false));
        assignTandemmaster(detailsDTO, false);

        // Delete Tandemmaster
        tandemmasterRepository.deleteById(id);
    }

    private void manageTandemmasterAssignment(Jumpday jumpday, Tandemmaster tandemmaster, SimpleAssignment simpleAssignment) {
        Optional<Assignment<Tandemmaster>> foundAssignment = jumpday.getTandemmaster().stream()
                .filter(t -> t != null && t.getFlyer() != null && t.getFlyer().getId().equals(tandemmaster.getId()))
                .findFirst();

        Assignment<Tandemmaster> assignment = assignmentConverter.convertToAssignment(simpleAssignment, tandemmaster);

        if (foundAssignment.isEmpty() && assignment.isAssigned()) {
            jumpday.getTandemmaster().add(assignment);
            jumpdayRepository.save(jumpday);
        }

        if (foundAssignment.isPresent() && !assignment.isAssigned()) {
            jumpday.getTandemmaster().remove(foundAssignment.get());
            jumpdayRepository.save(jumpday);
        }

        if (foundAssignment.isPresent() && assignment.isAssigned()) {
            jumpday.getTandemmaster().remove(foundAssignment.get());
            jumpday.getTandemmaster().add(assignment);
            jumpdayRepository.save(jumpday);
        }
    }
}
