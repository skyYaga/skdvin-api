package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.InvalidRequestException;
import in.skdv.skdvinbackend.exception.NotFoundException;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.entity.Assignment;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.TandemmasterDetails;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import in.skdv.skdvinbackend.model.mapper.AssignmentMapper;
import in.skdv.skdvinbackend.model.mapper.TandemmasterMapper;
import in.skdv.skdvinbackend.repository.JumpdayRepository;
import in.skdv.skdvinbackend.repository.TandemmasterRepository;
import in.skdv.skdvinbackend.service.ISettingsService;
import in.skdv.skdvinbackend.service.ITandemmasterService;
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
public class TandemmasterService implements ITandemmasterService {

    private final JumpdayRepository jumpdayRepository;
    private final TandemmasterRepository tandemmasterRepository;
    private final ISettingsService settingsService;
    private final TandemmasterMapper tandemmasterMapper;
    private final AssignmentMapper assignmentMapper;
    @Lazy
    private final TandemmasterService self;

    @Override
    @Transactional
    public Tandemmaster save(Tandemmaster input) {
        return tandemmasterRepository.save(input);
    }

    @Override
    public List<Tandemmaster> findAll() {
        return tandemmasterRepository.findAllSortByFavorite();
    }

    @Override
    public TandemmasterDetails getById(String id) {
        Optional<Tandemmaster> tandemmaster = tandemmasterRepository.findById(id);
        if (tandemmaster.isEmpty()) {
            log.warn("Tandemmaster {} not found", id);
            throw new NotFoundException(ErrorMessage.TANDEMMASTER_NOT_FOUND);
        }

        return getDetails(tandemmaster.get());
    }

    @Override
    public TandemmasterDetails getByEmail(String email) {
        Optional<Tandemmaster> tandemmaster = tandemmasterRepository.findByEmail(email);
        if (tandemmaster.isEmpty()) {
            log.warn("Tandemmaster with email {} not found", email);
            throw new NotFoundException(ErrorMessage.TANDEMMASTER_NOT_FOUND);
        }

        return getDetails(tandemmaster.get());
    }

    @Override
    @Transactional
    public void assignTandemmaster(TandemmasterDetails tandemmasterDetails, boolean selfAssign) {
        if (selfAssign) {
            checkSelfAssignPrerequisites(tandemmasterDetails);
        }

        Optional<Tandemmaster> tandemmaster = tandemmasterRepository.findById(tandemmasterDetails.getId());
        if (tandemmaster.isEmpty()) {
            log.warn("Tandemmaster {} not found", tandemmasterDetails.getId());
            throw new NotFoundException(ErrorMessage.TANDEMMASTER_NOT_FOUND);
        }

        tandemmasterDetails.getAssignments().keySet().parallelStream().forEach(date ->
                assignTandemmasterToJumpday(date, tandemmaster.get(), tandemmasterDetails.getAssignments().get(date)));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Optional<Tandemmaster> tandemmaster = tandemmasterRepository.findById(id);

        if (tandemmaster.isEmpty()) {
            log.warn("Tandemmaster {} not found.", id);
            throw new NotFoundException(ErrorMessage.TANDEMMASTER_NOT_FOUND);
        }

        // Unassign Tandemmaster
        TandemmasterDetails details = getById(id);
        details.getAssignments().forEach((key, value) -> value.setAssigned(false));
        self.assignTandemmaster(details, false);

        // Delete Tandemmaster
        tandemmasterRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Tandemmaster updateTandemmaster(Tandemmaster input) {
        Optional<Tandemmaster> tandemmaster = tandemmasterRepository.findById(input.getId());

        if (tandemmaster.isEmpty()) {
            log.warn("Tandemmaster {} not found.", input.getId());
            throw new NotFoundException(ErrorMessage.TANDEMMASTER_NOT_FOUND);
        }
        return tandemmasterRepository.save(input);
    }

    private TandemmasterDetails getDetails(Tandemmaster tandemmaster) {
        Map<LocalDate, SimpleAssignment> assignments = new HashMap<>();

        jumpdayRepository.findAllAfterIncludingDate(LocalDate.now()).forEach(j -> {
            Optional<Assignment<Tandemmaster>> localAssignment = j.getTandemmaster().stream()
                    .filter(t -> t != null && t.getFlyer() != null && t.getFlyer().getId().equals(tandemmaster.getId())).findFirst();
            localAssignment
                    .ifPresentOrElse(assignment -> assignments.put(j.getDate(), assignmentMapper.tandemmasterToSimpleAssignment(assignment)),
                            () -> assignments.put(j.getDate(), new SimpleAssignment(false)));
        });

        return tandemmasterMapper.toDetails(tandemmaster, assignments);
    }

    protected void assignTandemmasterToJumpday(LocalDate date, Tandemmaster tandemmaster, SimpleAssignment assignment) {
        Jumpday jumpday = jumpdayRepository.findByDate(date);
        if (jumpday == null) {
            log.error("Jumpday is null");
            throw new NotFoundException(ErrorMessage.JUMPDAY_NOT_FOUND_MSG);
        }
        manageTandemmasterAssignment(jumpday, tandemmaster, assignment);
    }

    private void checkSelfAssignPrerequisites(TandemmasterDetails newTandemmasterDetails) {
        SelfAssignmentMode selfAssignmentMode = settingsService.getSettings().getCommonSettings().getSelfAssignmentMode();
        if (SelfAssignmentMode.READONLY.equals(selfAssignmentMode)) {
            log.error("Selfassignment is in read-only mode.");
            throw new InvalidRequestException(ErrorMessage.SELFASSIGNMENT_READONLY);
        }
        if (SelfAssignmentMode.NODELETE.equals(selfAssignmentMode)) {
            TandemmasterDetails currentDetails = getById(newTandemmasterDetails.getId());
            for (Map.Entry<LocalDate, SimpleAssignment> currentEntry : currentDetails.getAssignments().entrySet()) {
                SimpleAssignment newAssignment = newTandemmasterDetails.getAssignments().get(currentEntry.getKey());
                if (currentEntry.getValue().isAssigned() && (newAssignment == null || !newAssignment.isAssigned())
                        || currentEntry.getValue().isAssigned() && newAssignment.isAllday() != currentEntry.getValue().isAllday()) {
                    log.error("Selfassignment is in no-delete mode.");
                    throw new InvalidRequestException(ErrorMessage.SELFASSIGNMENT_NODELETE);
                }
            }
        }
    }

    private void manageTandemmasterAssignment(Jumpday jumpday, Tandemmaster tandemmaster, SimpleAssignment simpleAssignment) {
        Optional<Assignment<Tandemmaster>> foundAssignment = jumpday.getTandemmaster().stream()
                .filter(t -> t != null && t.getFlyer() != null && t.getFlyer().getId().equals(tandemmaster.getId()))
                .findFirst();

        Assignment<Tandemmaster> assignment = assignmentMapper.toAssignment(simpleAssignment, tandemmaster);

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
