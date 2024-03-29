package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.model.dto.JumpdayDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.model.mapper.JumpdayMapper;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.service.ITimezoneService;
import in.skdv.skdvinbackend.util.GenericResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/jumpday")
@RequiredArgsConstructor
@Validated
public class JumpdayController {

    private static final String YEAR_MONTH_PATTERN = "^[0-9]{4}-[0-9]{2}$";
    private static final String DATE_PATTERN = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$";

    private final IJumpdayService jumpdayService;
    private final JumpdayMapper jumpdayMapper;
    private final ITimezoneService timezoneService;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read:jumpdays')")
    public GenericResult<List<JumpdayDTO>> readJumpdays(
            @RequestParam(required = false) @Pattern(regexp = YEAR_MONTH_PATTERN) String month) {
        log.info("Reading jumpdays");
        if (month != null) {
            List<Jumpday> jumpdaysByMonth = jumpdayService.findJumpdaysByMonth(YearMonth.parse(month));
            return new GenericResult<>(true, jumpdayMapper.toDto(jumpdaysByMonth));
        }
        return new GenericResult<>(true, jumpdayMapper.toDto(jumpdayService.findJumpdays()));
    }

    @GetMapping(value = "/{jumpdayDate}")
    @PreAuthorize("hasAuthority('SCOPE_read:jumpdays')")
    public GenericResult<JumpdayDTO> readJumpday(@PathVariable @Pattern(regexp = DATE_PATTERN) String jumpdayDate) {
        log.info("Reading jumpday {}", jumpdayDate);
        Jumpday jumpday = jumpdayService.findJumpday(LocalDate.parse(jumpdayDate));
        return new GenericResult<>(true, jumpdayMapper.toDto(jumpday));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCOPE_create:jumpdays')")
    public GenericResult<JumpdayDTO> addJumpday(@RequestBody @Valid JumpdayDTO input) {
        log.info("Adding jumpday {}", input);
        Jumpday result = jumpdayService.saveJumpday(jumpdayMapper.toEntity(input, timezoneService.getZoneId()));
        return new GenericResult<>(true, jumpdayMapper.toDto(result));
    }

    @PutMapping(value = "/{jumpdayDate}")
    @PreAuthorize("hasAuthority('SCOPE_update:jumpdays')")
    public GenericResult<JumpdayDTO> updateJumpday(@PathVariable @Pattern(regexp = DATE_PATTERN) String jumpdayDate, @RequestBody @Valid JumpdayDTO input) {
        log.info("Updating jumpday {}, {}", jumpdayDate, input);
        Jumpday updatedJumpday = jumpdayService.updateJumpday(LocalDate.parse(jumpdayDate), jumpdayMapper.toEntity(input, timezoneService.getZoneId()));
        return new GenericResult<>(true, jumpdayMapper.toDto(updatedJumpday));
    }

    @DeleteMapping(value = "/{jumpdayDate}")
    @PreAuthorize("hasAuthority('SCOPE_update:jumpdays')")
    public GenericResult<Void> deleteJumpday(@PathVariable @Pattern(regexp = DATE_PATTERN)  String jumpdayDate) {
        log.info("Deleting jumpday {}", jumpdayDate);
        jumpdayService.deleteJumpday(LocalDate.parse(jumpdayDate));
        return new GenericResult<>(true);
    }
}
