package in.skdv.skdvinbackend.model.dto;

import in.skdv.skdvinbackend.model.common.AbstractSettings;

public class SettingsDTO extends AbstractSettings {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
