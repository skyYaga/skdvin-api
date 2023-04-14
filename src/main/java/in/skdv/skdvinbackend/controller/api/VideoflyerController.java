package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.config.Claims;
import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.InvalidRequestException;
import in.skdv.skdvinbackend.model.dto.VideoflyerDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsInputDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerInputDTO;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.model.entity.VideoflyerDetails;
import in.skdv.skdvinbackend.model.mapper.VideoflyerMapper;
import in.skdv.skdvinbackend.service.IVideoflyerService;
import in.skdv.skdvinbackend.util.GenericResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/videoflyer")
@RequiredArgsConstructor
public class VideoflyerController {

    private final VideoflyerMapper videoflyerMapper;
    private final IVideoflyerService videoflyerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCOPE_create:videoflyer')")
    public GenericResult<VideoflyerDTO> addVideoflyer(@RequestBody @Valid VideoflyerDTO input) {
        log.info("Adding videoflyer {}", input);
        Videoflyer convertedInput = videoflyerMapper.toEntity(input);
        Videoflyer videoflyer = videoflyerService.save(convertedInput);
        return new GenericResult<>(true, videoflyerMapper.toDto(videoflyer));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read:videoflyer')")
    public GenericResult<List<VideoflyerDTO>> getAllVideoflyers() {
        log.info("Getting all videoflyers");
        List<Videoflyer> videoflyers = videoflyerService.findAll();
        return new GenericResult<>(true, videoflyerMapper.toDto(videoflyers));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_read:videoflyer')")
    public GenericResult<VideoflyerDetailsDTO> getVideoflyer(@PathVariable String id) {
        log.info("Getting videoflyer by id {}", id);
        VideoflyerDetails videoflyer = videoflyerService.getById(id);
        return new GenericResult<>(true, videoflyerMapper.toDto(videoflyer));
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasAuthority('SCOPE_videoflyer')")
    public GenericResult<VideoflyerDetailsDTO> getMeVideoflyer() {
        log.info("Getting me videoflyer");
        VideoflyerDetails videoflyer = videoflyerService.getByEmail(Claims.getEmail());
        return new GenericResult<>(true, videoflyerMapper.toDto(videoflyer));
    }

    @PatchMapping(value = "/me/assign")
    @PreAuthorize("hasAuthority('SCOPE_videoflyer')")
    public GenericResult<Void> selfAssignVideoflyerToJumpdays(@RequestBody @Valid VideoflyerDetailsDTO input) {
        log.info("Self assigning videoflyer to jumpdays {}", input);
        if (input.getEmail() == null || !input.getEmail().equals(Claims.getEmail())) {
            log.error("Input email doesn't match claims email");
            throw new InvalidRequestException(ErrorMessage.SELFASSIGNMENT_NODELETE);
        }

        videoflyerService.assignVideoflyer(videoflyerMapper.toEntity(input), true);
        return new GenericResult<>(true);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_update:videoflyer')")
    public GenericResult<VideoflyerDTO> updateVideoflyer(@PathVariable String id, @RequestBody @Valid VideoflyerInputDTO input) {
        log.info("Updating videoflyer {}: {}", id, input);
        Videoflyer savedVideoflyer = videoflyerService.updateVideoflyer(videoflyerMapper.toEntity(id, input));
        return new GenericResult<>(true, videoflyerMapper.toDto(savedVideoflyer));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_delete:videoflyer')")
    public GenericResult<Void> deleteVideoflyer(@PathVariable String id) {
        log.info("Deleting videoflyer {}", id);
        videoflyerService.delete(id);
        return new GenericResult<>(true);
    }


    @PatchMapping(value = "/{id}/assign")
    @PreAuthorize("hasAuthority('SCOPE_update:videoflyer')")
    public GenericResult<Void> assignVideoflyerToJumpdays(
            @PathVariable @NotNull @Valid String id,
            @RequestBody @Valid VideoflyerDetailsInputDTO input) {
        log.info("Assigning videoflyer {} to jumpdays {}", id, input);
        videoflyerService.assignVideoflyer(videoflyerMapper.toEntity(id, input), false);
        return new GenericResult<>(true);
    }

}
