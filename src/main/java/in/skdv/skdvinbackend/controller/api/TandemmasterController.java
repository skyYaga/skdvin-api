package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.model.converter.TandemmasterConverter;
import in.skdv.skdvinbackend.model.dto.TandemmasterDTO;
import in.skdv.skdvinbackend.model.entity.Tandemmaster;
import in.skdv.skdvinbackend.repository.TandemmasterRepository;
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
@RequestMapping("/api/tandemmaster")
public class TandemmasterController {

    private final TandemmasterRepository tandemmasterRepository;
    private TandemmasterConverter tandemmasterConverter = new TandemmasterConverter();
    private MessageSource messageSource;

    @Autowired
    public TandemmasterController(TandemmasterRepository tandemmasterRepository, MessageSource messageSource) {
        this.tandemmasterRepository = tandemmasterRepository;
        this.messageSource = messageSource;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_create:tandemmaster')")
    public ResponseEntity<GenericResult<TandemmasterDTO>> addTandemmaster(@RequestBody @Valid TandemmasterDTO input) {
        Tandemmaster convertedInput = tandemmasterConverter.convertToEntity(input);
        Tandemmaster tandemmaster = tandemmasterRepository.save(convertedInput);

        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new GenericResult<>(true, tandemmasterConverter.convertToDto(tandemmaster)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read:tandemmaster')")
    public ResponseEntity<GenericResult<List<TandemmasterDTO>>> getTandemmaster() {
        List<Tandemmaster> tandemmasters = tandemmasterRepository.findAll();

        return ResponseEntity.ok(new GenericResult<>(true, tandemmasterConverter.convertToDto(tandemmasters)));
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_update:tandemmaster')")
    public ResponseEntity<GenericResult<TandemmasterDTO>> updateTandemmaster(@PathVariable String id, @RequestBody @Valid TandemmasterDTO input) {
        Optional<Tandemmaster> tandemmaster = tandemmasterRepository.findById(id);

        if (tandemmaster.isPresent() && input.getId().equals(id)) {
            Tandemmaster savedTandemmaster = tandemmasterRepository.save(tandemmasterConverter.convertToEntity(input));
            return ResponseEntity.ok(new GenericResult<>(true, tandemmasterConverter.convertToDto(savedTandemmaster)));

        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new GenericResult<>(false, messageSource.getMessage(ErrorMessage.TANDEMMASTER_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('SCOPE_delete:tandemmaster')")
    public ResponseEntity<GenericResult<Void>> deleteTandemmaster(@PathVariable String id) {
        Optional<Tandemmaster> tandemmaster = tandemmasterRepository.findById(id);

        if (tandemmaster.isPresent()) {
            tandemmasterRepository.deleteById(id);
            return ResponseEntity.ok(new GenericResult<>(true));

        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new GenericResult<>(false, messageSource.getMessage(ErrorMessage.TANDEMMASTER_NOT_FOUND.toString(), null, LocaleContextHolder.getLocale())));
    }
}
