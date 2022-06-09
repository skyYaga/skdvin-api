package in.skdv.skdvinbackend.migration.document;

import in.skdv.skdvinbackend.model.entity.settings.AdminSettings;
import in.skdv.skdvinbackend.model.entity.settings.Dropzone;
import in.skdv.skdvinbackend.model.entity.settings.Faq;
import in.skdv.skdvinbackend.model.entity.settings.SelfAssignmentMode;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class OldSettings {

    @Id
    private String id;
    private AdminSettings adminSettings;
    private Map<String, CommonSettings> commonSettings;

    @Data
    public static class CommonSettings {
        private Dropzone dropzone;
        private List<Faq> faq = new ArrayList<>();
        private String homepageHint = "";
        private String homepageHintTitle = "";
        private String bccMail = "";
        private String additionalReminderHint = "";
        private SelfAssignmentMode selfAssignmentMode = SelfAssignmentMode.WRITE_DELETE;
    }

}
