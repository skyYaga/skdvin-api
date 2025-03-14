package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.InvalidRequestException;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.model.entity.VideoflyerDetails;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import in.skdv.skdvinbackend.model.mapper.AssignmentMapper;
import in.skdv.skdvinbackend.model.mapper.VideoflyerMapper;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.VideoflyerRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.IVideoflyerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoflyerService implements IVideoflyerService {

    private final JumpdayRepository jumpdayRepository;
    private final VideoflyerRepository videoflyerRepository;
    private final ISettingsService settingsService;
    private final VideoflyerMapper videoflyerMapper;
    private final AssignmentMapper assignmentMapper;
    @Lazy
    private final VideoflyerService self;

    @Override
    @Transactional
    public Videoflyer save(Videoflyer input) {
        return videoflyerRepository.save(input);
    }

    @Override
    public List<Videoflyer> findAll() {
        return videoflyerRepository.findAllSortByFavorite();
    }

    @Override
    public VideoflyerDetails getById(String id) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(id);
        if (videoflyer.isEmpty()) {
            log.warn("Videoflyer {} not found", id); //NOSONAR would be confusing to have it as constant
            throw new NotFoundException(ErrorMessage.VIDEOFLYER_NOT_FOUND);
        }

        return getDetails(videoflyer.get());
    }

    @Override
    public VideoflyerDetails getByEmail(String email) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findByEmail(email);
        if (videoflyer.isEmpty()) {
            log.warn("Videoflyer with email {} not found", email);
            throw new NotFoundException(ErrorMessage.VIDEOFLYER_NOT_FOUND);
        }

        return getDetails(videoflyer.get());
    }

    @Override
    @Transactional
    public void assignVideoflyer(VideoflyerDetails videoflyerDetails, boolean selfAssign) {
        if (selfAssign) {
            checkSelfAssignPrerequisites(videoflyerDetails);
        }

        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(videoflyerDetails.getId());
        if (videoflyer.isEmpty()) {
            log.warn("Videoflyer {} not found", videoflyerDetails.getId()); //NOSONAR would be confusing to have it as constant
            throw new NotFoundException(ErrorMessage.VIDEOFLYER_NOT_FOUND);
        }

        videoflyerDetails.getAssignments().keySet().parallelStream().forEach(date ->
                assignVideoflyerToJumpday(date, videoflyer.get(), videoflyerDetails.getAssignments().get(date)));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(id);

        if (videoflyer.isEmpty()) {
            log.warn("Videoflyer {} not found", id); //NOSONAR would be confusing to have it as constant
            throw new NotFoundException(ErrorMessage.VIDEOFLYER_NOT_FOUND);
        }

        // Unassign Videoflyer
        VideoflyerDetails details = getById(id);
        details.getAssignments().forEach((key, value) -> value.setAssigned(false));
        self.assignVideoflyer(details, false);

        // Delete Videoflyer
        videoflyerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Videoflyer updateVideoflyer(Videoflyer input) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(input.getId());

        if (videoflyer.isEmpty()) {
            log.warn("Videoflyer {} not found.", input.getId()); //NOSONAR would be confusing to have it as constant
            throw new NotFoundException(ErrorMessage.VIDEOFLYER_NOT_FOUND);
        }
        return videoflyerRepository.save(input);
    }

    private VideoflyerDetails getDetails(Videoflyer videoflyer) {
        Map<LocalDate, SimpleAssignment> assignments = new HashMap<>();

        jumpdayRepository.findAllAfterIncludingDate(LocalDate.now()).forEach(j -> {
            Optional<Assignment<Videoflyer>> localAssignment = j.getVideoflyer().stream()
                    .filter(t -> t != null && t.getFlyer() != null && t.getFlyer().getId().equals(videoflyer.getId())).findFirst();
            localAssignment
                    .ifPresentOrElse(assignment -> assignments.put(j.getDate(), assignmentMapper.videoflyerToSimpleAssignment(assignment)),
                            () -> assignments.put(j.getDate(), new SimpleAssignment(false)));
        });

        return videoflyerMapper.toDetails(videoflyer, assignments);
    }

    public void assignVideoflyerToJumpday(LocalDate date, Videoflyer videoflyer, SimpleAssignment simpleAssignment) {
        Jumpday jumpday = jumpdayRepository.findByDate(date);
        if (jumpday == null) {
            log.error("Jumpday is null");
            throw new NotFoundException(ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }
        manageVideoflyerAssignment(jumpday, videoflyer, simpleAssignment);
    }

    private void checkSelfAssignPrerequisites(VideoflyerDetails newVideoflyerDetails) {
        SelfAssignmentMode selfAssignmentMode = settingsService.getSettings().getCommonSettings().getSelfAssignmentMode();
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

    private void manageVideoflyerAssignment(Jumpday jumpday, Videoflyer videoflyer, SimpleAssignment simpleAssignment) {
        Optional<Assignment<Videoflyer>> foundAssignment = jumpday.getVideoflyer().stream()
                .filter(t -> t != null && t.getFlyer() != null && t.getFlyer().getId().equals(videoflyer.getId()))
                .findFirst();

        Assignment<Videoflyer> assignment = assignmentMapper.toAssignment(simpleAssignment, videoflyer);

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
