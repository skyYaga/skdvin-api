package in.skdv.skdvinbackend.model.entity.settings;

import in.skdv.skdvinbackend.model.common.AbstractSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@EqualsAndHashCode(callSuper = true)
public class Settings extends AbstractSettings {

    @Id
    private String id;

}