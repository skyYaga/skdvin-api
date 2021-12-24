package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.config.Claims;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.InvalidRequestException;
import in.skdv.skdvinbackend.model.converter.TandemmasterConverter;
import in.skdv.skdvinbackend.model.dto.TandemmasterDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterDetailsInputDTO;
import in.skdv.skdvinbackend.model.dto.TandemmasterInputDTO;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.model.entity.TandemmasterDetails;
import in.skdv.skdvinbackend.service.ITandemmasterService;
import in.skdv.skdvinbackend.util.GenericResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tandemmaster")
@RequiredArgsConstructor
public class TandemmasterController {

    private final ITandemmasterService tandemmasterService;
    private final TandemmasterConverter tandemmasterConverter = new TandemmasterConverter();

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_create:tandemmaster')")
    public ResponseEntity<GenericResult<TandemmasterDTO>> addTandemmaster(@RequestBody @Valid TandemmasterDTO input) {
        log.info("Adding tandemmaster {}", input);
        Tandemmaster convertedInput = tandemmasterConverter.convertToEntity(input);
        Tandemmaster tandemmaster = tandemmasterService.save(convertedInput);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResult<>(true, tandemmasterConverter.convertToDto(tandemmaster)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read:tandemmaster')")
    public GenericResult<List<TandemmasterDTO>> getAllTandemmasters() {
        log.info("Getting all tandemmasters");
        List<Tandemmaster> tandemmasters = tandemmasterService.findAll();
        return new GenericResult<>(true, tandemmasterConverter.convertToDto(tandemmasters));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_read:tandemmaster')")
    public GenericResult<TandemmasterDetailsDTO> getTandemmaster(@PathVariable String id) {
        log.info("Getting tandemmaster by id {}", id);
        TandemmasterDetails tandemmaster = tandemmasterService.getById(id);
        return new GenericResult<>(true, tandemmasterConverter.convertToDto(tandemmaster));
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasAuthority('SCOPE_tandemmaster')")
    public GenericResult<TandemmasterDetailsDTO> getMeTandemmaster() {
        log.info("Getting me tandemmaster");
        TandemmasterDetails tandemmaster = tandemmasterService.getByEmail(Claims.getEmail());
        return new GenericResult<>(true, tandemmasterConverter.convertToDto(tandemmaster));
    }

    @PatchMapping(value = "/me/assign")
    @PreAuthorize("hasAuthority('SCOPE_tandemmaster')")
    public GenericResult<Void> selfAssignTandemmasterToJumpdays(@RequestBody @Valid TandemmasterDetailsDTO input) {
        log.info("Self assigning tandemmaster to jumpdays {}", input);
        if (input.getEmail() == null || !input.getEmail().equals(Claims.getEmail())) {
            log.error("Input email doesn't match claims email");
            throw new InvalidRequestException(ErrorMessage.SELFASSIGNMENT_NODELETE);
        }

        tandemmasterService.assignTandemmaster(tandemmasterConverter.convertToEntity(input), true);
        return new GenericResult<>(true);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_update:tandemmaster')")
    public GenericResult<TandemmasterDTO> updateTandemmaster(@PathVariable String id, @RequestBody @Valid TandemmasterInputDTO input) {
        log.info("Updating tandemmaster {}: {}", id, input);
        Tandemmaster savedTandemmaster = tandemmasterService.updateTandemmaster(tandemmasterConverter.convertToEntity(id, input));
        return new GenericResult<>(true, tandemmasterConverter.convertToDto(savedTandemmaster));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_delete:tandemmaster')")
    public GenericResult<Void> deleteTandemmaster(@PathVariable String id) {
        log.info("Deleting tandemmaster with id {}", id);
        tandemmasterService.delete(id);
        return new GenericResult<>(true);
    }


    @PatchMapping(value = "/{id}/assign")
    @PreAuthorize("hasAuthority('SCOPE_update:tandemmaster')")
    public GenericResult<Void> assignTandemmasterToJumpdays(
            @PathVariable @NotNull @Valid String id,
            @RequestBody @Valid TandemmasterDetailsInputDTO input) {
        log.info("Assigning tandemmaster {} to jumpdays {}", id, input);
        tandemmasterService.assignTandemmaster(tandemmasterConverter.convertToEntity(id, input), false);
        return new GenericResult<>(true);
    }

}
