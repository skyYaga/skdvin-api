package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.model.converter.WaiverConverter;
import in.skdv.skdvinbackend.model.dto.WaiverDTO;
import in.skdv.skdvinbackend.model.dto.WaiverInputDTO;
import in.skdv.skdvinbackend.model.entity.waiver.Waiver;
import in.skdv.skdvinbackend.service.IWaiverService;
import in.skdv.skdvinbackend.util.GenericResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/waivers")
@RequiredArgsConstructor
public class WaiverController {

    private final WaiverConverter waiverConverter = new WaiverConverter();
    private final IWaiverService waiverService;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read:waivers')")
    public GenericResult<List<WaiverDTO>> getAllWaivers() {
        log.info("Getting all waivers");
        List<Waiver> waiverList = waiverService.getWaivers();
        return new GenericResult<>(true, waiverConverter.convertToDto(waiverList));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll")
    public GenericResult<WaiverDTO> saveWaiver(@RequestBody @Valid WaiverDTO input) {
        log.info("Saving waiver {}", input);
        Waiver waiverResult = waiverService.saveWaiver(waiverConverter.convertToEntity(input));
        return new GenericResult<>(true, waiverConverter.convertToDto(waiverResult));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_update:waivers')")
    public GenericResult<WaiverDTO> updateWaiver(@PathVariable String id, @RequestBody @Valid WaiverInputDTO input) {
        log.info("Updating waiver {}", input);
        Waiver waiverResult = waiverService.updateWaiver(waiverConverter.convertToEntity(id, input));
        return new GenericResult<>(true, waiverConverter.convertToDto(waiverResult));
    }

}
