package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.model.dto.WaiverDTO;
import in.skdv.skdvinbackend.service.IWaiverService;
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

@RestController
@RequestMapping("/api/waivers")
public class WaiverController {

    private IWaiverService waiverService;
    private MessageSource messageSource;

    @Autowired
    public WaiverController(IWaiverService waiverService, MessageSource messageSource) {
        this.waiverService = waiverService;
        this.messageSource = messageSource;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_read:waivers')")
    public ResponseEntity<GenericResult<List<WaiverDTO>>> getAllWaivers() {
        List<WaiverDTO> waiverList = waiverService.getWaivers();
        return ResponseEntity.ok(new GenericResult<>(true, waiverList));
    }

    @PostMapping
    public ResponseEntity<GenericResult<WaiverDTO>> saveWaiver(@RequestBody @Valid WaiverDTO input) {
        GenericResult<WaiverDTO> waiverResult = waiverService.saveWaiver(input);
        if (waiverResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(waiverResult);
        }
        return ResponseEntity.badRequest().body(new GenericResult<>(false,
                messageSource.getMessage(waiverResult.getMessage(), null, LocaleContextHolder.getLocale())));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_update:waivers')")
    public ResponseEntity<GenericResult<WaiverDTO>> updateWaiver(@RequestBody @Valid WaiverDTO input) {
        GenericResult<WaiverDTO> waiverResult = waiverService.updateWaiver(input);
        if (waiverResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(waiverResult);
        }
        return ResponseEntity.badRequest().body(new GenericResult<>(false,
                messageSource.getMessage(waiverResult.getMessage(), null, LocaleContextHolder.getLocale())));
    }

}
