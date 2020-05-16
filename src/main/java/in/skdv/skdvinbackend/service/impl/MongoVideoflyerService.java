package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.converter.AssignmentConverter;
import in.skdv.skdvinbackend.model.converter.VideoflyerConverter;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.VideoflyerRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.IVideoflyerService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class MongoVideoflyerService implements IVideoflyerService {

    private JumpdayRepository jumpdayRepository;
    private VideoflyerRepository videoflyerRepository;
    private ISettingsService settingsService;
    private VideoflyerConverter videoflyerConverter = new VideoflyerConverter();
    private AssignmentConverter assignmentConverter = new AssignmentConverter();

    @Autowired
    public MongoVideoflyerService(JumpdayRepository jumpdayRepository, VideoflyerRepository videoflyerRepository, ISettingsService settingsService) {
        this.jumpdayRepository = jumpdayRepository;
        this.videoflyerRepository = videoflyerRepository;
        this.settingsService = settingsService;
    }

    @Override
    public VideoflyerDetailsDTO getById(String id) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(id);
        if (videoflyer.isEmpty()) {
            return null;
        }

        return getDetails(videoflyer.get());
    }

    @Override
    public VideoflyerDetailsDTO getByEmail(String email) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findByEmail(email);
        if (videoflyer.isEmpty()) {
            return null;
        }

        return getDetails(videoflyer.get());
    }

    private VideoflyerDetailsDTO getDetails(Videoflyer videoflyer) {
        Map<LocalDate, SimpleAssignment> assignments = new HashMap<>();

        jumpdayRepository.findAll().forEach(j -> {
            Optional<Assignment<Videoflyer>> localAssignment = j.getVideoflyer().stream()
                    .filter(t -> t != null && t.getFlyer() != null && t.getFlyer().getId().equals(videoflyer.getId())).findFirst();
            localAssignment
                    .ifPresentOrElse(assignment -> assignments.put(j.getDate(), assignmentConverter.convertToSimpleAssignment(assignment)),
                            () -> assignments.put(j.getDate(), new SimpleAssignment(false)));
        });

        return videoflyerConverter.convertToDetailsDto(videoflyer, assignments);
    }

    @Override
    public GenericResult<Void> assignVideoflyerToJumpday(LocalDate date, String videoflyerId, SimpleAssignment simpleAssignment) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(videoflyerId);

        if (videoflyer.isPresent()) {
            Jumpday jumpday = jumpdayRepository.findByDate(date);
            if (jumpday != null) {
                manageVideoflyerAssignment(jumpday, videoflyer.get(), simpleAssignment);
                return new GenericResult<>(true);
            }
            return new GenericResult<>(false, ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }
        return new GenericResult<>(false, ErrorMessage.VIDEOFLYER_NOT_FOUND);
    }

    @Override
    public GenericResult<Void> assignVideoflyer(VideoflyerDetailsDTO videoflyerDetails, boolean selfAssign) {
        if (selfAssign) {
            ErrorMessage errorMessage = checkSelfAssignPrerequisites(videoflyerDetails);
            if (errorMessage != null) {
                return new GenericResult<>(false, errorMessage);
            }
        }
        for (LocalDate date : videoflyerDetails.getAssignments().keySet()) {
            GenericResult<Void> result = assignVideoflyerToJumpday(date, videoflyerDetails.getId(), videoflyerDetails.getAssignments().get(date));
            if (!result.isSuccess()) {
                return new GenericResult<>(false, result.getMessage());
            }
        }
        return new GenericResult<>(true);
    }

    private ErrorMessage checkSelfAssignPrerequisites(VideoflyerDetailsDTO newVideoflyerDetails) {
        SelfAssignmentMode selfAssignmentMode = settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage()).getSelfAssignmentMode();
        if (SelfAssignmentMode.READONLY.equals(selfAssignmentMode)) {
            return ErrorMessage.SELFASSIGNMENT_READONLY;
        }
        if (SelfAssignmentMode.NODELETE.equals(selfAssignmentMode)) {
            VideoflyerDetailsDTO currentDetails = getById(newVideoflyerDetails.getId());
            for (Map.Entry<LocalDate, SimpleAssignment> currentEntry : currentDetails.getAssignments().entrySet()) {
                SimpleAssignment existingAssignment = newVideoflyerDetails.getAssignments().get(currentEntry.getKey());
                if (existingAssignment == null || !existingAssignment.equals(currentEntry.getValue())) {
                    return ErrorMessage.SELFASSIGNMENT_NODELETE;
                }
            }
        }
        return null;
    }

    @Override
    public void delete(String id) {
        // Unassign Videoflyer
        VideoflyerDetailsDTO detailsDTO = getById(id);
        detailsDTO.getAssignments().forEach((key, value) -> value.setAssigned(false));
        assignVideoflyer(detailsDTO, false);

        // Delete Videoflyer
        videoflyerRepository.deleteById(id);
    }

    private void manageVideoflyerAssignment(Jumpday jumpday, Videoflyer videoflyer, SimpleAssignment simpleAssignment) {
        Optional<Assignment<Videoflyer>> foundAssignment = jumpday.getVideoflyer().stream()
                .filter(t -> t != null && t.getFlyer() != null && t.getFlyer().getId().equals(videoflyer.getId()))
                .findFirst();

        Assignment<Videoflyer> assignment = assignmentConverter.convertToAssignment(simpleAssignment, videoflyer);

        if (foundAssignment.isEmpty() && assignment.isAssigned()) {
            jumpday.getVideoflyer().add(assignment);
            jumpdayRepository.save(jumpday);
        }

        if (foundAssignment.isPresent() && !assignment.isAssigned()) {
            jumpday.getVideoflyer().remove(foundAssignment.get());
            jumpdayRepository.save(jumpday);
        }

        if (foundAssignment.isPresent() && assignment.isAssigned()) {
            jumpday.getVideoflyer().remove(foundAssignment.get());
            jumpday.getVideoflyer().add(assignment);
            jumpdayRepository.save(jumpday);
        }
    }
}
