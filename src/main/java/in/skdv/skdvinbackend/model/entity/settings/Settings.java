package in.skdv.skdvinbackend.model.entity.settings;

import in.skdv.skdvinbackend.model.common.AbstractSettings;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Settings extends AbstractSettings {

    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
