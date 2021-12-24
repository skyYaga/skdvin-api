package in.skdv.skdvinbackend.model.entity.waiver;

import in.skdv.skdvinbackend.model.common.waiver.AbstractWaiver;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document
@Data
@EqualsAndHashCode(callSuper = true)
public class Waiver extends AbstractWaiver {

    @Id
    private String id;

    @NotNull
    private String waiverText;

}
