package in.skdv.skdvinbackend.model.entity.waiver;

import in.skdv.skdvinbackend.model.common.waiver.AbstractWaiver;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class Waiver extends AbstractWaiver {

    @Id
    private String id;

}
