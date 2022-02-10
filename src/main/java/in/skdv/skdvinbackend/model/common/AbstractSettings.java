package in.skdv.skdvinbackend.model.common;

import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.CommonSettings;
import lombok.Data;

import java.util.Map;

@Data
public class AbstractSettings {

    private AdminSettings adminSettings;
    private Map<String, CommonSettings> commonSettings;

}
