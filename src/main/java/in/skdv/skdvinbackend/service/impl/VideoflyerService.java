package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.InvalidRequestException;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.converter.AssignmentConverter;
import in.skdv.skdvinbackend.model.converter.VideoflyerConverter;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.model.entity.VideoflyerDetails;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.VideoflyerRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.IVideoflyerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class VideoflyerService implements IVideoflyerService {

    private final JumpdayRepository jumpdayRepository;
    private final VideoflyerRepository videoflyerRepository;
    private final ISettingsService settingsService;
    private final VideoflyerConverter videoflyerConverter = new VideoflyerConverter();
    private final AssignmentConverter assignmentConverter = new AssignmentConverter();

    @Override
    public Videoflyer save(Videoflyer input) {
        return videoflyerRepository.save(input);
    }

    @Override
    public List<Videoflyer> findAll() {
        return videoflyerRepository.findAll();
    }

    @Override
    public VideoflyerDetails getById(String id) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(id);
        if (videoflyer.isEmpty()) {
            log.error("Videoflyer {} not found", id);
            throw new NotFoundException(ErrorMessage.VIDEOFLYER_NOT_FOUND);
        }

        return getDetails(videoflyer.get());
    }

    @Override
    public VideoflyerDetails getByEmail(String email) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findByEmail(email);
        if (videoflyer.isEmpty()) {
            log.error("Videoflyer with email {} not found", email);
            throw new NotFoundException(ErrorMessage.VIDEOFLYER_NOT_FOUND);
        }

        return getDetails(videoflyer.get());
    }

    private VideoflyerDetails getDetails(Videoflyer videoflyer) {
        Map<LocalDate, SimpleAssignment> assignments = new HashMap<>();

        jumpdayRepository.findAllAfterIncludingDate(LocalDate.now()).forEach(j -> {
            Optional<Assignment<Videoflyer>> localAssignment = j.getVideoflyer().stream()
                    .filter(t -> t != null && t.getFlyer() != null && t.getFlyer().getId().equals(videoflyer.getId())).findFirst();
            localAssignment
                    .ifPresentOrElse(assignment -> assignments.put(j.getDate(), assignmentConverter.convertToSimpleAssignment(assignment)),
                            () -> assignments.put(j.getDate(), new SimpleAssignment(false)));
        });

        return videoflyerConverter.convertToDetails(videoflyer, assignments);
    }

    public void assignVideoflyerToJumpday(LocalDate date, Videoflyer videoflyer, SimpleAssignment simpleAssignment) {
        Jumpday jumpday = jumpdayRepository.findByDate(date);
        if (jumpday == null) {
            log.error("Jumpday is null");
            throw new NotFoundException(ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }
        manageVideoflyerAssignment(jumpday, videoflyer, simpleAssignment);
    }

    @Override
    public void assignVideoflyer(VideoflyerDetails videoflyerDetails, boolean selfAssign) {
        if (selfAssign) {
            checkSelfAssignPrerequisites(videoflyerDetails);
        }

        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(videoflyerDetails.getId());
        if (videoflyer.isEmpty()) {
            log.error("Videoflyer {} not found", videoflyerDetails.getId());
            throw new NotFoundException(ErrorMessage.VIDEOFLYER_NOT_FOUND);
        }

        videoflyerDetails.getAssignments().keySet().parallelStream().forEach(date ->
                assignVideoflyerToJumpday(date, videoflyer.get(), videoflyerDetails.getAssignments().get(date)));
    }

    private void checkSelfAssignPrerequisites(VideoflyerDetails newVideoflyerDetails) {
        SelfAssignmentMode selfAssignmentMode = settingsService.getCommonSettingsByLanguage(Locale.GERMAN.getLanguage()).getSelfAssignmentMode();
        if (SelfAssignmentMode.READONLY.equals(selfAssignmentMode)) {
            log.error("Selfassignment is in read-only mode.");
            throw new InvalidRequestException(ErrorMessage.SELFASSIGNMENT_READONLY);
        }
        if (SelfAssignmentMode.NODELETE.equals(selfAssignmentMode)) {
            VideoflyerDetails currentDetails = getById(newVideoflyerDetails.getId());
            for (Map.Entry<LocalDate, SimpleAssignment> currentEntry : currentDetails.getAssignments().entrySet()) {
                SimpleAssignment newAssignment = newVideoflyerDetails.getAssignments().get(currentEntry.getKey());
                if (currentEntry.getValue().isAssigned() && (newAssignment == null || !newAssignment.isAssigned())
                        || currentEntry.getValue().isAssigned() && newAssignment.isAllday() != currentEntry.getValue().isAllday()) {
                    log.error("Selfassignment is in no-delete mode.");
                    throw new InvalidRequestException(ErrorMessage.SELFASSIGNMENT_NODELETE);
                }
            }
        }
    }

    @Override
    public void delete(String id) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(id);

        if (videoflyer.isEmpty()) {
            log.error("Videoflyer {} not found", id);
            throw new NotFoundException(ErrorMessage.VIDEOFLYER_NOT_FOUND);
        }

        // Unassign Videoflyer
        VideoflyerDetails details = getById(id);
        details.getAssignments().forEach((key, value) -> value.setAssigned(false));
        assignVideoflyer(details, false);

        // Delete Videoflyer
        videoflyerRepository.deleteById(id);
    }

    @Override
    public Videoflyer updateVideoflyer(Videoflyer input) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(input.getId());

        if (videoflyer.isEmpty()) {
            log.error("Videoflyer {} not found.", input.getId());
            throw new NotFoundException(ErrorMessage.VIDEOFLYER_NOT_FOUND);
        }
        return videoflyerRepository.save(input);
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
