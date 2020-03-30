package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.converter.VideoflyerConverter;
import in.skdv.skdvinbackend.model.dto.VideoflyerDTO;
import in.skdv.skdvinbackend.model.dto.VideoflyerDetailsDTO;
import in.skdv.skdvinbackend.model.entity.Videoflyer;
import in.skdv.skdvinbackend.repository.VideoflyerRepository;
import in.skdv.skdvinbackend.service.IVideoflyerService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/videoflyer")
public class VideoflyerController {

    private final VideoflyerRepository videoflyerRepository;
    private VideoflyerConverter videoflyerConverter = new VideoflyerConverter();
    private IVideoflyerService videoflyerService;
    private MessageSource messageSource;

    @Autowired
    public VideoflyerController(VideoflyerRepository videoflyerRepository, IVideoflyerService videoflyerService, MessageSource messageSource) {
        this.videoflyerRepository = videoflyerRepository;
        this.videoflyerService = videoflyerService;
        this.messageSource = messageSource;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_create:videoflyer')")
    public ResponseEntity<GenericResult<VideoflyerDTO>> addVideoflyer(@RequestBody @Valid VideoflyerDTO input) {
        Videoflyer convertedInput = videoflyerConverter.convertToEntity(input);
        Videoflyer videoflyer = videoflyerRepository.save(convertedInput);

        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new GenericResult<>(true, videoflyerConverter.convertToDto(videoflyer)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read:videoflyer')")
    public ResponseEntity<GenericResult<List<VideoflyerDTO>>> getAllVideoflyers() {
        List<Videoflyer> videoflyers = videoflyerRepository.findAll();

        return ResponseEntity.ok(new GenericResult<>(true, videoflyerConverter.convertToDto(videoflyers)));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_read:videoflyer')")
    public ResponseEntity<GenericResult<VideoflyerDetailsDTO>> getVideoflyer(@PathVariable String id) {
        VideoflyerDetailsDTO videoflyer = videoflyerService.getById(id);

        if (videoflyer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResult<>(false, messageSource.getMessage(ErrorMessage.VIDEOFLYER_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));
        }

        return ResponseEntity.ok(new GenericResult<>(true, videoflyer));
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_update:videoflyer')")
    public ResponseEntity<GenericResult<VideoflyerDTO>> updateVideoflyer(@PathVariable String id, @RequestBody @Valid VideoflyerDTO input) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(id);

        if (videoflyer.isPresent() && input.getId().equals(id)) {
            Videoflyer savedVideoflyer = videoflyerRepository.save(videoflyerConverter.convertToEntity(input));
            return ResponseEntity.ok(new GenericResult<>(true, videoflyerConverter.convertToDto(savedVideoflyer)));

        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new GenericResult<>(false, messageSource.getMessage(ErrorMessage.VIDEOFLYER_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_delete:videoflyer')")
    public ResponseEntity<GenericResult<Void>> deleteVideoflyer(@PathVariable String id) {
        Optional<Videoflyer> videoflyer = videoflyerRepository.findById(id);

        if (videoflyer.isPresent()) {
            videoflyerService.delete(id);
            return ResponseEntity.ok(new GenericResult<>(true));

        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new GenericResult<>(false, messageSource.getMessage(ErrorMessage.VIDEOFLYER_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));
    }


    @PatchMapping(value = "/{id}/assign")
    @PreAuthorize("hasAuthority('SCOPE_update:videoflyer')")
    public ResponseEntity<GenericResult<Void>> assignVideoflyerToJumpdays(@PathVariable String id, @RequestBody @Valid VideoflyerDetailsDTO input) {
        if (!id.equals(input.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GenericResult<>(false));
        }

        GenericResult<Void> result = videoflyerService.assignVideoflyer(input);

        if (result.isSuccess()) {
            return ResponseEntity.ok(new GenericResult<>(true));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new GenericResult<>(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
    }
}
