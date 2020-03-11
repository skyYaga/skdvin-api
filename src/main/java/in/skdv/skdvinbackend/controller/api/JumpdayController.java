package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.exception.ErrorMessage;
import in.skdv.skdvinbackend.exception.JumpdayExistsException;
import in.skdv.skdvinbackend.exception.JumpdayInternalException;
import in.skdv.skdvinbackend.model.converter.JumpdayConverter;
import in.skdv.skdvinbackend.model.dto.JumpdayDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/jumpday")
public class JumpdayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JumpdayController.class);

    private IJumpdayService jumpdayService;
    private MessageSource messageSource;
    private JumpdayConverter jumpdayConverter = new JumpdayConverter();

    @Autowired
    public JumpdayController(IJumpdayService jumpdayService, MessageSource messageSource) {
        this.jumpdayService = jumpdayService;
        this.messageSource = messageSource;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read:jumpdays')")
    public ResponseEntity<GenericResult> readJumpdays() {
        GenericResult<List<Jumpday>> result = jumpdayService.findJumpdays();
        if (result.isSuccess()) {
            return ResponseEntity.ok(new GenericResult<>(true, jumpdayConverter.convertToDto(result.getPayload())));
        }

        LOGGER.warn("Error reading Jumpdays: {}", result.getMessage());
        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
    }

    @GetMapping(value = "/{jumpdayDate}")
    @PreAuthorize("hasAuthority('SCOPE_read:jumpdays')")
    public ResponseEntity<GenericResult> readJumpday(@PathVariable String jumpdayDate) {

        GenericResult<Jumpday> result = jumpdayService.findJumpday(LocalDate.parse(jumpdayDate));
        if (result.isSuccess()) {
            return ResponseEntity.ok(new GenericResult<>(true, jumpdayConverter.convertToDto(result.getPayload())));
        }

        if (result.getMessage().equals(ErrorMessage.JUMPDAY_NOT_FOUND_MSG.toString())) {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                    .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
        }

        LOGGER.warn("Error reading Jumpday: {}", result.getMessage());
        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_create:jumpdays')")
    public ResponseEntity<GenericResult> addJumpday(@RequestBody @Valid JumpdayDTO input, HttpServletResponse response) {
        GenericResult<Jumpday> result = jumpdayService.saveJumpday(jumpdayConverter.convertToEntity(input));

        if (result.isSuccess()) {
            return ResponseEntity.status(HttpServletResponse.SC_CREATED)
                    .body(new GenericResult<>(true, jumpdayConverter.convertToDto(result.getPayload())));
        }

        if (result.getMessage().equals(ErrorMessage.JUMPDAY_ALREADY_EXISTS_MSG.toString())) {
            LOGGER.warn("Jumpday already exists: {}", input.getDate().toString().replaceAll("[\n|\r|\t]", "_"));
            throw new JumpdayExistsException(result.getMessage());
        }

        LOGGER.error("Error adding jumpday: {}", result.getMessage());
        throw new JumpdayInternalException(result.getMessage());
    }
}
