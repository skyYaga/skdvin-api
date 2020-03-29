package in.skdv.skdvinbackend.service.impl;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.common.SimpleAssignment;
import in.skdv.skdvinbackend.model.converter.AssignmentConverter;
import in.skdv.skdvinbackend.model.converter.VideoflyerConverter;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Assignment;
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
    private VideoflyerConverter videoflyerConverter = new VideoflyerConverter();
    private AssignmentConverter assignmentConverter = new AssignmentConverter();

    @Autowired
    public MongoVideoflyerService(JumpdayRepository jumpdayRepository, VideoflyerRepository videoflyerRepository) {
        this.jumpdayRepository = jumpdayRepository;
        this.videoflyerRepository = videoflyerRepository;
    }

    @Override
    public VideoflyerDetailsDTO getById(String id) {
        Map<LocalDate, SimpleAssignment> assignments = new HashMap<>();

        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(id);
        if (videoflyer.isEmpty()) {
            return null;
        }

        jumpdayRepository.findAll().forEach(j -> {
            Optional<Assignment<Videoflyer>> localAssignment = j.getVideoflyer().stream().filter(t -> t.getFlyer().getId().equals(id)).findFirst();
            localAssignment.ifPresent(assignment -> assignments.put(j.getDate(), assignmentConverter.convertToSimpleAssignment(assignment)));
        });

        return videoflyerConverter.convertToDetailsDto(videoflyer.get(), assignments);
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
    public GenericResult<Void> assignVideoflyer(VideoflyerDetailsDTO videoflyerDetails) {
        for (LocalDate date : videoflyerDetails.getAssignments().keySet()) {
            GenericResult<Void> result = assignVideoflyerToJumpday(date, videoflyerDetails.getId(), videoflyerDetails.getAssignments().get(date));
            if (!result.isSuccess()) {
                return new GenericResult<>(false, result.getMessage());
            }
        }
        return new GenericResult<>(true);
    }

    private void manageVideoflyerAssignment(Jumpday jumpday, Videoflyer tandemmaster, SimpleAssignment simpleAssignment) {
        Optional<Assignment<Videoflyer>> foundAssignment = jumpday.getVideoflyer().stream()
                .filter(t -> t != null && t.getFlyer().getId().equals(tandemmaster.getId()))
                .findFirst();

        Assignment<Videoflyer> assignment = assignmentConverter.convertToAssignment(simpleAssignment, tandemmaster);

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
