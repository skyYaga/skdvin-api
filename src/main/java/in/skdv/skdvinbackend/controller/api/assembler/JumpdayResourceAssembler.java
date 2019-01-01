package in.skdv.skdvinbackend.controller.api.assembler;

import in.skdv.skdvinbackend.controller.api.JumpdayController;
import in.skdv.skdvinbackend.model.dto.JumpdayDTO;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class JumpdayResourceAssembler implements ResourceAssembler<JumpdayDTO, Resource<JumpdayDTO>> {

    /**
     * Converts the given entity into an {@link ResourceSupport}.
     *
     * @param jumpday The Jumpday
     * @return Jumpday converted to resource
     */
    @Override
    public Resource<JumpdayDTO> toResource(JumpdayDTO jumpday) {
        return new Resource<>(jumpday,
                linkTo(methodOn(JumpdayController.class).readJumpday(jumpday.getDate().toString())).withSelfRel(),
                linkTo(methodOn(JumpdayController.class).readJumpdays()).withRel("jumpdays"));
    }
}
