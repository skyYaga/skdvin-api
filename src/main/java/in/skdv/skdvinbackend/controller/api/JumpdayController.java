package in.skdv.skdvinbackend.controller.api;

import in.skdv.skdvinbackend.controller.api.assembler.JumpdayResourceAssembler;
import in.skdv.skdvinbackend.model.dto.JumpdayDTO;
import in.skdv.skdvinbackend.model.entity.Jumpday;
import in.skdv.skdvinbackend.service.IJumpdayService;
import in.skdv.skdvinbackend.util.GenericResult;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/api/jumpday")
public class JumpdayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JumpdayController.class);

    private IJumpdayService jumpdayService;
    private MessageSource messageSource;
    private ModelMapper modelMapper = new ModelMapper();
    private JumpdayResourceAssembler assembler;

    @Autowired
    public JumpdayController(IJumpdayService jumpdayService, MessageSource messageSource, JumpdayResourceAssembler assembler) {
        this.jumpdayService = jumpdayService;
        this.messageSource = messageSource;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<GenericResult> readJumpdays() {
        GenericResult<List<Jumpday>> result = jumpdayService.findJumpdays();
        if (result.isSuccess()) {
            return ResponseEntity.ok(new GenericResult<>(true, convertToDto(result.getPayload())));
        }

        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
    }

    @GetMapping(value = "/{jumpdayDate}")
    public ResponseEntity<GenericResult> readJumpday(@PathVariable String jumpdayDate) {
        GenericResult<Jumpday> result = jumpdayService.findJumpday(LocalDate.parse(jumpdayDate));
        if (result.isSuccess()) {
            return ResponseEntity.ok(new GenericResult<>(true, convertToDto(result.getPayload())));
        }

        if (result.getMessage().equals("jumpday.not.found")) {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                    .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
        }

        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
    }

    @PostMapping
    ResponseEntity<?> addJumpday(@RequestBody JumpdayDTO input, HttpServletResponse response) throws URISyntaxException {
        GenericResult<Jumpday> result = jumpdayService.saveJumpday(convertToEntity(input));

        if (result.isSuccess()) {
            Resource<Jumpday> jumpdayResource = assembler.toResource(result.getPayload());
            return ResponseEntity
                    .created(new URI(jumpdayResource.getId().expand().getHref()))
                    .body(jumpdayResource);
        }

        if (result.getMessage().equals("jumpday.already.exists")) {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                    .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
        }

        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                .body(new GenericResult(false, messageSource.getMessage(result.getMessage(), null, LocaleContextHolder.getLocale())));
    }


    private JumpdayDTO convertToDto(Jumpday jumpday) {
        if (jumpday == null) {
            return null;
        }
        return modelMapper.map(jumpday, JumpdayDTO.class);
    }

    private List<JumpdayDTO> convertToDto(List<Jumpday> jumpdays) {
        if (jumpdays == null) {
            return Collections.emptyList();
        }
        List<JumpdayDTO> jumpdayDTOList = new ArrayList<>();
        jumpdays.forEach(a -> jumpdayDTOList.add(this.convertToDto(a)));
        return jumpdayDTOList;
    }

    private Jumpday convertToEntity(JumpdayDTO jumpdayDTO) {
        if (jumpdayDTO == null) {
            return null;
        }
        return modelMapper.map(jumpdayDTO, Jumpday.class);
    }
}
